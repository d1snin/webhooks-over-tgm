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

package dev.d1s.wot.server.entity.content

import dev.d1s.advice.exception.BadRequestException
import dev.d1s.wot.server.constant.TEXT_LINK_MARKDOWN_REGEX
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class FormattedTextSource(
    val type: @NotNull FormattedTextSourceType,
    val value: @NotBlank String,
    val subsources: @NotNull List<@Valid FormattedTextSource>
) {
    private val textLinkRegex = TEXT_LINK_MARKDOWN_REGEX.toRegex()

    init {
        if (!type.supportsSubsources && subsources.isNotEmpty()) {
            throw BadRequestException("This text source doesn't support subsources ($type).")
        }

        if (type == FormattedTextSourceType.TEXT_LINK && !textLinkRegex.matches(value)) {
            throw BadRequestException("A text link must follow the Markdown format.")
        }
    }
}
