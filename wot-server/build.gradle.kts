/*
 * Copyright 2022 Webhooks over Telegram project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.gorylenko.gradle-git-properties")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

java.sourceCompatibility = JavaVersion.VERSION_17

val teabagsVersion: String by project
val starterSimpleSecurityVersion: String by project
val starterAdviceVersion: String by project
val starterWelcomerVersion: String by project
val longPollingStarterVersion: String by project
val caffeineVersion: String by project
val kmLogVersion: String by project
val liquibaseVersion: String by project
val hibernateTypesVersion: String by project
val tgbotapiVersion: String by project

dependencies {
    implementation(project(":wot-commons"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("dev.d1s.teabags:teabag-dto:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-stdlib:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-spring-web:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-hibernate:$teabagsVersion")
    implementation("dev.d1s:spring-boot-starter-simple-security:$starterSimpleSecurityVersion")
    implementation("dev.d1s:spring-boot-starter-advice:$starterAdviceVersion")
    implementation("dev.d1s:spring-boot-starter-welcomer:$starterWelcomerVersion") {
        exclude("dev.d1s.teabags", "teabag-stdlib")
    }
    implementation("dev.d1s.long-polling:spring-boot-starter-lp-server-web:$longPollingStarterVersion")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    implementation("org.lighthousegames:logging-jvm:$kmLogVersion")
    implementation("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    implementation("com.vladmihalcea:hibernate-types-55:$hibernateTypesVersion")
    implementation("dev.inmo:tgbotapi-jvm:$tgbotapiVersion")
}

configurations["implementation"].exclude(
    "org.springframework.boot", "spring-boot-starter-tomcat"
)

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
    }
}

springBoot {
    buildInfo()
}