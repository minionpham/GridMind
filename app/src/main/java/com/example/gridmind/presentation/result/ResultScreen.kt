package com.example.gridmind.presentation.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.gridmind.ui.components.GradientScreen
import com.example.gridmind.ui.components.PrimaryButton
import com.example.gridmind.ui.components.SecondaryButton
import com.example.gridmind.ui.theme.Error
import com.example.gridmind.ui.theme.Success
import java.util.Locale

private data class ResultRowData(val label: String, val value: String, val tag: String)

@Composable
fun ResultScreen(
    level: Int,
    score: Int,
    correct: Int,
    incorrect: Int,
    missed: Int,
    accuracy: Float,
    passed: Boolean,
    onNextLevel: () -> Unit,
    onRetry: () -> Unit,
    onHome: () -> Unit
) {
    ResultContent(
        level = level,
        passed = passed,
        rows = listOf(
            ResultRowData("Score", score.toString(), "result_score"),
            ResultRowData("Correct", correct.toString(), "result_correct"),
            ResultRowData("Incorrect", incorrect.toString(), "result_incorrect"),
            ResultRowData("Missed", missed.toString(), "result_missed"),
            ResultRowData("Accuracy", String.format(Locale.US, "%.0f%%", accuracy), "result_accuracy")
        ),
        onNextLevel = onNextLevel,
        onRetry = onRetry,
        onHome = onHome
    )
}

@Composable
fun CubeResultScreen(
    level: Int,
    score: Int,
    chosen: Int?,
    correctAnswer: Int,
    passed: Boolean,
    onNextLevel: () -> Unit,
    onRetry: () -> Unit,
    onHome: () -> Unit
) {
    ResultContent(
        level = level,
        passed = passed,
        rows = listOf(
            ResultRowData("Score", score.toString(), "result_score"),
            ResultRowData("Your answer", chosen?.toString() ?: "-", "result_chosen"),
            ResultRowData("Correct answer", correctAnswer.toString(), "result_answer")
        ),
        onNextLevel = onNextLevel,
        onRetry = onRetry,
        onHome = onHome
    )
}

@Composable
private fun ResultContent(
    level: Int,
    passed: Boolean,
    rows: List<ResultRowData>,
    onNextLevel: () -> Unit,
    onRetry: () -> Unit,
    onHome: () -> Unit
) {
    GradientScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                if (passed) "PASS" else "FAILED",
                style = MaterialTheme.typography.displayMedium,
                color = if (passed) Success else Error,
                modifier = Modifier.testTag("result_status")
            )
            Text(
                "Level $level",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    rows.forEach { ResultRow(it.label, it.value, it.tag) }
                }
            }
            Spacer(Modifier.height(24.dp))
            Column(
                modifier = Modifier.widthIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (passed) {
                    PrimaryButton("Next Level", onNextLevel, Modifier.fillMaxWidth().testTag("next_level_button"))
                    SecondaryButton("Retry", onRetry, Modifier.fillMaxWidth().testTag("retry_button"))
                } else {
                    PrimaryButton("Retry", onRetry, Modifier.fillMaxWidth().testTag("retry_button"))
                }
                SecondaryButton("Back to Home", onHome, Modifier.fillMaxWidth().testTag("home_button"))
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, tag: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag(tag))
    }
}
