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

buildscript {

    val versionMajor = 1
    val versionMinor = 0
    val versionPatch = 0
    val versionId = "-alpha05"

    extra.apply {
        set("build_versions.min_sdk", 24)
        set("build_versions.target_sdk", 30)
        set("build_versions.compile_sdk", 30)
        set(
            "build_versions.build_tools",
            if (System.getenv("CUSTOM_BUILDTOOLS") != null) System.getenv("CUSTOM_BUILDTOOLS") else "30.0.3"
        )
        set(
            "build_versions.version_code",
            versionMajor * 1000 + versionMinor * 100 + versionPatch * 10
        )
        set(
            "build_versions.version_name",
            "${versionMajor}.${versionMinor}.${versionPatch}${versionId}"
        )
        set("kotlin_version", "1.4.31")
        set("compose_version", "1.0.0-beta04")
    }

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha14")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.extra["kotlin_version"]}")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}