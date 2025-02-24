package community.amaranth_legacy.mediawiki_discord_integration.discord.event

import community.amaranth_legacy.mediawiki_discord_integration.MEDIAWIKI_ARTICLE_PATH
import community.amaranth_legacy.mediawiki_discord_integration.discord.escapeDiscordMarkdown
import community.amaranth_legacy.mediawiki_discord_integration.mediawiki.getMediaWikiPageBare
import community.amaranth_legacy.mediawiki_discord_integration.mediawiki.model.BarePage
import community.amaranth_legacy.mediawiki_discord_integration.mediawiki.processForPipeTrick
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.allowedMentions
import io.ktor.client.*
import io.ktor.client.call.*

internal fun onMessageCreate(httpClient: HttpClient): suspend MessageCreateEvent.() -> Unit = event@{
	// ignore bot messages
	if (message.author?.isBot != false) return@event

	// match anything that looks like [[link]] or [[link|]] or [[link|text]]
	val matches = Regex("""(?<!\\)\[\[(.+?)(\|.*?)?]]""").findAll(message.content)
	// ignore messages without MediaWiki links
	if (matches.count() == 0) return@event

	// process each match
	for (match in matches) {
		processMediaWikiLinkMatch(message, match, httpClient)
	}
}

private suspend fun processMediaWikiLinkMatch(message: Message, match: MatchResult, httpClient: HttpClient) {
	// extract the parameters from the text
	val page = match.groupValues[1].trim()
	val linkText = match.groupValues[2].trim()

	// make a REST API call to find if the page exists
	val response = getMediaWikiPageBare(httpClient, page)
	if (response.status.value == 404) {
		// page does not exist
		message.reply {
			content = "Page does not exist: ${escapeDiscordMarkdown(page)}"
			allowedMentions {
				repliedUser = false
			}
		}
		return
	}

	// page exists
	val responseContent: BarePage = response.body()
	val markdownLinkText = when (linkText) {
		// no pipe
		"" -> page
		// pipe trick
		"|" -> processForPipeTrick(page)
		// pipe with specified link text
		else -> linkText.removePrefix("|")
	}
	message.reply {
		content = buildString {
			append("Link: ")
			append("[")
			append(markdownLinkText)
			append("]")
			append("(")
			append(MEDIAWIKI_ARTICLE_PATH.replace("$1", responseContent.key))
			append(")")
		}
		allowedMentions {
			repliedUser = false
		}
		suppressEmbeds = true
	}
}
