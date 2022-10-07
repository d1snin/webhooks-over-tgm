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

package dev.d1s.wot.commons.validation

import dev.d1s.teabag.constant.regex.COMMON_NAME_REGEX
import dev.d1s.teabag.konform.isNotBlank
import dev.d1s.wot.commons.dto.delivery.DeliveryCreationDto
import dev.d1s.wot.commons.dto.delivery.PublicDeliveryCreationDto
import dev.d1s.wot.commons.dto.target.TargetUpsertDto
import dev.d1s.wot.commons.dto.webhook.WebhookUpsertDto
import dev.d1s.wot.commons.entity.content.Content
import dev.d1s.wot.commons.entity.content.FormattedTextSource
import io.konform.validation.Validation
import io.konform.validation.jsonschema.pattern

public val validateFormattedTextSource: Validation<FormattedTextSource> = Validation {
    FormattedTextSource::value {
        isNotBlank()
    }
}

public val validateContent: Validation<Content> = Validation {
    Content::sources onEach {
        run(validateFormattedTextSource)

        FormattedTextSource::subsources onEach {
            run(validateFormattedTextSource)
        }
    }
}

public val validateDeliveryCreationDto: Validation<DeliveryCreationDto> = Validation {
    DeliveryCreationDto::content {
        run(validateContent)
    }

    DeliveryCreationDto::webhook {
        isNotBlank()
    }
}

public val validatePublicDeliveryCreationDto: Validation<PublicDeliveryCreationDto> = Validation {
    PublicDeliveryCreationDto::content {
        run(validateContent)
    }
}

public val validateTargetUpsertDto: Validation<TargetUpsertDto> = Validation {
    TargetUpsertDto::chatId {
        isNotBlank()
    }
}

public val validateWebhookUpsertDto: Validation<WebhookUpsertDto> = Validation {
    WebhookUpsertDto::name {
        pattern(COMMON_NAME_REGEX)
    }

    WebhookUpsertDto::botToken {
        isNotBlank()
    }

    WebhookUpsertDto::targets onEach {
        isNotBlank()
    }
}