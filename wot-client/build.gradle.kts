/*
 * Copyright 2022 Webhooks over Telegram project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("maven-publish")
}

java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    val ktorVersion: String by project
    val teabagsVersion: String by project
    val lpClientVersion: String by project
    val jacksonVersion: String by project
    val hibernateValidatorVersion: String by project
    val glassfishElVersion: String by project

    api(project(":wot-commons"))
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("dev.d1s.teabags:teabag-stdlib:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-dto:$teabagsVersion")
    api("dev.d1s.long-polling:lp-client:$lpClientVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
    implementation("org.glassfish:javax.el:$glassfishElVersion")
}

publishing {
    publications {
        create<MavenPublication>("wot-client") {
            from(components["java"])
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

kotlin {
    explicitApi()
}