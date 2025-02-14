FROM docker.io/library/gradle:8 as build
WORKDIR /opt/MediaWikiDiscordIntegration

COPY ./ /opt/MediaWikiDiscordIntegration/
RUN gradle installDist --no-daemon

FROM docker.io/library/eclipse-temurin:21
WORKDIR /opt/MediaWikiDiscordIntegration

COPY --from=build /opt/MediaWikiDiscordIntegration/build/install/MediaWikiDiscordIntegration/ /opt/MediaWikiDiscordIntegration/
EXPOSE 8080
ENTRYPOINT ["/opt/MediaWikiDiscordIntegration/bin/MediaWikiDiscordIntegration"]
