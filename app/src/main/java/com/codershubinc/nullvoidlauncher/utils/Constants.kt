package com.codershubinc.nullvoidlauncher.utils

object Constants {
    object Github {
        const val USERNAME = "CodersHubInc"
        const val REPO_SLUG = "null-void-launcher"
        const val BASE_URL = "https://github.com/$USERNAME"
        const val REPO_URL = "$BASE_URL/$REPO_SLUG"
        const val LICENSE_URL = "$REPO_URL/blob/main/LICENSE"
        const val API_BASE_URL = "https://api.github.com"
        const val LATEST_RELEASE_API_URL = "$API_BASE_URL/repos/$USERNAME/$REPO_SLUG/releases/latest"
        const val USER_AGENT = REPO_SLUG
    }

    object App {
        const val VERSION = "v0.1.0"
        const val VERSION_SUFFIX = "stable"
    }

    object System {
        const val CODENAME = "ELEGANCE"
        const val ORGANIZATION = "CodersHub INC"
        const val BUILD_TYPE = "First Stable Release"
        const val UI_ENGINE = "Compose Modern"
    }
    object  Org {
        const val WEBSITE = "https://codershubinc.com"
    }
}
