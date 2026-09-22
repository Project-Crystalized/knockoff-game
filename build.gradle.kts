plugins {
    id("java")
}

group = "gg.knockoff.game"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.opencollab.dev/main/")
    }
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52")) // Ref: https://github.com/IntellectualSites/bom
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit")
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")
    implementation("org.xerial:sqlite-jdbc:3.47.0.0");
    implementation("gg.crystalized.lobby:Lobby_plugin:1.0-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.13.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    // Taken from LS
    //tasks.withType<JavaCompile> {
    //    options.encoding = "UTF-8"
    //}
}

