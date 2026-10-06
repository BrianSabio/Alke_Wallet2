plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.alkewallet"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.alkewallet"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    systemProperty("net.bytebuddy.experimental", "true")
}

dependencies {
    // Ya existentes en el proyecto
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Retrofit & Gson (Red REST)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)

    // Room (Persistencia Local)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Picasso (Carga Asíncrona de Imágenes)
    implementation(libs.picasso)

    // Architecture Components: Lifecycle, ViewModel & LiveData
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    // Testing Unitario e Integración
    testImplementation(libs.arch.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockwebserver)
}
