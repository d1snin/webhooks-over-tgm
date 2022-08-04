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

package dev.d1s.wot.commons.dto.webhook

import dev.d1s.teabag.web.constant.regex.COMMON_NAME_REGEX
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Suppress("CanBePrimaryConstructorProperty")
public class WebhookUpsertDto(
    name: String,
    botToken: String,
    private: Boolean,
    available: Boolean,
    targets: List<String>
) {
    @Pattern(regexp = COMMON_NAME_REGEX)
    public val name: String = name

    @NotBlank
    public val botToken: String = botToken

    @NotNull
    public val private: Boolean = private

    @NotNull
    public val available: Boolean = available

    @NotNull
    public val targets: List<@NotBlank String> = targets
}
