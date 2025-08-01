package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FunctionMinNameLengthSpec {

    @Test
    fun `should report a function name that is too short`() {
        val code = "fun a() = 3"
        assertThat(FunctionNameMinLength(Config.empty).lint(code)).hasSize(1)
    }

    @Test
    fun `should report a function name that is too short base on config`() {
        val code = "fun four() = 3"
        assertThat(
            FunctionNameMinLength(TestConfig("minimumFunctionNameLength" to 5))
                .lint(code)
        ).hasSize(1)
    }

    @Test
    fun `should not report a function name that is okay`() {
        val code = "fun three() = 3"
        assertThat(FunctionNameMinLength(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not report an overridden function name that is too short`() {
        val code = """
            class C : I {
                override fun tooShortButShouldNotBeReportedByDefault() {}
            }
            interface I { @Suppress("FunctionNameMinLength") fun tooShortButShouldNotBeReportedByDefault() }
        """.trimIndent()
        assertThat(
            FunctionNameMinLength(
                TestConfig("minimumFunctionNameLength" to 50)
            ).lint(code)
        ).isEmpty()
    }

    @Test
    fun `should not report an operator function`() {
        val code = """
            data class Point2D(var x: Int = 0, var y: Int = 0) {
                operator fun plus(another: Point2D): Point2D =
                    Point2D(x = x + another.x, y = y + another.y)
            }
        """.trimIndent()
        assertThat(
            FunctionNameMinLength(
                TestConfig("minimumFunctionNameLength" to 5)
            ).lint(code)
        ).isEmpty()
    }
}
