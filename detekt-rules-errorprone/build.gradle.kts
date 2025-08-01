plugins {
    id("module")
}

dependencies {
    compileOnly(projects.detektApi)
    compileOnly(projects.detektKotlinAnalysisApi)
    compileOnly(projects.detektPsiUtils)
    testImplementation(projects.detektTest)
    testImplementation(libs.assertj.core)
}
