package dev.detekt.rules.exceptions

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType

/**
 * This rule reports all cases where exceptions are thrown from a `finally` block. Throwing exceptions from a `finally`
 * block should be avoided as it can lead to confusion and discarded exceptions.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } finally {
 *         throw IOException()
 *     }
 * }
 * </noncompliant>
 */
@ActiveByDefault(since = "1.16.0")
class ThrowingExceptionFromFinally(config: Config) : Rule(
    config,
    "Do not throw an exception within a finally statement. This can discard exceptions and is confusing."
) {

    override fun visitFinallySection(finallySection: KtFinallySection) {
        finallySection.finalExpression.forEachDescendantOfType<KtThrowExpression> {
            report(Finding(Entity.from(it), description))
        }
    }
}
