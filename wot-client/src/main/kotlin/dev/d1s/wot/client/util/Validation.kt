package dev.d1s.wot.client.util

import dev.d1s.wot.client.entity.content.TextSourceList
import dev.d1s.wot.client.factory.validator
import dev.d1s.wot.commons.constant.TEXT_LINK_MARKDOWN_REGEX
import dev.d1s.wot.commons.entity.content.FormattedTextSourceType
import javax.validation.ConstraintViolationException
import javax.validation.ValidationException

private val validator = validator()

private val textLinkRegex = TEXT_LINK_MARKDOWN_REGEX.toRegex()

internal fun <T> T.validate(): T = apply {
    val violations = try {
        validator.validate(this)
    } catch (_: ValidationException) {
        return this
    }

    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations)
    }
}

internal fun TextSourceList.validateTextSources(): TextSourceList = apply {
    this.forEach {
        if (!it.type.supportsSubsources && it.subsources.isNotEmpty()) {
            throw IllegalArgumentException("This text source doesn't support subsources: $it")
        }

        if (it.type == FormattedTextSourceType.TEXT_LINK && !textLinkRegex.matches(it.value)) {
            throw IllegalArgumentException("A text link must follow the Markdown standard: $it")
        }
    }
}