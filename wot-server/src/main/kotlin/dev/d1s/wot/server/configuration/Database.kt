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

package dev.d1s.wot.server.configuration

import cc.popkorn.popKorn
import com.zaxxer.hikari.HikariDataSource
import dev.d1s.ktor.liquibase.plugin.LiquibaseMigrations
import dev.d1s.teabag.ktor.server.ext.tryRetrieveDbProperties
import io.ktor.server.application.*
import org.ktorm.database.Database

fun Application.configureDatabase() {
    val database = Database.connect(
        HikariDataSource().apply {
            environment.config.tryRetrieveDbProperties().let {
                jdbcUrl = it.url
                username = it.user
                password = it.password
            }
        }
    )

    database.useConnection {
        install(LiquibaseMigrations) {
            connection = it
        }
    }

    popKorn().addInjectable(database)
}