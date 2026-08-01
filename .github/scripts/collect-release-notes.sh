#!/usr/bin/env bash
set -euo pipefail

script_path="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/$(basename "${BASH_SOURCE[0]}")"

collect_release_notes() {
  local version="$1"
  local notes_dir="release-notes/${version}/changes"

  if [[ ! -d "${notes_dir}" ]]; then
    return
  fi

  local first=true
  local f
  local content
  for f in "${notes_dir}"/*.md; do
    if [[ -f "${f}" ]]; then
      content="$(cat "${f}")"
      if [[ "${first}" == "true" ]]; then
        first=false
      else
        printf '\n\n'
      fi
      printf '%s' "${content}"
    fi
  done
}

run_self_test() (
  local tmpdir
  tmpdir="$(mktemp -d)"
  trap 'rm -rf "${tmpdir}"' EXIT

  mkdir -p "${tmpdir}/release-notes/1.0.0/changes"
  mkdir -p "${tmpdir}/release-notes/1.1.0/changes"

  cat > "${tmpdir}/release-notes/1.0.0/changes/100-add-feature.md" <<'EOF'
## Add feature X

This is the first feature.
EOF

  cat > "${tmpdir}/release-notes/1.0.0/changes/101-fix-bug.md" <<'EOF'
## Fix bug Y

This fixes the bug.
EOF

  cat > "${tmpdir}/release-notes/1.1.0/changes/200-improve-z.md" <<'EOF'
## Improve Z

Improvement details.
EOF

  pushd "${tmpdir}" >/dev/null

  local output
  local expected

  output="$("${script_path}" "1.0.0")"
  expected="$(cat <<'EOF'
## Add feature X

This is the first feature.

## Fix bug Y

This fixes the bug.
EOF
)"
  if [[ "${output}" != "${expected}" ]]; then
    echo "FAIL: collect-release-notes.sh output for 1.0.0 does not match expected." >&2
    echo "Expected:" >&2
    echo "${expected}" >&2
    echo "Got:" >&2
    echo "${output}" >&2
    return 1
  fi

  output="$("${script_path}" "1.1.0")"
  expected="$(cat <<'EOF'
## Improve Z

Improvement details.
EOF
)"
  if [[ "${output}" != "${expected}" ]]; then
    echo "FAIL: collect-release-notes.sh output for 1.1.0 does not match expected." >&2
    echo "Expected:" >&2
    echo "${expected}" >&2
    echo "Got:" >&2
    echo "${output}" >&2
    return 1
  fi

  output="$("${script_path}" "2.0.0")"
  if [[ -n "${output}" ]]; then
    echo "FAIL: collect-release-notes.sh should produce no output for missing version." >&2
    return 1
  fi

  popd >/dev/null
  echo "All self-tests passed."
)

main() {
  if [[ "${1:-}" == "--self-test" ]]; then
    run_self_test
    return
  fi

  if [[ $# -ne 1 ]]; then
    echo "Usage: $0 <version>" >&2
    exit 1
  fi

  collect_release_notes "$1"
}

main "$@"
