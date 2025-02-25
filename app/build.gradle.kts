plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("kotlin-parcelize")
    alias(libs.plugins.kotlinKsp)
}

android {
    namespace = "com.test.zomato"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.test.zomato"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // country code picker
    implementation("com.hbb20:ccp:2.7.0")

    // for otp box
    implementation("com.github.GauravMeghanathiWeDoApps:OtpView:1.0.1")


    // Phone Selector API is used to detect phone numbers being used in the phone
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // remove country code
    implementation("com.googlecode.libphonenumber:libphonenumber:8.12.32")

    implementation("com.github.bumptech.glide:glide:4.11.0")

    // image slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // get location
    // implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    // rating bar
    implementation("com.github.wdsqjq:AndRatingBar:1.0.6")


    // retrofit


    implementation ("com.squareup.retrofit2:retrofit:2.9.0")


    // GSON


    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")


    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.5.0")
    implementation("androidx.room:room-ktx:2.2.1")


    implementation("nl.dionsegijn:konfetti-xml:2.0.5")
    implementation("com.github.dhaval2404:imagepicker:2.1")

}