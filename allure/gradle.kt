import io.qameta.allure.gradle.AllureExtension

configure<AllureExtension> {
    version.set("2.17.2")
    autoconfigure.set(true)
    aspectjweaver.set(true)
}
