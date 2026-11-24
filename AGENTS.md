# AGENTS.md — TechFix

App Android "TechFix" (Kotlin, Jetpack Compose, Navigation Compose, Room, Coil) feito por alunos para uma feira escolar. Conecta clientes a técnicos. Visitantes instalam o APK por QR code (Release `ultima-versao`, arquivo `techfix.apk`). Antes de qualquer tarefa, leia o repositório inteiro.

## Como o usuário trabalha (importante)
- Não tem Android Studio. Edita só pelo site do GitHub; o GitHub Actions compila o APK. Você não consegue compilar: confira imports, chaves e assinaturas com cuidado.
- Entregue SEMPRE arquivos INTEIROS, nunca trechos (colar por cima de parte de um arquivo no editor do GitHub já corrompeu o projeto). Qualquer tipo de arquivo vale: Kotlin, XML, gradle, imagens.
- Ao final de cada resposta, diga o caminho exato de cada arquivo e se deve CRIAR ou SUBSTITUIR. Ele sobe com "Add file → Upload files".
- Baseie as edições SEMPRE na versão atual do repositório. Não desfaça mudanças de outros colegas.
- Se o Actions ficar vermelho, o usuário traz a primeira linha com `e:`.

## Regras do projeto
- Dados só no Room (SQLite local). Sem servidor nem banco externo. O banco usa `fallbackToDestructiveMigration`: subir a versão APAGA os dados dos celulares. Avise antes de mudar tabelas.
- Interface em português do Brasil. Fonte Sora.
- Cores sempre pelos tokens de `ui/theme/Color.kt` (`TechFixBackground`, `TechFixAccentBrush`, etc.). Sem cores soltas no código.
- Logo: imagem do drawable `logo_techfix`, como nas outras telas. Exceções já decididas: login e cadastro NÃO têm logo; o Explorar mostra só o texto "Techfix" no topo; a logo aparece no topo do Perfil.
- Não desenhe logos de marcas (Google, Facebook, LinkedIn). O usuário sobe os PNGs oficiais em `res/drawable` (`ic_google`, `ic_facebook`, `ic_linkedin`).
- Não guarde número completo de cartão, CVV nem senha em texto.
- Se achar algo que valha implementar, trocar ou apagar, explore, mas AVISE e explique antes de fazer.

## O que já existe (não refazer)
Login, cadastro, esqueci a senha, onboarding de cliente e de profissional, Home, Explorar (busca e filtros), solicitar serviço, pedidos e detalhe, painel do profissional, chat, notificações, formas de pagamento com cartões, perfil com foto e botão Sair, splash animada, tela escondida do banco (5 toques na logo do Perfil), barra de navegação do sistema escondida.

## Dados de demonstração
Semeados por `DemoData`. Contas: `cliente@techfix.demo` (Marina Souza) e `profissional@techfix.demo` (Carlos Mendes), senha `techfix123`.

## Detalhes técnicos úteis
- Perfil profissional = existe linha em `professional_profile` (o app usa isso para decidir a home).
- Foto de perfil e cartões ficam em SharedPreferences (`ProfilePrefs`, `CardPrefs`), não no Room.
- Build debug com keystore fixa (`app/debug.keystore`), para o APK atualizar por cima do antigo.
