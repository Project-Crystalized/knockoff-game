plugins {
    id("java")
}

group = "gg.knockoff.game"
version = "1.0.0 - build.gradle.kts Build"

repositories {
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
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.50")) // Ref: https://github.com/IntellectualSites/bom
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit")
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    implementation("org.xerial:sqlite-jdbc:3.47.0.0");

    //compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
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

