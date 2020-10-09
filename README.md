![Nexus Repository](https://img.shields.io/nexus/proton/io.github.protonmc/tiny-config?server=https%3A%2F%2Fnexus.dyonb.nl%2F)
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
    implementation("io.github.protonmc:tiny_config:version")
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
    implementation("io.github.protonmc", "tiny_config", "version")
}
```

If you're using this for a [Minecraft](https://minecraft.net/) [FabricMC](https://fabricmc.net/) mod you may also want to insert
```kotlin
include("io.github.protonmc:tiny_config:version")
```
in the dependencies block.

Join [this Discord](https://discord.gg/qzGj4En) if you need any help.
