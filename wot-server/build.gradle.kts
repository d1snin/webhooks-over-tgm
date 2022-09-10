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
}

dependencies {
    val kotlinVersion: String by project
    val ktorVersion: String by project
    val logbackVersion: String by project
    val hikariVersion: String by project
    val postgresqlVersion: String by project
    val popkornVersion: String by project
    val tgbotapiVersion: String by project
    val kmLogVersion: String by project
    val liquibaseVersion: String by project

    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("cc.popkorn:popkorn:$popkornVersion")
    implementation("dev.inmo:tgbotapi:$tgbotapiVersion")
    implementation("org.lighthousegames:logging:$kmLogVersion")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

ktor {
    docker {
        localImageName.set(project.name)
    }
}