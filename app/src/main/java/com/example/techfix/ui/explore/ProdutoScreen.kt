package com.example.produtos.ui.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.theme.BackColor
import com.example.techfix.R
import com.example.techfix.ui.theme.SoraFamily
import kotlin.collections.listOf

val LaranjaTechFix = Color(0xFFFF6B35)

data class Produto(
    val nome: String,
    val categoria: String,
    val avaliacao: Double,
    val preco: String,
    val fotoRes: Int
)

val categorias = listOf("Appliance", "Vehicle", "Industrial")

val produtos = listOf(
Produto("Renata Costa", "Appliance", 4.6, "$38", R.drawable.placeholder_perfil),
Produto("Fábio Nunes", "Appliance", 4.8, "$52", R.drawable.placeholder_perfil),
Produto("Juliana Ramos", "Appliance", 4.7, "$40", R.drawable.placeholder_perfil),
Produto("Diego Moreira", "Appliance", 4.5, "$35", R.drawable.placeholder_perfil),
Produto("Patrícia Lima", "Appliance", 4.9, "$60", R.drawable.placeholder_perfil),
Produto("Rafael Teixeira", "Appliance", 4.4, "$30", R.drawable.placeholder_perfil),

// ---------- VEHICLE (7) ----------
Produto("Bruno Cardoso", "Vehicle", 4.6, "$95", R.drawable.placeholder_perfil),
Produto("Camila Duarte", "Vehicle", 4.9, "$150", R.drawable.placeholder_perfil),
Produto("Eduardo Farias", "Vehicle", 4.7, "$110", R.drawable.placeholder_perfil),
Produto("Larissa Pinto", "Vehicle", 4.5, "$85", R.drawable.placeholder_perfil),
Produto("Thiago Azevedo", "Vehicle", 4.8, "$130", R.drawable.placeholder_perfil),
Produto("Vanessa Rocha", "Vehicle", 4.6, "$99", R.drawable.placeholder_perfil),

// ---------- INDUSTRIAL (6) ----------
Produto("Simone Barros", "Industrial", 4.9, "$280", R.drawable.placeholder_perfil),
Produto("André Vasconcelos", "Industrial", 4.5, "$175", R.drawable.placeholder_perfil),
Produto("Beatriz Monteiro", "Industrial", 4.8, "$240", R.drawable.placeholder_perfil),
Produto("Gustavo Peixoto", "Industrial", 4.6, "$195", R.drawable.placeholder_perfil),
Produto("Isabela Correia", "Industrial", 4.7, "$220", R.drawable.placeholder_perfil),
    Produto("João Silva", "Appliance", 4.9, "$45", R.drawable.eletricista_joao),
    Produto("Marina Souza", "Vehicle", 4.8, "$120", R.drawable.marina_mecanica),
    Produto("Carlos Andrade", "Industrial", 4.7, "$210", R.drawable.carlos_industrial)
)

@Composable
fun ProdutoScreen(
    onHomeClick: () -> Unit = {},
    onServicesClick: () -> Unit = {}
) {

    var searchText by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf("Appliance") }

    val produtosFiltrados = produtos.filter { produto ->
        val combinaCategoria = produto.categoria == categoriaSelecionada
        val combinaBusca = produto.nome.contains(searchText, ignoreCase = true)
        combinaCategoria && combinaBusca
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackColor)
    ) {

        // ---------- HEADER ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(BackColor)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo_badge),
                    contentDescription = "Icon da plataforma",
                    modifier = Modifier.size(50.dp)
                )
                Text(text = "Techfix",
                    fontFamily = SoraFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp))
            }

        }

        // ---------- SAUDAÇÃO + BUSCA ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(BackColor)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top //
        ) {
            Text(
                text = "Hello, Welcome back \uD83D\uDC4B",
                color = Color(0xFFA1A1AA),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "What needs fixing today?",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF18181B))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color(0xFF71717A),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text(
                                text = "Search repairs, systems, painting...",
                                color = Color(0xFF71717A),
                                fontSize = 13.sp
                            )
                        }
                        innerTextField()
                    }
                )

                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filtros",
                    tint = Color(0xFF71717A),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // ---------- CATEGORIAS ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(BackColor)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Categories", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("See All", color = LaranjaTechFix, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { categoria ->
                    val ativo = categoria == categoriaSelecionada
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (ativo) LaranjaTechFix else Color(0xFF18181B))
                            .clickable { categoriaSelecionada = categoria }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = categoria,
                            color = if (ativo) Color.White else Color(0xFFA1A1AA),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // ---------- SERVIÇOS EM DESTAQUE ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(BackColor)
                .padding(start = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Featured Services", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Trending", color = LaranjaTechFix, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(produtosFiltrados) { produto ->
                    ProdutoCard(produto)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- ESTATÍSTICAS ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(BackColor)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF18181B))
                    .padding(vertical = 16.dp)
            ) {
                EstatisticaItem("3 Years", "EXPERIENCE", Modifier.weight(1f))
                Divider(
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxHeight().width(1.dp)
                )
                EstatisticaItem("600+", "CUSTOMERS", Modifier.weight(1f))
                Divider(
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxHeight().width(1.dp)
                )
                EstatisticaItem("12+", "SPECIALTIES", Modifier.weight(1f))
            }
        }

        // ---------- NAVEGAÇÃO INFERIOR ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ItemNavegacao(
                label = "Home",
                icone = Icons.Default.Home,
                ativo = false,
                onClick = onHomeClick
            )

            ItemNavegacao(
                label = "Services",
                icone = Icons.Default.Build,
                ativo = true,
                onClick = onServicesClick
            )
        }
    }
}

@Composable
fun ProdutoCard(produto: Produto) {
    Column(
        modifier = Modifier
            .width(165.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF18181B))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            Image(
                painter = painterResource(id = produto.fotoRes),
                contentDescription = produto.nome,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFACC15),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text("${produto.avaliacao}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }

        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = produto.categoria.uppercase(),
                color = LaranjaTechFix,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = produto.nome,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text("STARTING AT", color = Color(0xFF71717A), fontSize = 9.sp)
                Text(produto.preco, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EstatisticaItem(valor: String, rotulo: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(rotulo, color = Color(0xFF71717A), fontSize = 10.sp)
    }
}

@Composable
fun ItemNavegacao(
    label: String,
    icone: androidx.compose.ui.graphics.vector.ImageVector,
    ativo: Boolean,
    onClick: () -> Unit
) {
    val cor = if (ativo) LaranjaTechFix else Color(0xFF71717A)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Icon(icone, contentDescription = label, tint = cor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = cor, fontSize = 10.sp)
    }
}