package community.amaranth_legacy.mediawiki_discord_integration

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import community.amaranth_legacy.mediawiki_discord_integration.discord.event.onMemberJoin
import community.amaranth_legacy.mediawiki_discord_integration.discord.event.onMessageCreate
import community.amaranth_legacy.mediawiki_discord_integration.http.onWebhookPost
import dev.kord.core.Kord
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import java.io.File
import kotlin.system.exitProcess
import io.ktor.client.engine.cio.CIO as ClientCIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.cio.CIO as ServerCIO
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

// build-time variables
internal const val MEDIAWIKI_REST_PATH = "https://amaranth-legacy.community/rest.php"
internal const val MEDIAWIKI_ARTICLE_PATH = "https://amaranth-legacy.community/$1"
internal const val DISCORD_GUILD_ID = 618886775208935427
internal const val DISCORD_AUTO_ASSIGN_ROLE_ID = 1287189159232143370
internal const val DISCORD_AUTO_ASSIGN_BOT_ROLE_ID = 759274901366112276
internal const val DISCORD_LINKED_ACCOUNT_ROLE_ID = 1287189129469231105
internal const val DISCORD_PUBLIC_KEY = "052bb76c8ac866e895e02aa65b811a3f5c4144f8d201558ae9b745e5fecd44b5"
internal const val HTTP_USER_AGENT = "MediaWikiDiscordIntegration/0.3.0"
internal const val HTTP_SERVER_PORT = 8080

private val logger = KotlinLogging.logger {}

suspend fun main() {
	// environment variables
	val discordTokenPath = System.getenv("DISCORD_TOKEN_PATH")
	if (discordTokenPath == null) {
		logger.error { "DISCORD_TOKEN_PATH must be set as an environment variable" }
		exitProcess(1)
	}

	// Kord init
	val kord = Kord(withContext(Dispatchers.IO) { File(discordTokenPath).readText() })

	// HTTP client init
	val httpClient = HttpClient(ClientCIO) {
		install(ClientContentNegotiation) {
			json()
		}
	}

	// libsodium init
	LibsodiumInitializer.initialize()

	// event init
	kord.on<MemberJoinEvent>(consumer = onMemberJoin())
	kord.on<MessageCreateEvent>(consumer = onMessageCreate(httpClient))

	// shutdown Kord on Java VM shutdown
	Runtime.getRuntime().addShutdownHook(object : Thread() {
		override fun run() = runBlocking {
			kord.shutdown()
		}
	})

	coroutineScope {
		launch {
			// Kord login
			@OptIn(PrivilegedIntent::class)
			kord.login {
				intents += Intent.MessageContent
				intents += Intent.GuildMembers
			}
		}
		launch {
			// HTTP server init
			embeddedServer(ServerCIO, port = HTTP_SERVER_PORT) {
				install(ServerContentNegotiation) {
					json()
				}
				routing {
					post("/webhook", onWebhookPost(kord))
				}
			}.startSuspend(wait = true)
		}
	}
}
