package com.example.techfix.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.techfix.data.REVIEW_COMMENT_LIMIT
import com.example.techfix.data.local.ServiceReview
import com.example.techfix.data.local.ServiceReviewEntity
import com.example.techfix.ui.theme.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

internal fun formatReviewScore(score: Double): String =
    String.format(Locale("pt", "BR"), "%.1f", score)

internal fun reviewCountLabel(count: Int): String =
    if (count == 1) "1 avaliação" else "$count avaliações"

@Composable
internal fun ReviewSummary(reviews: List<ServiceReview>) {
    if (reviews.isEmpty()) return

    val average = reviews.map { it.stars }.average()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "${formatReviewScore(average)} de 5",
            style = MaterialTheme.typography.titleLarge,
            color = TechFixTextPrimary,
            fontWeight = FontWeight.Bold
        )
        ReviewStars(average.roundToInt())
        Text(reviewCountLabel(reviews.size), color = TechFixTextSecondary)
    }
}

@Composable
internal fun ReviewListButton(
    reviews: List<ServiceReview>,
    title: String,
    label: String = "Ver avaliações"
) {
    var open by rememberSaveable { mutableStateOf(false) }
    TextButton(
        onClick = { open = true },
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        Text(label, color = TechFixAccentSolid, fontWeight = FontWeight.SemiBold)
    }
    if (open) {
        ReviewListDialog(reviews, title, onClose = { open = false })
    }
}

@Composable
private fun ReviewListDialog(reviews: List<ServiceReview>, title: String, onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = TechFixSurface,
            border = BorderStroke(1.dp, TechFixBorder)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            color = TechFixTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onClose) {
                            Icon(Icons.Filled.Close, contentDescription = "Fechar avaliações", tint = TechFixTextPrimary)
                        }
                    }
                }
                if (reviews.isEmpty()) {
                    item { Text("Nenhuma avaliação registrada neste aparelho.", color = TechFixTextSecondary) }
                } else {
                    item { ReviewSummary(reviews) }
                    items(reviews) { review ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = TechFixSurfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    review.authorName.trim().substringBefore(' ').ifBlank { "Usuário TechFix" },
                                    color = TechFixTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                ReviewStars(review.stars, describeRating = true)
                                if (review.comment.isNotEmpty()) {
                                    Text(review.comment, color = TechFixTextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun OrderReviewCard(
    title: String,
    submitted: ServiceReviewEntity?,
    onSubmit: suspend (Int, String) -> Boolean
) {
    var stars by rememberSaveable { mutableIntStateOf(0) }
    var comment by rememberSaveable { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    TechFixCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                if (submitted == null) title else "Sua avaliação",
                style = MaterialTheme.typography.titleMedium,
                color = TechFixTextPrimary,
                fontWeight = FontWeight.Bold
            )
            if (submitted != null) {
                ReviewStars(submitted.stars, describeRating = true)
                if (submitted.comment.isNotEmpty()) {
                    Text(submitted.comment, color = TechFixTextSecondary)
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth().selectableGroup()) {
                    (1..5).forEach { value ->
                        Box(
                            modifier = Modifier.weight(1f)
                                .heightIn(min = 48.dp)
                                .selectable(selected = stars == value, role = Role.RadioButton, enabled = !sending) {
                                    stars = value
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (value <= stars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = if (value == 1) "1 estrela" else "$value estrelas",
                                tint = if (value <= stars) TechFixWarning else TechFixTextTertiary
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { if (it.length <= REVIEW_COMMENT_LIMIT) comment = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !sending,
                    label = { Text("Comentário (opcional)") },
                    minLines = 2,
                    maxLines = 4
                )
                if (error) {
                    Text("Não foi possível enviar a avaliação. Tente de novo.", color = TechFixRed)
                }
                Button(
                    onClick = {
                        sending = true
                        error = false
                        scope.launch {
                            val saved = try {
                                onSubmit(stars, comment)
                            } catch (cancelled: CancellationException) {
                                throw cancelled
                            } catch (_: Exception) {
                                false
                            }
                            if (!saved) {
                                sending = false
                                error = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = stars > 0 && !sending,
                    colors = ButtonDefaults.buttonColors(containerColor = TechFixAccentSolid)
                ) {
                    Text(if (sending) "Enviando…" else "Enviar avaliação", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ReviewStars(stars: Int, describeRating: Boolean = false) {
    val description = if (describeRating) {
        Modifier.semantics { contentDescription = "$stars de 5 estrelas" }
    } else {
        Modifier
    }
    Row(modifier = description, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        (1..5).forEach { value ->
            Icon(
                if (value <= stars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (value <= stars) TechFixWarning else TechFixTextTertiary
            )
        }
    }
}
