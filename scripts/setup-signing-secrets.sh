#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
    echo "Usage: $0 <path-to-private-key.asc>"
    exit 1
fi

ASC_FILE="$1"

if [[ ! -f "$ASC_FILE" ]]; then
    echo "Error: File not found: $ASC_FILE"
    exit 1
fi

REPO="hdcodedev/matrix-kmp"

# Gradle's PgpKeyId accepts only the short 8-character key ID (or 0x plus
# those 8 characters), not a long key ID or a full fingerprint. Use GnuPG's
# dry-run import mode because secret-key input may expose a `sec` record rather
# than a `pub` record through `--show-keys` on macOS.
KEY_ID=$(gpg --batch --no-options --with-colons --keyid-format=long \
    --import-options show-only --import "$ASC_FILE" 2>/dev/null | \
    awk -F: '
        ($1 == "sec" || $1 == "pub") && $5 != "" {
            print substr($5, length($5) - 7)
            found = 1
            exit
        }
        $1 == "fpr" && $10 != "" && fingerprint == "" {
            fingerprint = $10
        }
        END {
            if (!found && fingerprint != "") {
                print substr(fingerprint, length(fingerprint) - 7)
            }
        }
    ')

# Bash 3.2 (the macOS system Bash) does not support ${value^^}.
KEY_ID="$(printf '%s' "$KEY_ID" | tr '[:lower:]' '[:upper:]')"

if [[ ! "$KEY_ID" =~ ^[0-9A-F]{8}$ ]]; then
    echo "Error: Could not extract a valid 8-character key ID from $ASC_FILE" >&2
    exit 1
fi

echo "Key ID: $KEY_ID"

# Prompt for password (hidden input)
read -rsp "Enter PGP key password: " KEY_PASSWORD
echo

# Set GitHub secrets
echo "Setting SIGNING_IN_MEMORY_KEY..."
gh secret set SIGNING_IN_MEMORY_KEY --repo "$REPO" < "$ASC_FILE"

echo "Setting SIGNING_KEY_ID..."
printf '%s\n' "$KEY_ID" | gh secret set SIGNING_KEY_ID --repo "$REPO"

echo "Setting SIGNING_PASSWORD..."
printf '%s\n' "$KEY_PASSWORD" | gh secret set SIGNING_PASSWORD --repo "$REPO"

echo ""
echo "All signing secrets set successfully for $REPO."
