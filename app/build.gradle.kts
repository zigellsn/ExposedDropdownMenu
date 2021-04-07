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
    maven
}

kotlin {
    explicitApi()
    explicitApiWarning()
}

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
        release {
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

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("javadoc"))
}

val dokkaJar by tasks.creating(org.gradle.jvm.tasks.Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}