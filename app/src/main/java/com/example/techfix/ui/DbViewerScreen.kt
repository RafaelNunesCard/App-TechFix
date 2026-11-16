package com.example.techfix.ui.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.RoomDatabase
import com.example.techfix.ui.orders.DetailHeader
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixGreen
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private data class TableSnapshot(
    val name: String,
    val count: Int,
    val columns: List<String>,
    val rows: List<List<String>>
)

private const val MAX_ROWS = 20

/** Colunas com segredo nunca são mostradas, nem aqui. */
private fun isSecret(column: String) =
    column.contains("password", ignoreCase = true) || column.contains("salt", ignoreCase = true)

private fun readSnapshot(database: RoomDatabase): List<TableSnapshot> {
    val db = database.openHelper.readableDatabase
    val names = mutableListOf<String>()
    db.query(
        "SELECT name FROM sqlite_master WHERE type='table' " +
            "AND name NOT LIKE 'sqlite_%' AND name NOT IN ('android_metadata','room_master_table') ORDER BY name"
    ).use { c -> while (c.moveToNext()) names.add(c.getString(0)) }

    return names.map { name ->
        var count = 0
        db.query("SELECT COUNT(*) FROM `$name`").use { c -> if (c.moveToFirst()) count = c.getInt(0) }

        var columns = emptyList<String>()
        val rows = mutableListOf<List<String>>()
        db.query("SELECT * FROM `$name` ORDER BY rowid DESC LIMIT $MAX_ROWS").use { c ->
            columns = c.columnNames.toList()
            while (c.moveToNext()) {
                rows.add(
                    columns.indices.map { i ->
                        when {
                            isSecret(columns[i]) -> "••••"
                            c.isNull(i) -> "null"
                            c.getType(i) == android.database.Cursor.FIELD_TYPE_BLOB -> "[blob]"
                            else -> c.getString(i).let { if (it.length > 60) it.take(60) + "…" else it }
                        }
                    }
                )
            }
        }
        TableSnapshot(name, count, columns, rows)
    }
}

/**
 * Tela escondida (toque 5x na logo do Perfil) que mostra o banco Room deste
 * celular ao vivo: atualiza a cada segundo, então dá para ver as linhas
 * aparecendo enquanto o app é usado.
 */
@Composable
fun DbViewerScreen(database: RoomDatabase, onBack: () -> Unit) {
    var tables by remember { mutableStateOf<List<TableSnapshot>>(emptyList()) }
    var expanded by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(database) {
        while (true) {
            tables = try {
                withContext(Dispatchers.IO) { readSnapshot(database) }
            } catch (e: Exception) {
                tables
            }
            delay(1000)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = TechFixBackground) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailHeader(title = "Banco de dados (Room)", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(TechFixGreen))
                    Spacer(Modifier.width(8.dp))
                    Text("Ao vivo • atualiza a cada 1 s", color = TechFixGreen, fontSize = 12.sp)
                }
                Text(
                    "SQLite via Room. Os dados ficam só neste celular. Toque numa tabela para ver as últimas linhas.",
                    color = TechFixTextSecondary,
                    fontSize = 12.sp
                )

                tables.forEach { table ->
                    val isOpen = table.name in expanded
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(TechFixSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expanded = if (isOpen) expanded - table.name else expanded + table.name
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                table.name,
                                color = TechFixTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${table.count} ${if (table.count == 1) "linha" else "linhas"}",
                                color = TechFixAccentSolid,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        if (isOpen) {
                            if (table.rows.isEmpty()) {
                                Text(
                                    "Tabela vazia.",
                                    color = TechFixTextTertiary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                                )
                            } else {
                                Text(
                                    "Colunas: ${table.columns.joinToString(", ")}",
                                    color = TechFixTextTertiary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                                if (table.count > MAX_ROWS) {
                                    Text(
                                        "Mostrando as $MAX_ROWS mais recentes.",
                                        color = TechFixTextTertiary,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp)
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                table.rows.forEach { row ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 4.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(TechFixBackground)
                                            .padding(10.dp)
                                    ) {
                                        table.columns.forEachIndexed { i, col ->
                                            Text(
                                                "$col = ${row[i]}",
                                                color = TechFixTextSecondary,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
