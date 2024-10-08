import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.google.devtools.ksp")
    id("com.mikepenz.aboutlibraries.plugin")
    id("dagger.hilt.android.plugin")
}

val acraAuthLogin: String =
    gradleLocalProperties(rootDir).getProperty("authLogin") ?: "\"acra_login\""
val acraAuthPassword: String =
    gradleLocalProperties(rootDir).getProperty("authPassword") ?: "\"acra_password\""

val shikimoriClientId: String =
    gradleLocalProperties(rootDir).getProperty("shikimoriClientId") ?: "\"shikimori\""
val shikimoriClientSecret: String =
    gradleLocalProperties(rootDir).getProperty("shikimoriClientSecret") ?: "\"shikimori\""

android {
    namespace = "org.xtimms.shirizu"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.xtimms.shirizu"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "COMMIT_COUNT", "\"${getCommitCount()}\"")
        buildConfigField("String", "COMMIT_SHA", "\"${getGitSha()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${getBuildTime()}\"")

        buildConfigField("String", "ACRA_URI", "\"https://bugs.kotatsu.app/report\"")
        buildConfigField("String", "ACRA_AUTH_LOGIN", acraAuthLogin)
        buildConfigField("String", "ACRA_AUTH_PASSWORD", acraAuthPassword)

        buildConfigField("String", "SHIKIMORI_CLIENT_ID", shikimoriClientId)
        buildConfigField("String", "SHIKIMORI_CLIENT_SECRET", shikimoriClientSecret)

        testInstrumentationRunner = "org.xtimms.shirizu.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        named("debug") {
            versionNameSuffix = "-${getCommitCount()}"
            applicationIdSuffix = ".debug"
        }
        named("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
            isDebuggable = false
            isProfileable = true
            versionNameSuffix = "-benchmark"
            applicationIdSuffix = ".benchmark"
        }
    }
    sourceSets {
        getByName("benchmark").res.srcDirs("src/debug/res")
    }
    flavorDimensions.add("default")
    productFlavors {
        create("standard") {
            dimension = "default"
        }
        create("dev") {
            dimension = "default"
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    // AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("dev.chrisbanes.compose:compose-bom:2024.05.00-alpha02"))
    implementation("androidx.compose.animation:animation-graphics")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended:1.6.5")
    implementation("androidx.compose.material3:material3-android:1.2.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    implementation("androidx.paging:paging-compose:3.2.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.room:room-testing:2.6.1")
    implementation("androidx.webkit:webkit:1.11.0")
    ksp("androidx.room:room-compiler:2.6.1")

    // Navigation
    implementation("cafe.adriel.voyager:voyager-navigator:1.1.0-alpha04")
    implementation("cafe.adriel.voyager:voyager-hilt:1.1.0-alpha04")
    implementation("cafe.adriel.voyager:voyager-screenmodel:1.1.0-alpha04")
    implementation("cafe.adriel.voyager:voyager-tab-navigator:1.1.0-alpha04")
    implementation("cafe.adriel.voyager:voyager-transitions:1.1.0-alpha04")

    // Motion
    implementation("io.github.fornewid:material-motion-compose-core:1.2.0")

    implementation("ch.acra:acra-http:5.9.7")
    implementation("com.github.solkin:disk-lru-cache:1.4")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.32.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    implementation("com.google.accompanist:accompanist-pager:0.32.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.32.0")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("com.github.KotatsuApp:kotatsu-parsers:b404b44008") {
        exclude(group = "org.json", module = "json")
    }
    implementation("com.mikepenz:aboutlibraries-compose-m3:10.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:4.12.0")
    implementation("com.squareup.okio:okio:3.8.0")
    implementation("com.tencent:mmkv:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("me.saket.telephoto:zoomable-image-coil:0.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("com.github.koitharu:workinspector:5778dd1747")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}

androidComponents {
    beforeVariants { variantBuilder ->
        // Disables standardBenchmark
        if (variantBuilder.buildType == "benchmark") {
            variantBuilder.enable = variantBuilder.productFlavors.containsAll(listOf("default" to "dev"))
        }
    }
    onVariants(selector().withFlavor("default" to "standard")) {
        // Only excluding in standard flavor because this breaks
        // Layout Inspector's Compose tree
        it.packaging.resources.excludes.add("META-INF/*.version")
    }
}

// Git is needed in your system PATH for these commands to work.
// If it's not installed, you can return a random value as a workaround
fun Project.getCommitCount(): String {
    return runCommand("git rev-list --count HEAD")
    // return "1"
}

fun Project.getGitSha(): String {
    return runCommand("git rev-parse --short HEAD")
    // return "1"
}

fun Project.getBuildTime(): String {
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
    df.timeZone = TimeZone.getTimeZone("UTC")
    return df.format(Date())
}

fun Project.runCommand(command: String): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        commandLine = command.split(" ")
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}