plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
}

android {
	namespace = "com.tii.dcsmartwatchcode"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.tii.dcsmartwatchcode"
		minSdk = 30
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

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
		compose = true
	}
}

dependencies {

	implementation(libs.play.services.wearable)
	implementation(platform(libs.compose.bom))
	implementation(libs.ui)
	implementation(libs.ui.graphics)
	implementation(libs.ui.tooling.preview)
	implementation(libs.compose.material)
	implementation(libs.compose.foundation)
	implementation(libs.wear.tooling.preview)
	implementation(libs.activity.compose)
	implementation(libs.core.splashscreen)
	implementation(libs.tiles)
	implementation(libs.tiles.material)
	implementation(libs.tiles.tooling.preview)
	implementation(libs.horologist.compose.tools)
	implementation(libs.horologist.tiles)
	implementation(libs.watchface.complications.data.source.ktx)
	androidTestImplementation(platform(libs.compose.bom))
	androidTestImplementation(libs.ui.test.junit4)
	debugImplementation(libs.ui.tooling)
	debugImplementation(libs.ui.test.manifest)
	debugImplementation(libs.tiles.tooling)
	implementation("androidx.health:health-services-client:1.1.0-alpha03")

}