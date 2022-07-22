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

package dev.d1s.wot.server.dto.webhook

import dev.d1s.teabag.web.constant.regex.COMMON_NAME_REGEX
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

class WebhookUpsertDto(
    _name: String,
    _botToken: String,
    _private: Boolean,
    _targets: List<String>
) {
    @Pattern(regexp = COMMON_NAME_REGEX)
    val name: String = _name

    @NotBlank
    val botToken: String = _botToken

    @NotNull
    val private: Boolean = _private

    @NotNull
    val targets: List<@NotBlank String> = _targets
}
