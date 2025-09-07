import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort

group = "com.copacracks"
version = "1.0-SNAPSHOT"

plugins {
    id("java")
    alias(libs.plugins.spotless)
    alias(libs.plugins.spotbugs)
    alias(libs.plugins.flyway)
    alias(libs.plugins.rewrite)
    checkstyle
    jacoco
    pmd
}

pmd {
    toolVersion = "7.16.0"
    ruleSetFiles = files("config/pmd/pmd-ruleset.xml")
    isConsoleOutput = true
    isIgnoreFailures = false
}

rewrite {
    activeRecipe("org.openrewrite.staticanalysis.CodeCleanup")
}

spotbugs {
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.HIGH)
    ignoreFailures.set(false)
    showProgress.set(true)
}

jacoco {
    toolVersion = "0.8.10"
}

checkstyle {
    toolVersion = "11.0.0"
    configProperties["org.checkstyle.google.suppressionfilter.config"] =
        file("config/checkstyle/suppressions.xml").absolutePath
}

spotless {
    java {
        googleJavaFormat("1.28.0")
        target("src/**/*.java")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.javalin.bundle)
    implementation(libs.google.java.format)
    implementation(libs.checkstyle)
    implementation(libs.guice)
    implementation(libs.hikari)
    implementation(libs.postgre)
    implementation(libs.bundles.flyway)
    implementation(libs.bouncycastle)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    spotbugsPlugins(libs.findsecbugs.plugin)
    // rewrite(libs.rewrite.recipe.staticanalysis)
    rewrite("org.openrewrite.recipe:rewrite-static-analysis:latest.release")

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.assertj)
    testImplementation(libs.mockito)
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("org.apache.commons:commons-lang3:3.8.1")).using(module("org.apache.commons:commons-lang3:3.18.0"))
    }
}

tasks.withType<Pmd> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<Checkstyle> {
    reports {
        xml.required.set(false)
        html.required.set(true)
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
    }
}

tasks.register("codeQuality") {
    dependsOn(tasks.named("pmdMain"), tasks.named("spotbugsMain"))
}

tasks.register("preCommitCheck") {
    group = "verification"
    description = "Executa verificação antes do commit"
    dependsOn("spotlessApply", "checkstyleMain", "test")
}

tasks.register<Copy>("installGitHooks") {
    from(file("scripts/pre-commit"))
    into(file(".git/hooks"))
    fileMode = 0b111101101 // 755
}

tasks.named("build") {
    dependsOn("installGitHooks")
}
