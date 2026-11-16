package com.example.techfix.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Este arquivo guarda o conteúdo de verdade das 6 seções da página
 * Sobre Nós, mais o rodapé. Cada seção é uma função @Composable
 * independente, chamada de dentro do LazyColumn em AboutScreen.kt.
 */

// ============================================================
// 1) OUR COMPANY
// ============================================================

@Composable
fun OurCompanySection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("NOSSA EMPRESA")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Aproximando bons profissionais da sua casa",
            color = AboutTextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "A TechFix nasceu para facilitar a manutenção e o suporte técnico. Somos uma plataforma que conecta clientes a profissionais verificados de forma simples, segura e transparente." +
                "" +
                "" +
                "",
            color = AboutTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Placeholder da foto do ambiente — troque por Image() quando
        // tiver o arquivo de imagem real.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AboutCardDark)
        )
    }
}

// ============================================================
// 2) WHY CHOOSE TECHFIX?
// ============================================================

private data class WhyChooseItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String
)

private val whyChooseItems = listOf(
    WhyChooseItem(
        icon = Icons.Outlined.VerifiedUser,
        title = "Profissionais qualificados",
        description = "Todo profissional passa por checagem de documentos e avaliação técnica."
    ),
    WhyChooseItem(
        icon = Icons.Outlined.Schedule,
        title = "Contratação fácil",
        description = "Peça o serviço em poucos toques e encontre o profissional ideal rapidamente."
    ),
    WhyChooseItem(
        icon = Icons.Outlined.Visibility,
        title = "Transparência total",
        description = "Você vê o valor antes de contratar, sem taxas escondidas, e confere as avaliações de outros clientes."
    )
)

@Composable
fun WhyChooseSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("POR QUE ESCOLHER A TECHFIX")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Feita para dar confiança e um atendimento de qualidade",
            color = AboutTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Empilhados verticalmente (no Figma de desktop eles ficam lado
        // a lado; num celular estreito, empilhar é o que cabe melhor).
        whyChooseItems.forEach { item ->
            WhyChooseCard(item)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun WhyChooseCard(item: WhyChooseItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AboutCardDark)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AboutAccentOrange.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = item.icon, contentDescription = null, tint = AboutAccentOrange)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = item.title, color = AboutTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = item.description, color = AboutTextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

// ============================================================
// 3) OUR VALUES
// ============================================================

private data class ValueItem(val number: String, val title: String, val description: String)

private val valueItems = listOf(
    ValueItem("01", "Compromisso com a qualidade", "Cuidado em cada serviço. Se algo não ficou certo, refazemos."),
    ValueItem("02", "Transparência", "Diagnóstico honesto e preço claro, para haver confiança entre profissional e cliente."),
    ValueItem("03", "Respeito e ética", "Sua casa é seu espaço. Nossos profissionais seguem um código de conduta rigoroso."),
    ValueItem("04", "Inovação contínua", "Usamos tecnologia para ligar você ao profissional certo e resolver mais rápido.")
)

@Composable
fun OurValuesSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("NOSSOS VALORES")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Os princípios por trás de cada serviço",
            color = AboutTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        // "chunked(2)" quebra os 4 valores em 2 linhas de 2 — o mesmo
        // truque usado nas telas de onboarding pra montar um grid.
        valueItems.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { value ->
                    ValueCard(value, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ValueCard(value: ValueItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AboutCardDark)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(value.number, color = AboutAccentOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Icon(
                imageVector = Icons.Outlined.Verified,
                contentDescription = null,
                tint = AboutAccentOrange,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(value.title, color = AboutTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(value.description, color = AboutTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

// ============================================================
// 4) OUR SERVICES
// ============================================================

private data class ServiceItem(val title: String, val description: String)

private val serviceItems = listOf(
    ServiceItem("Manutenção residencial", "Revisões de rotina, ajustes em aparelhos e manutenção preventiva."),
    ServiceItem("Reparos elétricos", "Troca de quadros, solução de falhas elétricas e instalação de iluminação inteligente."),
    ServiceItem("Reparos hidráulicos", "Busca de vazamentos, instalação de louças e metais e ajuste do sistema de água."),
    ServiceItem("Marcenaria", "Conserto de móveis, restauração de acabamentos e reforço de estruturas de madeira."),
    ServiceItem("Reformas", "Projetos completos, reparo de drywall e pintura com acabamento fino.")
)

@Composable
fun OurServicesSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("NOSSOS SERVIÇOS")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Suporte técnico completo para a sua casa",
            color = AboutTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Os 4 primeiros em pares (2 colunas); "Reformas" sozinho
        // ocupa a linha inteira — igual ao recorte do Figma.
        serviceItems.dropLast(1).chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { service ->
                    ServiceCard(service, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        ServiceCard(serviceItems.last(), modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ServiceCard(service: ServiceItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AboutCardDark)
    ) {
        // Placeholder da foto do serviço — troque por Image() quando
        // tiver os arquivos de imagem reais.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color(0xFF2A2A2A))
        )
        Column(modifier = Modifier.padding(14.dp)) {
            Text(service.title, color = AboutTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(service.description, color = AboutTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

// ============================================================
// 5) HOW WE WORK
// ============================================================

private data class WorkStep(val number: String, val title: String, val description: String)

private val workSteps = listOf(
    WorkStep("01", "Cadastro do profissional", "O profissional envia seus dados e documentos para verificação."),
    WorkStep("02", "Busca de serviços", "O cliente escolhe a categoria e descreve exatamente o que precisa."),
    WorkStep("03", "Contratação simples", "A TechFix indica os profissionais mais bem avaliados perto de você."),
    WorkStep("04", "Avaliações", "Avalie o atendimento. As notas ajudam a manter a qualidade dos profissionais.")
)

@Composable
fun HowWeWorkSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("COMO FUNCIONA")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Quatro passos simples para resolver seu problema",
            color = AboutTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        workSteps.forEachIndexed { index, step ->
            WorkStepRow(step, isLast = index == workSteps.lastIndex)
        }
    }
}

@Composable
private fun WorkStepRow(step: WorkStep, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // A coluna da esquerda é a "bolinha numerada + linha pontilhada"
        // que conecta um passo ao próximo.
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AboutAccentOrange),
                contentAlignment = Alignment.Center
            ) {
                Text(step.number, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(70.dp)
                        .background(AboutAccentOrange.copy(alpha = 0.35f))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(step.title, color = AboutTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(step.description, color = AboutTextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

// ============================================================
// 6) OUR TEAM
// ============================================================

private data class TeamMember(val initials: String, val name: String, val role: String, val avatarColor: Color)

private val teamMembers = listOf(
    TeamMember("RN", "Rafael Nunes", "DIRETOR DE ESTRATÉGIA E PRODUTO", Color(0xFFB5451B)),
    TeamMember("MJ", "Miguel Januario", "DIRETOR DE OPERAÇÕES", Color(0xFF3B5F45)),
    TeamMember("LG", "Leonardo Gregório", "DIRETOR FINANCEIRO", Color(0xFF4A3B6B)),
    TeamMember("GA", "Gabriel Araujo", "DIRETOR DE MARKETING", Color(0xFF8A4B1F)),
    TeamMember("J", "João", "DIRETOR JURÍDICO", Color(0xFF3A3A3A))
)

@Composable
fun OurTeamSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("NOSSA EQUIPE")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Conheça quem faz a TechFix acontecer",
            color = AboutTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Nossa equipe une experiência prática de atendimento com conhecimento em tecnologia.",
            color = AboutTextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        teamMembers.forEach { member ->
            TeamMemberRow(member)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun TeamMemberRow(member: TeamMember) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AboutCardDark)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(member.avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(member.initials, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(member.name, color = AboutTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = member.role,
                color = AboutAccentOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ========================
//         RODAPÉ
// ========================

@Composable
fun AboutFooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF080808))
            .padding(24.dp)
    ) {
        Text(
            buildString { append("TechFix") },
            color = AboutTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Uma plataforma que conecta você a profissionais verificados, com preço claro e acompanhamento do pedido do início ao fim.",
            color = AboutTextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "© 2026 TechFix. Todos os direitos reservados.",
            color = AboutTextSecondary,
            fontSize = 11.sp
        )
    }
}

/**
 * O textinho pequeno e laranja acima de cada título de seção
 * (ex: "NOSSOS VALORES", "COMO FUNCIONA"). Extraído aqui porque se repete
 * em todas as seções.
 */
@Composable
private fun SectionEyebrow(text: String) {
    Text(
        text = text,
        color = AboutAccentOrange,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}
