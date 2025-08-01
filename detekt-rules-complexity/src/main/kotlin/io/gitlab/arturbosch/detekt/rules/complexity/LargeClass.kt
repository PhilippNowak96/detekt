package io.gitlab.arturbosch.detekt.rules.complexity

import com.intellij.psi.util.PsiTreeUtil
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import io.github.detekt.metrics.linesOfCode
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import java.util.IdentityHashMap

/**
 * This rule reports large classes. Classes should generally have one responsibility. Large classes can indicate that
 * the class does instead handle multiple responsibilities. Instead of doing many things at once prefer to
 * split up large classes into smaller classes. These smaller classes are then easier to understand and handle less
 * things.
 */
@ActiveByDefault(since = "1.0.0")
class LargeClass(config: Config) : Rule(
    config,
    "One class should have one responsibility. Large classes tend to handle many things at once. " +
        "Split up large classes into smaller classes that are easier to understand."
) {

    @Configuration("The maximum number of lines allowed per class.")
    private val allowedLines: Int by config(defaultValue = 600)

    private val classToLinesCache = IdentityHashMap<KtClassOrObject, Int>()
    private val nestedClassTracking = IdentityHashMap<KtClassOrObject, HashSet<KtClassOrObject>>()

    override fun preVisit(root: KtFile) {
        classToLinesCache.clear()
        nestedClassTracking.clear()
    }

    override fun postVisit(root: KtFile) {
        for ((clazz, lines) in classToLinesCache) {
            if (lines > allowedLines) {
                report(
                    Finding(
                        Entity.atName(clazz),
                        "Class ${clazz.name} is too large. Consider splitting it into smaller pieces."
                    )
                )
            }
        }
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        val lines = classOrObject.linesOfCode()
        classToLinesCache[classOrObject] = lines
        classOrObject.getStrictParentOfType<KtClassOrObject>()
            ?.let { nestedClassTracking.getOrPut(it) { HashSet() }.add(classOrObject) }
        super.visitClassOrObject(classOrObject)

        PsiTreeUtil.findChildrenOfType(classOrObject, KtClassOrObject::class.java)
            .fold(0) { acc, next -> acc + (classToLinesCache[next] ?: 0) }
            .takeIf { it > 0 }
            ?.let { classToLinesCache[classOrObject] = lines - it }
    }
}
