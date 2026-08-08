plugins {
    id("java")
    //Copied this from my test plugin, this allows to run server in IDEA
    id("xyz.jpenilla.run-paper") version "3.0.2"
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
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        url = uri("https://repo.opencollab.dev/main/")
    }
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52")) // Ref: https://github.com/IntellectualSites/bom
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit")
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    implementation("org.xerial:sqlite-jdbc:3.47.0.0");
    implementation("gg.crystalized.lobby:Lobby_plugin:1.0-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

    // Taken from LS
    //tasks.withType<JavaCompile> {
    //    options.encoding = "UTF-8"
    //}
}
tasks {
    runServer {
        //I just copied pasted that from my plugin to make run server work so that I can test it in IDEA
        //Default comments that come with setting up the project with a plugin
        /*
            // Configure the Minecraft version for our task.
            // This is the only required configuration besides applying the plugin.
            // Your plugin's jar (or shadowJar if present) will be used automatically.

         */
        minecraftVersion("26.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

