@echo off
title Local Dependency Builder
IF NOT EXIST BuildTools (
    mkdir BuildTools
)
cd BuildTools
curl -z BuildTools.jar -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
"C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_8_R1/nms" -rev 1.8
"C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_8_R2/nms" -rev 1.8.3
"C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_8_R3/nms" -rev 1.8.8
"C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_9_R1/nms" -rev 1.9.2
"C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_9_R2/nms" -rev 1.9.4
"C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_10_R1/nms" -rev 1.10.2
"C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_11_R1/nms" -rev 1.11.2
"C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" -jar BuildTools.jar --compile spigot --output-dir "../impetus-spigot_1_12_R1/nms" -rev 1.12.2
cd ..
cls
echo "Finished building local dependencies!"
pause