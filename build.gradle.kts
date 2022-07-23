configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
  sourceSets {
	val commonMain by getting {
	  dependencies {
		implementations(
		  libs.kotlinx.html.common,
		  libs.kotlinx.serialization.json,
		  KSubProject.klib.path.auto(), // this is just an example, feel free to remove
		  handler = this
		)
	  }
	}
  }
}