package community.amaranth_legacy.mediawiki_discord_integration.discord.event

import community.amaranth_legacy.mediawiki_discord_integration.DISCORD_AUTO_ASSIGN_ROLE_ID
import dev.kord.common.entity.Snowflake
import dev.kord.core.event.guild.MemberJoinEvent

internal fun onMemberJoin(): suspend MemberJoinEvent.() -> Unit = event@{
	member.addRole(Snowflake(DISCORD_AUTO_ASSIGN_ROLE_ID), "automatically assigned by Discord bot")
}
