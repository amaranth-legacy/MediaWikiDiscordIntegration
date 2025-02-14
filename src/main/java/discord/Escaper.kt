package community.amaranth_legacy.mediawiki_discord_integration.discord

internal fun escapeDiscordMarkdown(text: String): String {
	val escaped = Regex("""\\([*_`~\\])""")
	val unescaped = Regex("""([*_`~\\])""")

	var processedText = text
	processedText = escaped.replace(processedText, "$1")
	processedText = unescaped.replace(processedText, """\\$1""")
	return processedText
}
