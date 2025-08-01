package dev.detekt.tooling.dsl

import dev.detekt.api.Severity
import dev.detekt.tooling.api.spec.RulesSpec
import dev.detekt.tooling.api.spec.RulesSpec.FailurePolicy

class RulesSpecBuilder : Builder<RulesSpec> {

    var activateAllRules: Boolean = false
    var failurePolicy: FailurePolicy = FailurePolicy.FailOnSeverity(Severity.Error)
    var autoCorrect: Boolean = false
    var runPolicy: RulesSpec.RunPolicy = RulesSpec.RunPolicy.NoRestrictions

    override fun build(): RulesSpec = RulesModel(
        activateAllRules,
        failurePolicy,
        autoCorrect,
        runPolicy
    )
}

private data class RulesModel(
    override val activateAllRules: Boolean,
    override val failurePolicy: FailurePolicy,
    override val autoCorrect: Boolean,
    override val runPolicy: RulesSpec.RunPolicy,
) : RulesSpec
