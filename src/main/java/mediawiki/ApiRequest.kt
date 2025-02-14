package community.amaranth_legacy.mediawiki_discord_integration.mediawiki

import community.amaranth_legacy.mediawiki_discord_integration.HTTP_USER_AGENT
import community.amaranth_legacy.mediawiki_discord_integration.MEDIAWIKI_REST_PATH
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

internal suspend fun getMediaWikiPageBare(httpClient: HttpClient, name: String) =
	httpClient.get(buildString {
		append(MEDIAWIKI_REST_PATH)
		append("/v1/page/")
		append(URLEncoder.encode(name, UTF_8.toString()).replace("+", "%20"))
		append("/bare")
	}) {
		headers {
			append(HttpHeaders.UserAgent, HTTP_USER_AGENT)
		}
	}
