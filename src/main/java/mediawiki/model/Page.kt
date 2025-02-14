package community.amaranth_legacy.mediawiki_discord_integration.mediawiki.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarePage(
	val id: Int,
	val key: String,
	val title: String,
	val latest: PageLatestRevision,
	@SerialName("content_model") val contentModel: String,
	val license: PageLicense,
	@SerialName("html_url") val htmlUrl: String,
	@SerialName("redirect_target") val redirectTarget: String? = null,
)

@Serializable
data class PageLatestRevision(val id: Int, val timestamp: Instant)

@Serializable
data class PageLicense(val url: String, val title: String)
