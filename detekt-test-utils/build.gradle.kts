plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.junit.jupiterApi)
    implementation(projects.detektKotlinAnalysisApi)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(projects.detektParser)
    implementation(libs.kotlin.mainKts) {
        isTransitive = false
    }
    runtimeOnly(libs.kotlin.scriptingCompiler)
    implementation(libs.kotlinx.coroutinesCore)

    testImplementation(libs.assertj.core)
}

apiValidation {
    ignoredPackages.add("dev.detekt.test.utils.internal")
}
