package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaFunctionType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

/**
 * Reports when property with 'is' prefix doesn't have a boolean type.
 * Please check [chapter 8.3.2 on the Java Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.3.2)
 *
 * <noncompliant>
 * val isEnabled: Int = 500
 * </noncompliant>
 *
 * <compliant>
 * val isEnabled: Boolean = false
 * </compliant>
 */
class NonBooleanPropertyPrefixedWithIs(config: Config) :
    Rule(
        config,
        "Only boolean property names can start with `is` prefix."
    ),
    RequiresAnalysisApi {

    private val booleanTypes = listOf(
        "kotlin.Boolean",
        "java.lang.Boolean",
        "java.util.concurrent.atomic.AtomicBoolean",
    ).map { FqName(it) }.toSet()

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)

        if (parameter.hasValOrVar()) {
            validateDeclaration(parameter)
        }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        validateDeclaration(property)
    }

    private fun validateDeclaration(declaration: KtCallableDeclaration) {
        val name = declaration.name ?: return
        if (name.startsWith("is") && name.getOrNull(2)?.isUpperCase() == true) {
            analyze(declaration) {
                val type = declaration.returnType.let { (it as? KaFunctionType)?.returnType ?: it }
                val typeFqName = type.symbol?.classId?.asSingleFqName() ?: return
                if (typeFqName !in booleanTypes) {
                    report(declaration, name, typeFqName)
                }
            }
        }
    }

    private fun report(declaration: KtCallableDeclaration, name: String, typeFqName: FqName) {
        val typeName = typeFqName.shortName().asString()
        val finding = Finding(
            Entity.from(declaration),
            message = "Non-boolean properties shouldn't start with 'is' prefix. Actual type of $name: $typeName"
        )
        report(finding)
    }
}
