package dev.d1s.wot.client.builder

import dev.d1s.wot.client.entity.content.MutableTextSourceList
import dev.d1s.wot.client.entity.content.TextSourceList
import dev.d1s.wot.commons.entity.content.FormattedTextSource
import dev.d1s.wot.commons.entity.content.FormattedTextSourceType

public typealias TextSourceListBuilder = MutableTextSourceList.() -> Unit

public inline fun textSources(builder: TextSourceListBuilder): TextSourceList =
    mutableListOf<FormattedTextSource>().apply(builder)

public fun MutableTextSourceList.source(
    type: FormattedTextSourceType, value: String, subsources: TextSourceList = listOf()
) {
    add(
        FormattedTextSource(type, value, subsources)
    )
}

public inline fun MutableTextSourceList.source(
    type: FormattedTextSourceType, value: String, subsources: TextSourceListBuilder
) {
    source(type, value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.bold(
    value: String, subsources: TextSourceList = listOf()
) {
    source(FormattedTextSourceType.BOLD, value, subsources)
}

public inline fun MutableTextSourceList.bold(
    value: String, subsources: TextSourceListBuilder
) {
    bold(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.botCommand(
    value: String
) {
    source(FormattedTextSourceType.BOT_COMMAND, value)
}

public fun MutableTextSourceList.cashTag(
    value: String, subsources: TextSourceList = listOf()
) {
    source(FormattedTextSourceType.CASH_TAG, value, subsources)
}

public inline fun MutableTextSourceList.cashTag(
    value: String, subsources: TextSourceListBuilder
) {
    cashTag(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.code(
    value: String
) {
    source(FormattedTextSourceType.CODE, value)
}

public fun MutableTextSourceList.eMail(
    value: String, subsources: TextSourceList = listOf()
) {
    source(FormattedTextSourceType.E_MAIL, value, subsources)
}

public inline fun MutableTextSourceList.eMail(
    value: String, subsources: TextSourceListBuilder
) {
    eMail(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.hashTag(
    value: String, subsources: TextSourceList = listOf()
) {
    source(FormattedTextSourceType.HASH_TAG, value, subsources)
}

public inline fun MutableTextSourceList.hashTag(
    value: String, subsources: TextSourceListBuilder
) {
    hashTag(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.italic(
    value: String, subsources: TextSourceList = listOf()
) {
    source(FormattedTextSourceType.ITALIC, value, subsources)
}

public inline fun MutableTextSourceList.italic(
    value: String, subsources: TextSourceListBuilder
) {
    italic(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.mention(
    value: String, subsources: TextSourceList = listOf()
) {
    source(FormattedTextSourceType.MENTION, value, subsources)
}

public inline fun MutableTextSourceList.mention(
    value: String, subsources: TextSourceListBuilder
) {
    mention(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.phoneNumber(
    value: String, subsources: TextSourceList = listOf()
) {
    source(FormattedTextSourceType.PHONE_NUMBER, value, subsources)
}

public inline fun MutableTextSourceList.phoneNumber(
    value: String, subsources: TextSourceListBuilder
) {
    phoneNumber(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.pre(
    value: String
) {
    source(FormattedTextSourceType.PRE, value)
}

public fun MutableTextSourceList.regular(
    value: String
) {
    source(FormattedTextSourceType.REGULAR, value)
}

public fun MutableTextSourceList.spoiler(
    value: String, subsources: TextSourceList = listOf()
) {
    source(FormattedTextSourceType.SPOILER, value, subsources)
}

public inline fun MutableTextSourceList.spoiler(
    value: String, subsources: TextSourceListBuilder
) {
    spoiler(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.strikethrough(
    value: String, subsources: TextSourceList
) {
    source(FormattedTextSourceType.STRIKETHROUGH, value, subsources)
}

public inline fun MutableTextSourceList.strikethrough(
    value: String, subsources: TextSourceListBuilder
) {
    strikethrough(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.textLink(
    text: String, link: String
) {
    source(FormattedTextSourceType.TEXT_LINK, "[$text]($link)")
}

public fun MutableTextSourceList.underline(
    value: String, subsources: TextSourceList
) {
    source(FormattedTextSourceType.UNDERLINE, value, subsources)
}

public inline fun MutableTextSourceList.underline(
    value: String, subsources: TextSourceListBuilder
) {
    underline(value, mutableListOf<FormattedTextSource>().apply(subsources))
}

public fun MutableTextSourceList.url(
    value: String
) {
    source(FormattedTextSourceType.URL, value)
}