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

# Extract key ID from the .asc file (portable: uses gpg colon format)
KEY_ID=$(gpg --show-keys --with-colons --keyid-format=long "$ASC_FILE" 2>/dev/null | awk -F: '/^pub:/{print $5; exit}')

if [[ -z "$KEY_ID" ]]; then
    KEY_ID=$(gpg --show-keys --with-colons "$ASC_FILE" 2>/dev/null | awk -F: '/^fpr:/{print $10; exit}')
fi

if [[ -z "$KEY_ID" ]]; then
    echo "Error: Could not extract key ID from $ASC_FILE"
    exit 1
fi

echo "Key ID: $KEY_ID"

# Prompt for password (hidden input)
read -rsp "Enter PGP key password: " KEY_PASSWORD
echo

# Read key content
KEY_CONTENT=$(cat "$ASC_FILE")

# Set GitHub secrets
echo "Setting SIGNING_IN_MEMORY_KEY..."
echo "$KEY_CONTENT" | gh secret set SIGNING_IN_MEMORY_KEY --repo "$REPO"

echo "Setting SIGNING_KEY_ID..."
echo "$KEY_ID" | gh secret set SIGNING_KEY_ID --repo "$REPO"

echo "Setting SIGNING_PASSWORD..."
echo "$KEY_PASSWORD" | gh secret set SIGNING_PASSWORD --repo "$REPO"

echo ""
echo "All signing secrets set successfully for $REPO."
