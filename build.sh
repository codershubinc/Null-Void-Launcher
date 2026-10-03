#!/usr/bin/env bash
# ==============================================================================
# build.sh - Unified, production-grade release pipeline
# ==============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

# ------------------------------------------------------------------------------
# Color & Style Formatting
# ------------------------------------------------------------------------------
if [[ -t 1 ]]; then
    BOLD="\033[1m"
    DIM="\033[2m"
    RED="\033[31m"
    GREEN="\033[32m"
    YELLOW="\033[33m"
    BLUE="\033[34m"
    CYAN="\033[36m"
    RESET="\033[0m"
else
    BOLD=""
    DIM=""
    RED=""
    GREEN=""
    YELLOW=""
    BLUE=""
    CYAN=""
    RESET=""
fi

log_info()    { printf "${BLUE}==>${RESET} ${BOLD}%s${RESET}\n" "$*"; }
log_success() { printf "${GREEN}==>${RESET} ${BOLD}%s${RESET}\n" "$*"; }
log_warn()    { printf "${YELLOW}==>${RESET} ${BOLD}%s${RESET}\n" "$*"; }
log_error()   { printf "${RED}==>${RESET} ${BOLD}%s${RESET}\n" "$*" >&2; }

# ------------------------------------------------------------------------------
# Configuration & Constants
# ------------------------------------------------------------------------------
PROJECT_NAME="nullvoidlauncher"
DIST_DIR="${SCRIPT_DIR}/dist"
RELEASE_FILE="${SCRIPT_DIR}/RELEASE"

# Options
BUILD_MODE="debug"
CLEAN_FIRST=0
DO_INSTALL=0
SPECIFIC_TARGET=""
MULTIARCH=0

# ------------------------------------------------------------------------------
# Metadata Parser
# ------------------------------------------------------------------------------
read_release_prop() {
    local key="$1"
    local default_val="$2"
    if [[ -f "${RELEASE_FILE}" ]]; then
        local val
        val="$(grep -E "^${key}=" "${RELEASE_FILE}" | cut -d'=' -f2- | tr -d ' "\r')"
        if [[ -n "${val}" ]]; then
            echo "${val}"
            return 0
        fi
    fi
    echo "${default_val}"
}

detect_git_meta() {
    if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
        GIT_COMMIT="$(git rev-parse --short HEAD)"
        if [[ -n "$(git status --porcelain 2>/dev/null)" ]]; then
            GIT_DIRTY="-dirty"
        else
            GIT_DIRTY=""
        fi
    else
        GIT_COMMIT="unknown"
        GIT_DIRTY=""
    fi
    BUILD_DATE="$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
}

detect_arch() {
    local raw_arch
    raw_arch="$(uname -m)"
    case "${raw_arch}" in
        x86_64)   HOST_ARCH="amd64" ;;
        aarch64)  HOST_ARCH="arm64" ;;
        armv7l)   HOST_ARCH="armv7" ;;
        i386|i686) HOST_ARCH="386" ;;
        *)        HOST_ARCH="${raw_arch}" ;;
    esac
    HOST_OS="$(uname -s | tr '[:upper:]' '[:lower:]')"
}

load_metadata() {
    VERSION="$(read_release_prop "VERSION" "0.1.0")"
    CHANNEL="$(read_release_prop "CHANNEL" "stable")"
    BUILD_NUMBER="$(read_release_prop "BUILD_NUMBER" "1")"
    CODENAME="$(read_release_prop "CODENAME" "Release")"
    detect_git_meta
    detect_arch
}

# ------------------------------------------------------------------------------
# Banner
# ------------------------------------------------------------------------------
print_banner() {
    printf "${CYAN}╔═══════════════════════════════════════════════════════════════╗${RESET}\n"
    printf "${CYAN}║${RESET}   ${BOLD}%-58s${RESET} ${CYAN}║${RESET}\n" "NullVoidLauncher Release Engineering Pipeline"
    printf "${CYAN}╚═══════════════════════════════════════════════════════════════╝${RESET}\n"
    printf "${DIM} Host OS / Arch :${RESET} ${BOLD}%s / %s${RESET}\n" "${HOST_OS}" "${HOST_ARCH}"
    printf "${DIM} Version / Build:${RESET} ${BOLD}%s (%s)%s${RESET} [Channel: %s, Codename: %s]\n" \
        "${VERSION}" "${BUILD_NUMBER}" "${GIT_DIRTY}" "${CHANNEL}" "${CODENAME}"
    printf "${DIM} Git Commit     :${RESET} ${BOLD}%s${RESET}\n" "${GIT_COMMIT}"
    printf "${DIM} Build Mode     :${RESET} ${BOLD}%s${RESET}\n" "${BUILD_MODE}"
    printf "${DIM} Target Output  :${RESET} ${BOLD}%s${RESET}\n" "${DIST_DIR}"
    printf "\n"
}

# ------------------------------------------------------------------------------
# Usage & Help
# ------------------------------------------------------------------------------
usage() {
    cat <<EOF
Usage: ./build.sh [OPTIONS]

Unified build pipeline for ${PROJECT_NAME}.

Options:
  (no arguments)        Build primary targets in development/debug mode.
  -r, --release         Build release artifacts (optimizations, shrinking, ProGuard/R8).
  --app                 Explicitly target Android App APK build.
  --daemon, --backend   Target-specific build flags (reserved for service components).
  -m, --multiarch       Cross-build / universal package targeting where supported.
  -c, --clean           Wipe dist/, Gradle build caches, and intermediate objects first.
  -i, --install         Install built artifact locally (via adb install or local path).
  -h, --help            Show this usage summary.

Examples:
  ./build.sh                     # Fast local debug APK build
  ./build.sh -r                  # Optimized release build into dist/
  ./build.sh -c -r               # Clean release build with checksums
  ./build.sh -i                  # Build and install directly via adb
EOF
}

# ------------------------------------------------------------------------------
# Prepare & Clean
# ------------------------------------------------------------------------------
clean_workspace() {
    log_info "Cleaning build directories and artifacts..."
    rm -rf "${DIST_DIR}"
    if [[ -x "./gradlew" ]]; then
        ./gradlew clean --no-daemon -q || true
    fi
    log_success "Clean completed."
}

prepare_dist() {
    mkdir -p "${DIST_DIR}"
}

# ------------------------------------------------------------------------------
# Build Android Subsystem
# ------------------------------------------------------------------------------
build_android() {
    local variant="$1" # "debug" or "release"
    log_info "Building Android target (${variant} mode)..."

    local gradle_task
    if [[ "${variant}" == "release" ]]; then
        gradle_task="assembleRelease"
    else
        gradle_task="assembleDebug"
    fi

    if [[ ! -x "./gradlew" ]]; then
        chmod +x ./gradlew
    fi

    ./gradlew "${gradle_task}" --no-daemon

    local source_apk=""
    if [[ "${variant}" == "release" ]]; then
        source_apk="app/build/outputs/apk/release/app-release.apk"
        if [[ ! -f "${source_apk}" ]]; then
            source_apk="app/build/outputs/apk/release/app-release-unsigned.apk"
        fi
    else
        source_apk="app/build/outputs/apk/debug/app-debug.apk"
    fi

    if [[ ! -f "${source_apk}" ]]; then
        log_error "Could not find expected APK at: ${source_apk}"
        exit 1
    fi

    local dest_filename="${PROJECT_NAME}-v${VERSION}-${variant}.apk"
    local symlink_filename="${PROJECT_NAME}-${variant}.apk"

    cp -f "${source_apk}" "${DIST_DIR}/${dest_filename}"
    (cd "${DIST_DIR}" && ln -sf "${dest_filename}" "${symlink_filename}")

    log_success "Built: ${DIST_DIR}/${dest_filename}"
    log_info "Convenience link: ${DIST_DIR}/${symlink_filename} -> ${dest_filename}"
}

# ------------------------------------------------------------------------------
# Checksums & Integrity
# ------------------------------------------------------------------------------
generate_checksums() {
    log_info "Generating cryptographic checksums (SHA-256)..."
    local sums_file="${DIST_DIR}/SHA256SUMS"
    
    (
        cd "${DIST_DIR}"
        rm -f SHA256SUMS
        # Find non-symlink regular files only
        local files=()
        while IFS= read -r -d $'\0' f; do
            files+=("$f")
        done < <(find . -maxdepth 1 -type f ! -name "SHA256SUMS" -print0)

        if [[ ${#files[@]} -gt 0 ]]; then
            sha256sum "${files[@]}" | sed 's|^\([a-f0-9]*\)\s*\./|\1  |' > SHA256SUMS
        fi
    )
    log_success "Checksums saved to ${sums_file}"
}

# ------------------------------------------------------------------------------
# Visual Feedback & Reporting
# ------------------------------------------------------------------------------
display_summary() {
    printf "\n"
    printf "${BOLD}%-40s %-12s %-64s${RESET}\n" "ARTIFACT" "SIZE" "SHA256 CHECKSUM PREFIX"
    printf "%s\n" "─────────────────────────────────────────────────────────────────────────────────────────────────────────────────"

    local count=0
    for f in "${DIST_DIR}"/*; do
        if [[ -f "${f}" && ! -L "${f}" && "$(basename "${f}")" != "SHA256SUMS" ]]; then
            count=$((count + 1))
            local fname size_human hash_prefix
            fname="$(basename "${f}")"
            size_human="$(du -h "${f}" | cut -f1)"
            hash_prefix="$(sha256sum "${f}" | awk '{print substr($1,1,16)}')..."
            printf "%-40s %-12s %-64s\n" "${fname}" "${size_human}" "${hash_prefix}"
        elif [[ -L "${f}" ]]; then
            local fname target
            fname="$(basename "${f}")"
            target="$(readlink "${f}")"
            printf "${DIM}%-40s %-12s -> %s${RESET}\n" "${fname}" "(symlink)" "${target}"
        fi
    done

    if [[ ${count} -eq 0 ]]; then
        printf "${YELLOW}No primary artifacts generated.${RESET}\n"
    fi
    printf "%s\n\n" "─────────────────────────────────────────────────────────────────────────────────────────────────────────────────"

    # Quick deployment instructions
    log_info "Quick Deployment / Execution Commands:"
    local active_apk="${DIST_DIR}/${PROJECT_NAME}-${BUILD_MODE}.apk"
    if [[ -f "${active_apk}" ]]; then
        printf "  ${BOLD}ADB Install:${RESET} adb install -r \"%s\"\n" "${active_apk}"
        printf "  ${BOLD}ADB Launch :${RESET} adb shell monkey -p com.codershubinc.nullvoidlauncher 1\n"
        printf "  ${BOLD}Verify APK :${RESET} apksigner verify --verbose \"%s\"\n" "${active_apk}"
    fi
    printf "\n"
}

# ------------------------------------------------------------------------------
# Local Installer
# ------------------------------------------------------------------------------
install_locally() {
    log_info "Executing installation step..."
    local active_apk="${DIST_DIR}/${PROJECT_NAME}-${BUILD_MODE}.apk"
    if [[ ! -f "${active_apk}" ]]; then
        log_error "No artifact found at ${active_apk} to install."
        exit 1
    fi

    if command -v adb >/dev/null 2>&1; then
        log_info "Found adb. Checking for connected Android devices..."
        local devices
        devices="$(adb devices | grep -v "List of devices" | grep "device$" || true)"
        if [[ -n "${devices}" ]]; then
            log_info "Installing ${active_apk} to connected device..."
            adb install -r "${active_apk}"
            log_success "App installed successfully via adb!"
        else
            log_warn "No connected adb devices found in 'device' state."
            log_info "You can install later using: adb install -r ${active_apk}"
        fi
    else
        log_warn "adb not found in PATH. Skipping adb install."
        log_info "Artifact remains ready at: ${active_apk}"
    fi
}

# ------------------------------------------------------------------------------
# Argument Parsing
# ------------------------------------------------------------------------------
while [[ $# -gt 0 ]]; do
    case "$1" in
        -r|--release)
            BUILD_MODE="release"
            shift
            ;;
        -c|--clean)
            CLEAN_FIRST=1
            shift
            ;;
        -i|--install)
            DO_INSTALL=1
            shift
            ;;
        -m|--multiarch)
            MULTIARCH=1
            shift
            ;;
        --app)
            SPECIFIC_TARGET="app"
            shift
            ;;
        --daemon|--backend)
            SPECIFIC_TARGET="backend"
            shift
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            usage
            exit 1
            ;;
    esac
done

# ------------------------------------------------------------------------------
# Main Flow
# ------------------------------------------------------------------------------
main() {
    load_metadata
    print_banner

    if [[ ${CLEAN_FIRST} -eq 1 ]]; then
        clean_workspace
    fi

    prepare_dist

    # Dispatch builds
    if [[ -z "${SPECIFIC_TARGET}" || "${SPECIFIC_TARGET}" == "app" ]]; then
        build_android "${BUILD_MODE}"
    elif [[ "${SPECIFIC_TARGET}" == "backend" ]]; then
        log_warn "No separate backend/daemon subsystem detected in this project. Android target is primary."
    fi

    generate_checksums
    display_summary

    if [[ ${DO_INSTALL} -eq 1 ]]; then
        install_locally
    fi

    log_success "Build pipeline finished successfully!"
}

main
