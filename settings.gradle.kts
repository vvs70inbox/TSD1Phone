pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            isAllowInsecureProtocol = true
            url=uri("http://47.108.228.164:8081/nexus/service/local/repositories/releases/content/")
        }
    }
}

rootProject.name = "TSD1Phone"
include(":app")
