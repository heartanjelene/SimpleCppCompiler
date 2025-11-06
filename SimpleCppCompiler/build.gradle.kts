import org.gradle.jvm.tasks.Jar

plugins {
    id("java")
    id("antlr")
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.13.1")
    implementation("org.antlr:antlr4-runtime:4.13.1")
}

application {
    mainClass.set("com.simplecpp.compiler.CompilerMain")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

sourceSets {
    main {
        java.srcDir("$buildDir/generated-src/antlr/main")
    }
}

tasks.generateGrammarSource {
    arguments.addAll(listOf("-visitor", "-no-listener"))
    outputDirectory = file("$buildDir/generated-src/antlr/main/com/simplecpp/compiler/grammar")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.register<Jar>("fatJar") {
    dependsOn(tasks.build)
    archiveClassifier.set("all")
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    manifest { attributes["Main-Class"] = "com.simplecpp.compiler.CompilerMain" }
}

tasks.register<JavaExec>("syntaxCheck") {
    group = "application"
    description = "Run the robust syntax checker (NiceSyntaxErrorListener)"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.simplecpp.compiler.tools.SyntaxCheckMain")

    val fileToCheck = (project.findProperty("file") as String?) ?: "examples/PL2.cpp"
    args(fileToCheck)
}

tasks.register<JavaExec>("compileExample") {
    group = "application"
    description = "Compile an example .cpp file to LLVM IR (.ll)"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.simplecpp.compiler.CompilerMain")

    val src = (project.findProperty("src") as String?) ?: "examples/PL2.cpp"
    val out = (project.findProperty("out") as String?) ?: "build/PL2.ll"
    args(src, "-o", out)
}

