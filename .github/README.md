# GitHub Actions Workflows

Este diretório contém os workflows do GitHub Actions para automatizar a qualidade e segurança do código.

## 📋 Workflows Disponíveis

### 1. **Pull Request Checks** (`pr-checks.yml`)
**Trigger**: Pull Requests para `main` e `develop`

**Jobs**:
- **Code Quality**: Executa Checkstyle, PMD, SpotBugs, Spotless, testes e gera relatório JaCoCo
- **Security**: Verifica vulnerabilidades nas dependências
- **Build**: Verifica se o projeto compila corretamente

**Features**:
- ✅ Cache do Gradle para builds mais rápidos
- 📊 Upload automático de relatórios como artifacts
- 💬 Comentário automático no PR com resultados
- 🔒 Upload de cobertura para Codecov

### 2. **Feature Branch Checks** (`feature-checks.yml`)
**Trigger**: Push para branches de feature (exceto main/develop)

**Jobs**:
- **Quick Checks**: Execução rápida de Spotless e testes

**Features**:
- ⚡ Execução rápida para feedback imediato
- 📁 Artifacts específicos por branch
- 🎯 Foco em qualidade essencial

### 3. **Nightly Quality Checks** (`nightly-checks.yml`)
**Trigger**: Diariamente às 2:00 AM UTC + execução manual

**Jobs**:
- **Comprehensive Checks**: Todos os checks de qualidade
- **Issue Creation**: Cria issues automáticas em caso de falha

**Features**:
- 🌙 Execução noturna para não impactar desenvolvimento
- 🚨 Criação automática de issues para problemas
- 📈 Relatórios de longo prazo (90 dias)

### 4. **CodeQL Security Analysis** (`codeql.yml`)
**Trigger**: Push/PR para main/develop + semanalmente

**Jobs**:
- **Security Analysis**: Análise estática de segurança com CodeQL

**Features**:
- 🔒 Análise de segurança automatizada
- 📊 Relatórios de vulnerabilidades
- 🚨 Alertas de segurança

## 🛠️ Configurações

### Dependabot (`dependabot.yml`)
- **Gradle**: Atualizações semanais (segunda-feira 9:00 BRT)
- **GitHub Actions**: Atualizações semanais
- **Gradle Wrapper**: Atualizações mensais

### Cache
- Gradle packages e wrapper
- Otimiza tempo de execução dos workflows

## 📊 Relatórios e Artifacts

### Code Quality
- Checkstyle HTML reports
- PMD HTML/XML reports  
- SpotBugs HTML reports
- Test results (JUnit XML)
- JaCoCo coverage reports

### Security
- Dependency vulnerability reports
- CodeQL analysis results
- SARIF files

### Build
- JAR files
- Build logs

## 🚀 Como Usar

### Para Desenvolvedores
1. **Push para feature branch**: Executa checks rápidos
2. **Criar PR**: Executa todos os checks de qualidade
3. **Merge**: Verifica se todos os checks passaram

### Para Mantenedores
1. **Monitorar nightly builds**: Verificar issues automáticas
2. **Revisar dependabot PRs**: Manter dependências atualizadas
3. **Analisar relatórios**: Usar artifacts para insights

## 🔧 Personalização

### Adicionar Novos Checks
1. Adicione a task no `build.gradle.kts`
2. Inclua no workflow apropriado
3. Configure upload de artifacts se necessário

### Modificar Triggers
1. Edite a seção `on` do workflow
2. Ajuste branches, paths ou schedules
3. Teste com `workflow_dispatch` primeiro

### Configurar Notificações
1. Adicione steps de notificação (Slack, Teams, etc.)
2. Configure webhooks se necessário
3. Use `if: failure()` para alertas de falha

## 📚 Recursos Adicionais

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle GitHub Actions](https://github.com/gradle/gradle-build-action)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)

## 🤝 Contribuição

Para melhorar os workflows:
1. Teste localmente com `act` ou similar
2. Crie PR com mudanças
3. Documente novas funcionalidades
4. Atualize este README se necessário
