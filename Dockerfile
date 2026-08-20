FROM eclipse-temurin:25-jre
LABEL authors="Juergen.Schiewe"

ENV WORKDIR=/opt/archigraph
RUN mkdir $WORKDIR $WORKDIR/lib

COPY target/archi-graph-j.jar $WORKDIR/lib

ENTRYPOINT java -jar $WORKDIR/lib/archi-graph-j.jar --help