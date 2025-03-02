plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    id("androidx.navigation.safeargs")
}


android {
    namespace = "com.example.footplanner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.footplanner"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
}
dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.lottie)
    implementation (libs.dotsindicator)
    implementation (libs.viewpager2)
    implementation (libs.material.v190)
    implementation (libs.firebase.auth)
    implementation (libs.play.services.auth)
    implementation (libs.facebook.login)
    implementation (libs.room.runtime)
    annotationProcessor (libs.room.compiler)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.auth)
    implementation (libs.firebase.auth)
    implementation (libs.firebase.core)
    implementation (libs.facebook.login.vlatestrelease)
    implementation (libs.shimmer)
    implementation(libs.recyclerview)

    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.adapter.rxjava3)
    implementation(libs.room.runtime)
    implementation(libs.room.rxjava3)
    annotationProcessor(libs.room.compiler)
    implementation (libs.carouselrecyclerview)
    implementation (libs.circleimageview)
    implementation (libs.firebase.firestore)
    implementation (libs.play.services.base)
    implementation(platform(libs.firebase.bom.v33100))
    implementation(libs.google.firebase.firestore)

}