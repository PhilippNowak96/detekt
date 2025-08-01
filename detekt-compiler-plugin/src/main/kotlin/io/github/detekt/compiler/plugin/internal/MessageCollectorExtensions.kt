package io.github.detekt.compiler.plugin.internal

import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.suppressed
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

fun MessageCollector.info(msg: String) {
    this.report(CompilerMessageSeverity.INFO, msg)
}

fun MessageCollector.warn(msg: String, location: CompilerMessageSourceLocation? = null) {
    this.report(CompilerMessageSeverity.WARNING, msg, location)
}

fun MessageCollector.error(msg: String) {
    this.report(CompilerMessageSeverity.ERROR, msg)
}

fun MessageCollector.reportIssues(result: Detektion) {
    result.issues
        .filterNot { it.suppressed }
        .sortedBy { it.location }
        .forEach { issue ->
            val (message, location) = issue.renderAsCompilerWarningMessage()
            warn(message, location)
        }
}

fun Issue.renderAsCompilerWarningMessage(): Pair<String, CompilerMessageLocation?> {
    val sourceLocation = CompilerMessageLocation.create(
        entity.location.path.toString(),
        entity.location.source.line,
        entity.location.source.column,
        null
    )

    return "${ruleInstance.id}: $message" to sourceLocation
}
