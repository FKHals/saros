plugins {
  id("saros.gradle.eclipse.plugin")
}

val versionQualifier = ext.get("versionQualifier") as String

val log4j2ApiVersion = ext.get("log4j2ApiVersion") as String
val log4j2CoreVersion = ext.get("log4j2CoreVersion") as String
val log4j2BridgeVersion = ext.get("log4j2BridgeVersion") as String

configurations {
    // Defined in root build.gradle
    val testConfig by getting {}
    val releaseDep by getting {}

    // Default configuration
    val compile by getting {
        extendsFrom(releaseDep)
    }
    val testCompile by getting {
        extendsFrom(testConfig)
    }
    val plain by creating {
        extendsFrom(compile)
    }
}

sarosEclipse {
    manifest = file("META-INF/MANIFEST.MF")
    excludeManifestDependencies = listOf("saros.libratory")
    isCreateBundleJar = true
    isAddPdeNature = true
    pluginVersionQualifier = versionQualifier
}

dependencies {
    implementation(project(":saros.libratory"))

    releaseDep("commons-codec:commons-codec:1.3")
    releaseDep("commons-io:commons-io:2.0.1")
    releaseDep("org.apache.commons:commons-lang3:3.8.1")

    releaseDep("javax.jmdns:jmdns:3.4.1")
    releaseDep("xpp3:xpp3:1.1.4c")
    releaseDep("com.thoughtworks.xstream:xstream:1.4.10")
    releaseDep("org.gnu.inet:libidn:1.15")
}

sourceSets {
    main {
        java.srcDirs("src", "patches")
        resources.srcDirs("resources")
    }
    test {
        java.srcDirs("test/junit")
    }
}

tasks {

    val testJar by registering(Jar::class) {
        classifier = "tests"
        from(sourceSets["test"].output)
    }

    // Jar containing only the core code (the default jar is an osgi bundle
    // containing a lib dir with all dependency jars)
    val plainJar by registering(Jar::class) {
        manifest {
            from("META-INF/MANIFEST.MF")
        }
        from(sourceSets["main"].output)
        classifier = "plain"
    }

    artifacts {
        add("testing", testJar)
        add("plain", plainJar)
    }
}
