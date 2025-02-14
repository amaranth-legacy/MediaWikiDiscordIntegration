package community.amaranth_legacy.mediawiki_discord_integration.mediawiki

// adapted from https://phabricator.wikimedia.org/source/mediawiki/browse/REL1_43/includes/parser/Parser.php$4671-4687
internal fun processForPipeTrick(pageName: String): String {
	val tc = Regex("""[ %!"$&'()*,\-./0-9:;=?@A-Z\\^_`a-z~\x80-\xFF+]""")
	val nc = Regex("""[ _0-9A-Za-z\x80-\xff-]""")

	val p1 = Regex("""(:?$nc+:|:|)($tc+?)( ?\($tc+\))""")
	val p4 = Regex("""(:?$nc+:|:|)($tc+?)( ? ($tc+) )""")
	val p3 = Regex("""(:?$nc+:|:|)($tc+?)( ?\($tc+\)|)((?:, |，|، )$tc+|)""")

	var processedPageName = pageName
	processedPageName = p1.replace(processedPageName, "$2")
	processedPageName = p4.replace(processedPageName, "$2")
	processedPageName = p3.replace(processedPageName, "$2")
	return processedPageName
}
