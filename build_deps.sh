#!/bin/sh
JAVA="/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java"
{
    if [ ! -d BuildTools ]; then
        mkdir BuildTools
    fi
    
    cd BuildTools
    curl -z BuildTools.jar -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
    
    $JAVA -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_8_R1/nms" -rev 1.8
    $JAVA -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_8_R2/nms" -rev 1.8.3
    $JAVA -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_8_R3/nms" -rev 1.8.8
    $JAVA -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_9_R1/nms" -rev 1.9.2
    $JAVA -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_9_R2/nms" -rev 1.9.4
    $JAVA -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_10_R1/nms" -rev 1.10.2
    $JAVA -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_11_R1/nms" -rev 1.11.2
    $JAVA -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_12_R1/nms" -rev 1.12.2    
    
    cd ..

} >/dev/null 2>&1

echo "Finished building local dependencies!"
