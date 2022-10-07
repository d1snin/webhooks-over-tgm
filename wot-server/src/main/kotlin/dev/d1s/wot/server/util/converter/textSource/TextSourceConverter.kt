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

package dev.d1s.wot.server.util.converter

import dev.d1s.wot.commons.const.TEXT_LINK_MARKDOWN_REGEX
import dev.d1s.wot.commons.entity.content.FormattedTextSource
import dev.d1s.wot.commons.entity.content.FormattedTextSourceType
import dev.inmo.tgbotapi.types.message.textsources.*
import dev.inmo.tgbotapi.utils.RiskFeature

private val textLinkRegex = TEXT_LINK_MARKDOWN_REGEX.toRegex()

@OptIn(RiskFeature::class)
fun FormattedTextSource.toTextSource(): TextSource? = run {
    if (!type.supportsSubsources && subsources.isNotEmpty()) {
        return null
    }

    if (type == FormattedTextSourceType.TEXT_LINK && !textLinkRegex.matches(value)) {
        return null
    }

    val transformedSubsources = subsources.map {
        it.toTextSource() ?: return null
    }

    when (type) {
        FormattedTextSourceType.BOLD -> {
            BoldTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.BOT_COMMAND -> {
            BotCommandTextSource(value)
        }

        FormattedTextSourceType.CASH_TAG -> {
            CashTagTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.CODE -> {
            CodeTextSource(value)
        }

        FormattedTextSourceType.E_MAIL -> {
            EMailTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.HASH_TAG -> {
            HashTagTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.ITALIC -> {
            ItalicTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.MENTION -> {
            MentionTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.PHONE_NUMBER -> {
            PhoneNumberTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.PRE -> {
            PhoneNumberTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.REGULAR -> {
            RegularTextSource(value)
        }

        FormattedTextSourceType.SPOILER -> {
            SpoilerTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.STRIKETHROUGH -> {
            StrikethroughTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.TEXT_LINK -> {
            val parts = value.split("](")

            TextLinkTextSource(
                parts[0].removePrefix("["),
                parts[1].removeSuffix(")")
            )
        }

        FormattedTextSourceType.UNDERLINE -> {
            UnderlineTextSource(value, transformedSubsources)
        }

        FormattedTextSourceType.URL -> {
            URLTextSource(value)
        }
    }
}