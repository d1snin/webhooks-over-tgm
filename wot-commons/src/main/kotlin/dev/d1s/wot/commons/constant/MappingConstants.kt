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

package dev.d1s.wot.commons.constant

import dev.d1s.teabag.web.constant.mapping.BASE_MAPPING
import dev.d1s.teabag.web.constant.mapping.ID_SEGMENT

private const val PUBLIC_WEBHOOK_MAPPING = "/{webhookNonce}"

private const val DELIVERIES_BASE_MAPPING = "$BASE_MAPPING/deliveries"
public const val POST_DELIVERY_MAPPING: String = DELIVERIES_BASE_MAPPING
public const val POST_DELIVERY_PUBLIC_MAPPING: String = PUBLIC_WEBHOOK_MAPPING
public const val GET_DELIVERY_MAPPING: String = DELIVERIES_BASE_MAPPING + ID_SEGMENT
public const val GET_DELIVERIES_MAPPING: String = DELIVERIES_BASE_MAPPING
public const val DELETE_DELIVERY_MAPPING: String = DELIVERIES_BASE_MAPPING + ID_SEGMENT

private const val TARGETS_BASE_MAPPING = "$BASE_MAPPING/targets"
public const val POST_TARGET_MAPPING: String = TARGETS_BASE_MAPPING
public const val GET_TARGET_MAPPING: String = TARGETS_BASE_MAPPING + ID_SEGMENT
public const val GET_TARGETS_MAPPING: String = TARGETS_BASE_MAPPING
public const val PUT_TARGET_MAPPING: String = TARGETS_BASE_MAPPING + ID_SEGMENT
public const val DELETE_TARGET_MAPPING: String = TARGETS_BASE_MAPPING + ID_SEGMENT

private const val WEBHOOKS_BASE_MAPPING = "$BASE_MAPPING/webhooks"
public const val POST_WEBHOOK_MAPPING: String = WEBHOOKS_BASE_MAPPING
public const val GET_WEBHOOK_MAPPING: String = WEBHOOKS_BASE_MAPPING + ID_SEGMENT
public const val GET_WEBHOOK_AVAILABILITY_MAPPING: String = PUBLIC_WEBHOOK_MAPPING
public const val GET_WEBHOOKS_MAPPING: String = WEBHOOKS_BASE_MAPPING
public const val PUT_WEBHOOK_MAPPING: String = WEBHOOKS_BASE_MAPPING + ID_SEGMENT
public const val PATCH_WEBHOOK_NONCE_MAPPING: String = "$WEBHOOKS_BASE_MAPPING$ID_SEGMENT/nonce"
public const val DELETE_WEBHOOK_MAPPING: String = WEBHOOKS_BASE_MAPPING + ID_SEGMENT