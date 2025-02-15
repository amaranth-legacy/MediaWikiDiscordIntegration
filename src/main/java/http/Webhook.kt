package community.amaranth_legacy.mediawiki_discord_integration.http

import com.ionspin.kotlin.crypto.signature.InvalidSignatureException
import com.ionspin.kotlin.crypto.signature.Signature
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import community.amaranth_legacy.mediawiki_discord_integration.DISCORD_GUILD_ID
import community.amaranth_legacy.mediawiki_discord_integration.DISCORD_LINKED_ACCOUNT_ROLE_ID
import community.amaranth_legacy.mediawiki_discord_integration.DISCORD_PUBLIC_KEY
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

@OptIn(ExperimentalUnsignedTypes::class)
internal fun onWebhookPost(kord: Kord): suspend RoutingContext.() -> Unit = post@{
	val body = call.receiveText()

	try {
		// validate signature
		Signature.verifyDetached(
			signature = call.request.header("X-Signature-Ed25519")!!.hexStringToUByteArray(),
			message = (call.request.header("X-Signature-Timestamp")!! + body).encodeToUByteArray(),
			publicKey = DISCORD_PUBLIC_KEY.hexStringToUByteArray()
		)
	} catch (_: InvalidSignatureException) {
		// invalid signature
		call.respond(HttpStatusCode.Unauthorized, message = "invalid request signature")
		return@post
	}

	try {
		val bodyAsJson = Json.parseToJsonElement(body).jsonObject
		if (bodyAsJson["type"] == JsonPrimitive(1)) {
			// this is an event (always APPLICATION_AUTHORIZED)
			val userId = bodyAsJson["event"]!!
				.jsonObject["data"]!!
				.jsonObject["user"]!!
				.jsonObject["id"]!!
				.jsonPrimitive.long
			kord.getGuild(Snowflake(DISCORD_GUILD_ID))
				.getMember(Snowflake(userId))
				.addRole(Snowflake(DISCORD_LINKED_ACCOUNT_ROLE_ID))
		}
		call.respond(HttpStatusCode.NoContent)
	} catch (_: SerializationException) {
		call.respond(HttpStatusCode.BadRequest, message = "invalid body")
	}
}
