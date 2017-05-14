package de.mhlz.kotlint.rules

import de.mhlz.kotlint.Config
import de.mhlz.kotlint.Emitter
import de.mhlz.kotlint.ProblemLevel
import de.mhlz.kotlint.Rule
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Mischa Holz
 */
class MaximumLineLengthRule : Rule("maximum-line-length") {

    private val maxLength: Int = Config["maximum-line-length"]["length"] as Int

    override fun lint(node: PsiElement, emit: Emitter) {
        if (node is KtFile) {
            val text = node.text

            val lines = text.split("\n")

            val importStatements = node.node.getChildren(TokenSet.create(KtNodeTypes.IMPORT_LIST))
            val importTextRange = importStatements.firstOrNull()?.textRange

            lines.forEachIndexed { i, line ->
                val offset = lines.subList(0, i).map { it.length + 1 }.sum()

                val partOfImportList = importTextRange?.contains(offset) ?: false

                if (line.length < maxLength || partOfImportList) return@forEachIndexed

                emit(
                        offset,
                        ProblemLevel.error,
                        "This line is too long. Shorten it to less than $maxLength chars",
                        null
                )
            }
        }
    }
}
