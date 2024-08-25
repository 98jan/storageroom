plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // needed for @Parcelize in BarcodeField.kt
    id("org.jetbrains.kotlin.plugin.parcelize")
    alias(libs.plugins.google.gms.google.services)
    // needed for sonarqube analysis
    id("org.sonarqube") version "5.0.0.4638"
    jacoco
}

android {
    namespace = "com.iu.storageroom"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iu.storageroom"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

sonar {
    properties {
        property("sonar.projectKey", "98jan_storageroom_cab43341-633c-4fab-a4d5-e59b14772719")
        property("sonar.host.url", System.getenv("SONAR_HOST_URL"))
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        property("sonar.androidLint.reportPaths", "build/reports/lint-results-debug.xml")
        property("sonar.junit.reportPaths", "build/test-results/testDebugUnitTest")
        property("sonar.java.binaries", "build")
    }
}

dependencies {

    api("com.google.guava:guava:32.0.1-jre")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.play.services.base)
    implementation(libs.core.ktx)

    // barcode
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.extensions)

    implementation(libs.preference.ktx)
    // Barcode model
    implementation(libs.barcode.scanning)
    // Object feature and model
    implementation(libs.objects.detection)
    // json processor
    implementation(libs.jackson.databind)

    // using glide for handling of the product images
    implementation (libs.glide)
    annotationProcessor(libs.compiler)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    //navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    description = "Generates the JaCoCo (Java Code Coverage) Report for SonarQube."
    group = "Reporting"
    dependsOn("testDebugUnitTest") // Hier wird sichergestellt, dass die Tests vor der Coverage-Generierung ausgeführt werden

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*", "android/**/*.*")

    val debugTree = fileTree("${project.buildDir}/intermediates/javac/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree("${project.buildDir}") {
        include(
            "jacoco/testDebugUnitTest.exec",
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
        )
    })
}

tasks.named("check") {
    dependsOn("jacocoTestReport")
}
