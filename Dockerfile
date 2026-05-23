FROM eclipse-temurin:11-jre-jammy

ENV JETTY_VERSION=11.0.20
ENV JETTY_HOME=/opt/jetty
ENV JETTY_BASE=/opt/jetty-base

# Jetty letöltése és kicsomagolása
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL "https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-home/${JETTY_VERSION}/jetty-home-${JETTY_VERSION}.tar.gz" \
        -o /tmp/jetty.tar.gz && \
    mkdir -p ${JETTY_HOME} && \
    tar -xzf /tmp/jetty.tar.gz -C ${JETTY_HOME} --strip-components=1 && \
    rm /tmp/jetty.tar.gz && \
    apt-get remove -y curl && apt-get autoremove -y && rm -rf /var/lib/apt/lists/*

# Jetty base inicializálása: http + deploy + annotations modulok
RUN java -jar ${JETTY_HOME}/start.jar \
    jetty.base=${JETTY_BASE} \
    --create-startd \
    --add-module=http,deploy,annotations

# Webapps és data könyvtárak létrehozása
RUN mkdir -p ${JETTY_BASE}/webapps && mkdir -p /data

# WAR telepítése ROOT context-re
COPY target/md-searcher-1.0-SNAPSHOT.war ${JETTY_BASE}/webapps/ROOT.war

# Ha van másik WAR, azt így add hozzá:
# COPY target/other-app.war ${JETTY_BASE}/webapps/other-app.war

VOLUME /data

EXPOSE 8080

ENV JAVA_OPTIONS="-Dmdsearcher.dataDir=/data"

ENTRYPOINT ["java", \
    "-Dmdsearcher.dataDir=/data", \
    "-jar", "/opt/jetty/start.jar", \
    "jetty.base=/opt/jetty-base", \
    "jetty.http.port=8080"]