package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresFullAnalysis
import dev.detekt.api.Rule
import dev.detekt.psi.hasImplicitParameterReference
import dev.detekt.psi.implicitParameter
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Disallows shadowing variable declarations.
 * Shadowing makes it impossible to access a variable with the same name in the scope.
 *
 * <noncompliant>
 * fun test(i: Int, j: Int, k: Int) {
 *     val i = 1
 *     val (j, _) = 1 to 2
 *     listOf(1).map { k -> println(k) }
 *     listOf(1).forEach {
 *         listOf(2).forEach {
 *         }
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun test(i: Int, j: Int, k: Int) {
 *     val x = 1
 *     val (y, _) = 1 to 2
 *     listOf(1).map { z -> println(z) }
 *     listOf(1).forEach {
 *         listOf(2).forEach { x ->
 *         }
 *     }
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class NoNameShadowing(config: Config) :
    Rule(
        config,
        "Disallow shadowing variable declarations."
    ),
    RequiresFullAnalysis {

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        checkNameShadowing(property)
    }

    override fun visitDestructuringDeclarationEntry(multiDeclarationEntry: KtDestructuringDeclarationEntry) {
        super.visitDestructuringDeclarationEntry(multiDeclarationEntry)
        checkNameShadowing(multiDeclarationEntry)
    }

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)
        checkNameShadowing(parameter)
    }

    private fun checkNameShadowing(declaration: KtNamedDeclaration) {
        val nameIdentifier = declaration.nameIdentifier ?: return
        if (bindingContext.diagnostics.forElement(declaration).any { it.factory == Errors.NAME_SHADOWING }) {
            report(Finding(Entity.from(nameIdentifier), "Name shadowed: ${nameIdentifier.text}"))
        }
    }

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        val implicitParameter = lambdaExpression.implicitParameter(bindingContext) ?: return
        if (lambdaExpression.hasImplicitParameterReference(implicitParameter, bindingContext) &&
            lambdaExpression.hasParentImplicitParameterLambda()
        ) {
            report(
                Finding(
                    Entity.from(lambdaExpression),
                    "Name shadowed: implicit lambda parameter 'it'"
                )
            )
        }
    }

    private fun KtLambdaExpression.hasParentImplicitParameterLambda(): Boolean =
        getStrictParentOfType<KtLambdaExpression>()?.implicitParameter(bindingContext) != null
}
