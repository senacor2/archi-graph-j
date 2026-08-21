# Runtime image for archi-graph-j.
#
# The shaded jar is built by the workflow (mvn package) and only copied in here.
# The image therefore contains no architecture specific content and can be built
# for every platform of the base image without emulating a foreign architecture.

FROM eclipse-temurin:25-jre

LABEL org.opencontainers.image.title="archi-graph-j" \
      org.opencontainers.image.description="Render high level architecture diagrams with an automatic layout"

# Input and output files are expected in the working directory, mount them here.
WORKDIR /work

COPY target/archi-graph-j.jar /opt/archi-graph-j/archi-graph-j.jar

ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "/opt/archi-graph-j/archi-graph-j.jar"]
CMD ["--help"]
