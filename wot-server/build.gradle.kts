/*
 * Copyright 2022 Mikhail Titov and other Webhooks over Telegram project contributors
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

plugins {
    application
    id("io.ktor.plugin")
    kotlin("kapt")
}

val projectGroup: String by project
val projectVersion: String by project

group = projectGroup
version = projectVersion

application {
    mainClass.set("dev.d1s.ApplicationKt")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    val kotlinVersion: String by project
    val ktorVersion: String by project
    val teabagsVersion: String by project
    val ktorStaticAuthVersion: String by project
    val logbackVersion: String by project
    val hikariVersion: String by project
    val postgresqlVersion: String by project
    val popkornVersion: String by project
    val tgbotapiVersion: String by project
    val kmLogVersion: String by project
    val liquibaseVersion: String by project
    val ktorServerLiquibaseVersion: String by project
    val ktormVersion: String by project
    val dispatchVersion: String by project
    val wsEventsVersion: String by project

    implementation(project(":wot-commons"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("dev.d1s.teabags:teabag-ktor-server:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-dto:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-postgres:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-ktorm:$teabagsVersion")
    implementation("dev.d1s:ktor-static-authentication:$ktorStaticAuthVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("cc.popkorn:popkorn:$popkornVersion")
    implementation("dev.inmo:tgbotapi:$tgbotapiVersion")
    implementation("org.lighthousegames:logging:$kmLogVersion")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    implementation("dev.d1s:ktor-server-liquibase:$ktorServerLiquibaseVersion")
    implementation("org.ktorm:ktorm-core:$ktormVersion")
    implementation("org.ktorm:ktorm-jackson:$ktormVersion")
    implementation("com.rickbusarow.dispatch:dispatch-core:$dispatchVersion")
    implementation("dev.d1s:ktor-ws-events:$wsEventsVersion")
    implementation("io.ktor:ktor-server-core-jvm:2.1.2")
    implementation("io.ktor:ktor-server-host-common-jvm:2.1.2")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.1.2")
    kapt("cc.popkorn:popkorn-compiler:$popkornVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}

ktor {
    docker {
        localImageName.set(project.name)
    }
}