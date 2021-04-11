/*
 * Copyright 2021 Simon Zigelli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.dokka") version "1.4.30"
    `maven-publish`
}

kotlin {
    explicitApi()
    explicitApiWarning()
}

group = "com.github.zigellsn.compose"
version = project.rootProject.ext["build_versions.version_name"] as String

android {
    compileSdkVersion(project.rootProject.ext["build_versions.compile_sdk"] as Int)
    buildToolsVersion(project.rootProject.ext["build_versions.build_tools"] as String)

    defaultConfig {
        minSdkVersion(project.rootProject.ext["build_versions.min_sdk"] as Int)
        targetSdkVersion(project.rootProject.ext["build_versions.target_sdk"] as Int)
        versionCode(project.rootProject.ext["build_versions.version_code"] as Int)
        versionName(project.rootProject.ext["build_versions.version_name"] as String)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isTestCoverageEnabled = !project.hasProperty("android.injected.invoked.from.ide")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        useIR = true
    }

    packagingOptions.resources.excludes.apply {
        add("META-INF/licenses/**")
        add("META-INF/AL2.0")
        add("META-INF/LGPL2.1")
        add("**/attach_hotspot_windows.dll")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = project.rootProject.ext["compose_version"] as String?
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.compose.ui:ui:${project.rootProject.ext["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling:${project.rootProject.ext["compose_version"]}")
    implementation("androidx.compose.foundation:foundation:${project.rootProject.ext["compose_version"]}")
    implementation("androidx.compose.material:material:${project.rootProject.ext["compose_version"]}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${project.rootProject.ext["compose_version"]}")
}

val androidSourcesJar by tasks.creating(org.gradle.jvm.tasks.Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

tasks.build.configure {
    dependsOn(dokkaJar)
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("javadoc"))
}

val dokkaJar by tasks.creating(org.gradle.jvm.tasks.Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

val githubUser = if (gradle is ExtensionAware) {
    if ((gradle as ExtensionAware).extra.has("GITHUB_USER"))
        (gradle as ExtensionAware).extra.get("GITHUB_USER") as String
    else ""
} else ""
val githubPersonalAccessToken = if (gradle is ExtensionAware) {
    if ((gradle as ExtensionAware).extra.has("GITHUB_PERSONAL_ACCESS_TOKEN"))
        (gradle as ExtensionAware).extra.get("GITHUB_PERSONAL_ACCESS_TOKEN") as String
    else ""
} else ""

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                artifact(dokkaJar)
                artifact(androidSourcesJar)

                repositories {
                    maven {
                        name = "GithubPackages"
                        url = uri("https://maven.pkg.github.com/zigellsn/ExposedDropdownMenu")
                        credentials {
                            username = System.getenv("GITHUB_USER")
                                ?: project.properties["GITHUB_USER"] as String? ?: ""
                            password = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN")
                                ?: project.properties["GITHUB_PERSONAL_ACCESS_TOKEN"] as String?
                                        ?: ""
                        }
                    }
                }

                pom {
                    name.set("ExposedDropdownMenu")
                    description.set("An exposed dropdown menu for Jetpack Compose")
                    url.set("https://github.com/zigellsn/exposeddropdownmenu/")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("zigellsn")
                            name.set("Simon Zigelli")
                            email.set("zigellsn@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/zigellsn/exposeddropdownmenu.git")
                        developerConnection.set("scm:git:ssh://github.com/zigellsn/exposeddropdownmenu.git")
                        url.set("https://github.com/zigellsn/exposeddropdownmenu/")
                    }
                }

                from(components["release"])
                groupId = "com.github.zigellsn.compose"
                artifactId = "exposeddropdownmenu"
                version = project.rootProject.ext["build_versions.version_name"] as String
            }
            create<MavenPublication>("debug") {
                artifact(dokkaJar)
                artifact(androidSourcesJar)

                repositories {
                    maven {
                        name = "GithubPackages"
                        url = uri("https://maven.pkg.github.com/zigellsn/ExposedDropdownMenu")
                        credentials {
                            username = System.getenv("GITHUB_USER")
                                ?: project.properties["GITHUB_USER"] as String? ?: ""
                            password = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN")
                                ?: project.properties["GITHUB_PERSONAL_ACCESS_TOKEN"] as String?
                                        ?: ""
                        }
                    }
                }

                pom {
                    name.set("ExposedDropdownMenu")
                    description.set("An exposed dropdown menu for Jetpack Compose")
                    url.set("https://github.com/zigellsn/exposeddropdownmenu/")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("zigellsn")
                            name.set("Simon Zigelli")
                            email.set("zigellsn@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/zigellsn/exposeddropdownmenu.git")
                        developerConnection.set("scm:git:ssh://github.com/zigellsn/exposeddropdownmenu.git")
                        url.set("https://github.com/zigellsn/exposeddropdownmenu/")
                    }
                }

                from(components["debug"])
                groupId = "com.github.zigellsn.compose"
                artifactId = "exposeddropdownmenu-debug"
                version = project.rootProject.ext["build_versions.version_name"] as String
            }
        }
    }
}