plugins {
    id("java")
}

group = "org.bbq"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    compileOnly("net.minestom:minestom-snapshots:96bf14500a")
}
