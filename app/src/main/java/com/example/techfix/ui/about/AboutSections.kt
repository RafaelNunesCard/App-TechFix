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
        SectionEyebrow("OUR COMPANY")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Bridging the gap between qualified experts and your home",
            color = AboutTextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "TechFix was born to disrupt the traditional home maintenance " +
                "industry. We are a premium technology-driven platform designed to " +
                "connect homeowners with verified, high-performance technicians " +
                "seamlessly, safely, and transparently.",
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
        title = "Qualified Professionals",
        description = "Every technician undergoes rigorous credential background checks, technical skill tests, and on-site assessments."
    ),
    WhyChooseItem(
        icon = Icons.Outlined.Schedule,
        title = "Ease of Hiring",
        description = "Intuitive modern booking process. Match with the ideal specialized repair team within seconds."
    ),
    WhyChooseItem(
        icon = Icons.Outlined.Visibility,
        title = "Full Transparency",
        description = "Real-time upfront pricing models with absolutely zero hidden fees. Verified community feedback loops."
    )
)

@Composable
fun WhyChooseSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("WHY CHOOSE TECHFIX")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Engineered for reliability and premium standard care",
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
    ValueItem("01", "Commitment to Quality", "Precision engineering in every task. If it isn't resolved flawlessly, we reset it right instantly."),
    ValueItem("02", "Radical Transparency", "Honest diagnostics and direct price calculations. Complete trust between provider and client."),
    ValueItem("03", "Respect & Ethics", "Your home is your sacred space. Our professionals operate with the highest behavioral code."),
    ValueItem("04", "Continuous Innovation", "Utilizing modern diagnostics, intelligent dispatch algorithms, and smart materials to fix things faster.")
)

@Composable
fun OurValuesSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("OUR VALUES")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "The principles guiding every fix we execute",
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
    ServiceItem("Residential Maintenance", "Routine diagnostic reviews, appliance tuning, and preventative care regimens."),
    ServiceItem("Electrical Repairs", "Premium panel upgrades, high-voltage troubleshooting, and intelligent smart home lighting setups."),
    ServiceItem("Hydraulic Repairs", "Leak diagnostics, premium fixture installations, and modern water system balancing."),
    ServiceItem("Carpentry Services", "Bespoke furniture repair, trim restoration, and structural wood reinforcement."),
    ServiceItem("Renovations", "Turnkey design-build projects, drywall restoration, and high-finish paint layers.")
)

@Composable
fun OurServicesSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("OUR SERVICES")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Comprehensive technical support for your home",
            color = AboutTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Os 4 primeiros em pares (2 colunas); "Renovations" sozinho
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
    WorkStep("01", "Professional Registration", "Technicians submit detailed verification documents, reference history, and pass practical exams."),
    WorkStep("02", "Search for Services", "Homeowners choose specialized service domains and pinpoint their exact repair requirement through our portal."),
    WorkStep("03", "Simple Hiring", "Our smart algorithmic engine matches and dispatches the highest-rated technician near you."),
    WorkStep("04", "Reviews & Feedback", "Rate your experience. Feedback drives quality scores and locks in community-driven performance standards.")
)

@Composable
fun HowWeWorkSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("HOW WE WORK")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Four simple steps to absolute home peace of mind",
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
    TeamMember("RN", "Rafael Nunes", "DIRECTOR OF STRATEGY AND PRODUCT", Color(0xFFB5451B)),
    TeamMember("MJ", "Miguel Januario", "DIRECTOR OF OPERATIONS", Color(0xFF3B5F45)),
    TeamMember("LG", "Leonardo Gregório", "FINANCIAL DIRECTOR", Color(0xFF4A3B6B)),
    TeamMember("GA", "Gabriel Araujo", "MARKETING DIRECTOR", Color(0xFF8A4B1F)),
    TeamMember("J", "João", "GENERAL COUNSEL", Color(0xFF3A3A3A))
)

@Composable
fun OurTeamSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        SectionEyebrow("OUR TEAM")
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Meet the operational and strategic leaders",
            color = AboutTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Our diverse leadership team balances extensive real-world operations experience with high-scale technology execution.",
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
            text = "A high-performance modern platform dedicated to preserving your physical home infrastructure. Vetted specialists, upfront cost calculators, and diagnostic-driven workflow parameters.",
            color = AboutTextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "© 2026 TechFix Inc. Premium Home Engineering Systems.",
            color = AboutTextSecondary,
            fontSize = 11.sp
        )
    }
}

/**
 * O textinho pequeno e laranja acima de cada título de seção
 * (ex: "OUR VALUES", "HOW WE WORK"). Extraído aqui porque se repete
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
