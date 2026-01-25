pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // <-- Thêm dòng này nếu chưa có, vì bản 2.8.2 nằm ở đây
        maven { url = uri("https://jitpack.io") } // Dự phòng
    }
}

rootProject.name = "FinalProject"
include(":app")
