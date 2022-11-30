plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}
apply(plugin = "dagger.hilt.android.plugin")
apply(plugin = "androidx.navigation.safeargs.kotlin")
apply(plugin = "com.bugsnag.android.gradle")

android {
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        applicationId = ProjectConfig.packageName

        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk

        versionCode = ProjectConfig.Version.code
        versionName = ProjectConfig.Version.name

        testInstrumentationRunner = "eu.darken.sdmse.HiltTestRunner"

        buildConfigField("String", "PACKAGENAME", "\"${ProjectConfig.packageName}\"")
        buildConfigField("String", "GITSHA", "\"${lastCommitHash()}\"")
        buildConfigField("String", "BUILDTIME", "\"${buildTime()}\"")
        buildConfigField("String", "VERSION_CODE", "\"${ProjectConfig.Version.code}\"")
        buildConfigField("String", "VERSION_NAME", "\"${ProjectConfig.Version.name}\"")

        manifestPlaceholders["bugsnagApiKey"] = getBugSnagApiKey(
            File(System.getProperty("user.home"), ".appconfig/${ProjectConfig.packageName}/bugsnag.properties")
        ) ?: "fake"
    }

    signingConfigs {
        val basePath = File(System.getProperty("user.home"), ".appconfig/${ProjectConfig.packageName}")
        create("releaseFoss") {
            setupCredentials(File(basePath, "signing-foss.properties"))
        }
        create("releaseGplay") {
            setupCredentials(File(basePath, "signing-gplay-upload.properties"))
        }
    }

    flavorDimensions.add("version")
    productFlavors {
        create("foss") {
            dimension = "version"
            signingConfig = signingConfigs["releaseFoss"]
        }
        create("gplay") {
            dimension = "version"
            signingConfig = signingConfigs["releaseGplay"]
        }
    }

    buildTypes {
        val customProguardRules = fileTree(File(projectDir, "proguard")) {
            include("*.pro")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFiles(*customProguardRules.toList().toTypedArray())
            proguardFiles("proguard-rules-debug.pro")
        }
        create("beta") {
            lint {
                abortOnError = true
                fatal.add("StopShip")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFiles(*customProguardRules.toList().toTypedArray())
        }
        release {
            lint {
                abortOnError = true
                fatal.add("StopShip")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFiles(*customProguardRules.toList().toTypedArray())
        }
    }

    buildOutputs.all {
        val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        val variantName: String = variantOutputImpl.name

        if (listOf("release", "beta").any { variantName.toLowerCase().contains(it) }) {
            val outputFileName = ProjectConfig.packageName +
                    "-v${defaultConfig.versionName}-${defaultConfig.versionCode}" +
                    "-${variantName.toUpperCase()}-${lastCommitHash()}.apk"

            variantOutputImpl.outputFileName = outputFileName
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    setupKotlinOptions()

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }

    sourceSets {
        getByName("test") {
        }
        getByName("androidTest") {
            assets.srcDirs(files("$projectDir/schemas"))
        }
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${Versions.Desugar.core}")

    implementation(project(":app-common"))
    testImplementation(project(":app-common-test"))
    implementation(project(":app-common-root"))
    implementation(project(":app-common-io"))

    addDI()
    addCoroutines()
    addSerialization()
    addIO()
    addRetrofit()

    "gplayImplementation"("com.bugsnag:bugsnag-android:5.9.2")
    "gplayImplementation"("com.getkeepsafe.relinker:relinker:1.4.3")

    "gplayImplementation"("com.android.billingclient:billing:4.0.0")

    addAndroidCore()
    addAndroidUI()
    addWorkerManager()

    addTesting()

    implementation("com.airbnb.android:lottie:3.5.0")

    implementation("io.coil-kt:coil:2.0.0-rc02")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.5.0")
}