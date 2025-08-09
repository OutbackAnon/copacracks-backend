group = "com.copacracks"
version = "1.0-SNAPSHOT"

plugins {
    id("java")
    checkstyle
    id("com.diffplug.spotless") version "6.20.0"
    jacoco
    pmd
    id("com.github.spotbugs") version "5.0.13"
}

pmd {
    toolVersion = "6.56.0"
    ruleSets = listOf("category/java/bestpractices.xml", "category/java/codestyle.xml")
    isConsoleOutput = true
    isIgnoreFailures = false
}

spotbugs {
    toolVersion = "4.7.3"
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.Confidence.HIGH)
    ignoreFailures.set(false)
    showProgress.set(true)
}

jacoco {
    toolVersion = "0.8.10"
}

checkstyle {
    toolVersion = "11.0.0"
    configFile = file("config/checkstyle/checkstyle.xml")
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
    implementation("io.javalin:javalin-bundle:6.7.0")

    implementation("com.google.googlejavaformat:google-java-format:1.28.0")
    implementation("com.puppycrawl.tools:checkstyle:11.0.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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