# Tiny Config
A very basic configuration API that is used by Proton.

## How to use:

### Import as a dependency (Gradle):
Gradle Groovy:
```groovy
repositories {
    maven { url = "https://nexus.dyonb.nl/repository/proton/" }
}

// ...

dependencies {
    implementation("io.github.protonmc:tiny_config:master-SNAPSHOT")
}
```

Gradle Kotlin:
```kotlin
repositories {
    maven {
        setUrl("https://nexus.dyonb.nl/repository/proton/")
    }
}

// ...

dependencies {
    implementation("io.github.protonmc", "tiny_config", "master-SNAPSHOT")
}
```

If you're using this for a [Minecraft](https://minecraft.net/) [FabricMC](https://fabricmc.net/) mod you may also want to insert
```kotlin
include("io.github.protonmc:tiny_config:master-SNAPSHOT")
```
in the dependencies block.

Join [this Discord](https://discord.gg/qzGj4En) if you need any help.
