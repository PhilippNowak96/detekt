package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.UnnecessaryParenthesesBeforeTrailingLambdaRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#unnecessary-parenthesis-before-trailing-lambda)
 * for documentation.
 */
@ActiveByDefault(since = "1.23.0")
@AutoCorrectable(since = "1.20.0")
class UnnecessaryParenthesesBeforeTrailingLambda(config: Config) : FormattingRule(
    config,
    "Ensures there are no unnecessary parentheses before a trailing lambda"
) {

    override val wrapping = UnnecessaryParenthesesBeforeTrailingLambdaRule()
}
