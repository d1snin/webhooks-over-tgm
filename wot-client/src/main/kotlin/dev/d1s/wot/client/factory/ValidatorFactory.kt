package dev.d1s.wot.client.factory

import javax.validation.Validation

private val factory = Validation.buildDefaultValidatorFactory()

internal fun validator() = factory.validator