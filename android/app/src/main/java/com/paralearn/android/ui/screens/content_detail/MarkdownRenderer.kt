package com.paralearn.android.ui.screens.content_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.ui.theme.PrimaryCyan
import com.paralearn.android.ui.theme.SpaceGrotesk
import com.paralearn.android.ui.theme.appSurfaceLowest
import com.paralearn.android.ui.theme.appTextMainColor
import com.paralearn.android.ui.theme.appTextSecondaryColor

sealed class ContentBlock {
    data class Paragraph(val text: String) : ContentBlock()
    data class Heading(val text: String, val level: Int) : ContentBlock()
    data class Code(val code: String, val language: String) : ContentBlock()
}


fun parseMarkdown(content: String): List<ContentBlock> {
    val blocks = mutableListOf<ContentBlock>()
    val lines = content.lines()
    var inCodeBlock = false
    var currentLanguage = ""
    val currentCode = StringBuilder()
    val currentParagraph = StringBuilder()

    fun flushParagraph() {
        if (currentParagraph.isNotEmpty()) {
            blocks.add(ContentBlock.Paragraph(currentParagraph.toString().trimEnd()))
            currentParagraph.clear()
        }
    }

    for (line in lines) {
        val trimmed = line.trim()
        when {
            trimmed.startsWith("```") -> {
                if (inCodeBlock) {
                    blocks.add(ContentBlock.Code(currentCode.toString().trimEnd(), currentLanguage))
                    currentCode.clear()
                    inCodeBlock = false
                } else {
                    flushParagraph()
                    currentLanguage = trimmed.removePrefix("```").trim().lowercase()
                    inCodeBlock = true
                }
            }
            inCodeBlock -> currentCode.append(line).append("\n")
            trimmed.startsWith("### ") -> {
                flushParagraph()
                blocks.add(ContentBlock.Heading(trimmed.removePrefix("### ").trim(), 3))
            }
            trimmed.startsWith("## ") -> {
                flushParagraph()
                blocks.add(ContentBlock.Heading(trimmed.removePrefix("## ").trim(), 2))
            }
            trimmed.startsWith("# ") -> {
                flushParagraph()
                blocks.add(ContentBlock.Heading(trimmed.removePrefix("# ").trim(), 1))
            }
            trimmed.isBlank() -> flushParagraph()
            else -> {
                if (currentParagraph.isNotEmpty()) currentParagraph.append("\n")
                currentParagraph.append(line)
            }
        }
    }
    if (inCodeBlock) {
        blocks.add(ContentBlock.Code(currentCode.toString().trimEnd(), currentLanguage))
    } else {
        flushParagraph()
    }
    return blocks
}

/**
 * Parsea estilos inline estándar de Markdown (**negrita**, *itálica*, ~~tachado~~)
 * Corregido: Ya no invoca funciones dinámicas @Composable dentro del bucle.
 */
fun parseInlineStyles(text: String, defaultStyle: SpanStyle): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            when {
                // Negrita: **texto**
                text.startsWith("**", i) -> {
                    val endToken = text.indexOf("**", i + 2)
                    if (endToken != -1) {
                        withStyle(defaultStyle.copy(fontWeight = FontWeight.Bold)) {
                            append(text.substring(i + 2, endToken))
                        }
                        i = endToken + 2
                    } else {
                        withStyle(defaultStyle) { append("**") }
                        i += 2
                    }
                }
                // Tachado: ~~texto~~
                text.startsWith("~~", i) -> {
                    val endToken = text.indexOf("~~", i + 2)
                    if (endToken != -1) {
                        withStyle(defaultStyle.copy(textDecoration = TextDecoration.LineThrough)) {
                            append(text.substring(i + 2, endToken))
                        }
                        i = endToken + 2
                    } else {
                        withStyle(defaultStyle) { append("~~") }
                        i += 2
                    }
                }
                // Itálica: *texto*
                text.startsWith("*", i) -> {
                    val endToken = text.indexOf("*", i + 1)
                    if (endToken != -1) {
                        withStyle(defaultStyle.copy(fontStyle = FontStyle.Italic)) {
                            append(text.substring(i + 1, endToken))
                        }
                        i = endToken + 1
                    } else {
                        withStyle(defaultStyle) { append("*") }
                        i += 1
                    }
                }

                else -> {
                    withStyle(defaultStyle) { append(text[i].toString()) }
                    i++
                }
            }
        }
    }
}

fun highlightCode(code: String, language: String = ""): AnnotatedString {
    return buildAnnotatedString {
        val keywords = setOf(
            "use", "fn", "let", "mut", "pub", "impl", "struct", "enum",
            "for", "in", "if", "else", "match", "return", "self", "super",
            "crate", "as", "const", "static", "type", "where", "unsafe",
            "import",
            "from",
            "def",
            "class",
            "async",
            "await",
            "__global__",
            "__shared__",
            "function",
            "var",
            "export",
            "interface",
            "namespace",
            "module",
            "extends",
            "implements",
            "new",
            "this",
            "typeof",
            "instanceof",
            "switch",
            "case",
            "break",
            "default",
            "try",
            "catch",
            "finally"
        )

        val types = setOf(
            "i32",
            "i64",
            "u32",
            "u64",
            "f32",
            "f64",
            "usize",
            "bool",
            "char",
            "str",
            "String",
            "Vec",
            "float",
            "string",
            "number",
            "boolean",
            "any",
            "unknown",
            "void",
            "never",
            "object",
            "Array",
            "Promise",
            "Map",
            "Set"
        )

        code.lines().forEachIndexed { lineIdx, line ->
            var remaining = line
            while (remaining.isNotEmpty()) {
                if (remaining.startsWith("//") || remaining.startsWith("#")) {
                    withStyle(style = SpanStyle(color = Color(0xFF65737E))) { append(remaining) }
                    remaining = ""
                } else {
                    val matchResult = Regex("^(\\w+|\\s+|\\W)").find(remaining)
                    if (matchResult != null) {
                        val token = matchResult.value
                        remaining = remaining.substring(token.length)
                        when {
                            token.matches(Regex("^\\s+$")) -> append(token)
                            keywords.contains(token) -> withStyle(SpanStyle(color = Color(0xFFC792EA))) { append(token) }
                            types.contains(token) -> withStyle(SpanStyle(color = Color(0xFFF78C6C))) { append(token) }
                            token.matches(Regex("^\\d+\\w*$")) -> withStyle(SpanStyle(color = Color(0xFFF78C6C))) { append(token) }
                            token.startsWith("\"") || token.startsWith("'") || token.startsWith("`") -> withStyle(
                                SpanStyle(color = Color(0xFFC3E88D))
                            ) { append(token) }
                            else -> append(token)
                        }
                    } else {
                        append(remaining.first())
                        remaining = remaining.drop(1)
                    }
                }
            }
            if (lineIdx < code.lines().size - 1) append("\n")
        }
    }
}

@Composable
fun MarkdownContent(
    markdown: String,
    modifier: Modifier = Modifier,
    emptyText: String = "No content available."
) {
    val blocks = remember(markdown) { parseMarkdown(markdown.ifBlank { emptyText }) }

    // Resolvemos los colores en el contexto Composable superior seguro
    val secondaryTextColor = appTextSecondaryColor()
    val mainTextColor = appTextMainColor()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        blocks.forEach { block ->
            when (block) {
                is ContentBlock.Paragraph -> {
                    Text(
                        text = remember(block.text, secondaryTextColor) {
                            parseInlineStyles(block.text, SpanStyle(color = secondaryTextColor))
                        },
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
                is ContentBlock.Heading -> {
                    val headingColor = if (block.level <= 2) PrimaryCyan else mainTextColor
                    Text(
                        text = remember(block.text, headingColor) {
                            parseInlineStyles(block.text, SpanStyle(color = headingColor))
                        },
                        fontSize = when (block.level) {
                            1 -> 20.sp
                            2 -> 17.sp
                            else -> 15.sp
                        },
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold
                    )
                }
                is ContentBlock.Code -> {
                    MarkdownCodeBlock(
                        code = block.code,
                        language = block.language
                    )
                }
            }
        }
    }
}

@Composable
private fun MarkdownCodeBlock(code: String, language: String) {
    val extension = when (language.lowercase()) {
        "typescript", "ts" -> "ts"
        "javascript", "js" -> "js"
        "rust", "rs" -> "rs"
        "cpp", "cpp", "c" -> language.lowercase()
        else -> if (language.isBlank()) "txt" else language
    }
    val fileName = "main.$extension"
    val secondaryTextColor = appTextSecondaryColor()
    val surfaceColor = appSurfaceLowest()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceColor.copy(alpha = 0.9f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF040E1F))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFF5F56)))
                Box(modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFFBD2E)))
                Box(modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF27C93F)))
            }
            Text(
                text = fileName,
                color = secondaryTextColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(20.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = remember(code, language) { highlightCode(code, language) },
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun MarkdownTabPanel(
    explanationMarkdown: String,
    useCasesMarkdown: String?,
    showUseCasesTab: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ContentDetailTab.EXPLANATION) }
    val surfaceColor = appSurfaceLowest()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1F2A3C))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MarkdownTabButton(
                label = "Explanation",
                selected = selectedTab == ContentDetailTab.EXPLANATION,
                onClick = { selectedTab = ContentDetailTab.EXPLANATION },
                modifier = Modifier.weight(1f)
            )
            if (showUseCasesTab) {
                MarkdownTabButton(
                    label = "Use Cases",
                    selected = selectedTab == ContentDetailTab.USE_CASES,
                    onClick = { selectedTab = ContentDetailTab.USE_CASES },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Box(modifier = Modifier.padding(20.dp)) {
            when (selectedTab) {
                ContentDetailTab.EXPLANATION -> MarkdownContent(explanationMarkdown)
                ContentDetailTab.USE_CASES -> MarkdownContent(useCasesMarkdown.orEmpty(), emptyText = "No use cases documented.")
            }
        }
    }
}

@Composable
private fun MarkdownTabButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = appSurfaceLowest()
    val mainTextColor = appTextMainColor()
    val secondaryTextColor = appTextSecondaryColor()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(if (selected) surfaceColor else Color.Transparent)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) PrimaryCyan else Color.Transparent,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontFamily = SpaceGrotesk,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) mainTextColor else secondaryTextColor
        )
    }
}