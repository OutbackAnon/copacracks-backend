# Copacracks Backend

Backend do projeto Copacracks desenvolvido em Java com Gradle, seguindo princípios de Domain-Driven Design e Clean Architecture.

## 🚀 Tecnologias

- **Java 21** - Linguagem principal
- **Gradle** - Build tool e gerenciador de dependências
- **JUnit 5** - Framework de testes
- **JaCoCo** - Cobertura de testes
- **Checkstyle** - Padrões de código
- **PMD** - Análise estática de código
- **SpotBugs** - Detecção de bugs
- **Spotless** - Formatação automática de código

## 🏗️ Arquitetura

O projeto segue os princípios de **Domain-Driven Design (DDD)** com:

- **Value Objects** - Objetos imutáveis que representam conceitos do domínio
- **Entities** - Objetos com identidade e ciclo de vida
- **Domain Services** - Lógica de negócio complexa
- **Exception Handling** - Tratamento centralizado de erros

## 📋 Comandos Gradle

### Build e Limpeza
```bash
# Limpa e faz build completo
./gradlew clean build

# Apenas build
./gradlew build

# Atualiza versão do Gradle
./gradlew wrapper --gradle-version <VERSION>
```

### Qualidade de Código
```bash
# Verifica formatação
./gradlew spotlessCheck

# Aplica formatação automática
./gradlew spotlessApply

# Executa Checkstyle
./gradlew checkstyleMain
./gradlew checkstyleTest

# Executa PMD
./gradlew pmdMain

# Executa SpotBugs
./gradlew spotbugsMain

# Executa PMD e SpotBugs
./gradlew codeQuality
```

### Testes
```bash
# Executa testes
./gradlew test

# Gera relatório de cobertura
./gradlew jacocoTestReport

# Executa testes com cobertura
./gradlew test jacocoTestReport
```

### Verificação Pré-commit
```bash
# Executa todos os checks necessários
./gradlew preCommitCheck

# Instala Git hooks
./gradlew installGitHooks
```

## 🔄 GitHub Actions

O projeto possui workflows automatizados para garantir qualidade contínua:

### Workflows Disponíveis
- **Pull Request Checks** - Executa todos os checks de qualidade em PRs
- **Feature Branch Checks** - Checks rápidos em branches de feature
- **Nightly Quality Checks** - Verificações noturnas abrangentes
- **CodeQL Security Analysis** - Análise de segurança automatizada
- **SonarCloud Integration** - Análise de qualidade contínua
- **Pre-commit Verification** - Verifica se os hooks estão funcionando

### Triggers
- ✅ Pull Requests para `main` e `develop`
- ✅ Push para branches de feature
- ✅ Execução noturna automática
- ✅ Análise de segurança semanal

### Relatórios
- 📊 Cobertura de testes (JaCoCo)
- 🔍 Análise estática (PMD, SpotBugs, Checkstyle)
- 🔒 Relatórios de segurança (CodeQL)
- 📈 Métricas de qualidade (SonarCloud)

## 🛠️ Desenvolvimento

### Pré-requisitos
- Java 21 ou superior
- Gradle 8.x
- Git

### Setup Local
```bash
# Clone o repositório
git clone <repository-url>
cd copacracks-backend

# Instala Git hooks
./gradlew installGitHooks

# Executa build inicial
./gradlew build
```

### Workflow de Desenvolvimento
1. **Crie uma branch** para sua feature
2. **Desenvolva** seguindo os padrões do projeto
3. **Execute checks locais** antes do commit
4. **Faça commit** (hooks executam automaticamente)
5. **Crie um PR** para `develop` ou `main`
6. **Aguarde CI/CD** executar todos os checks
7. **Corrija problemas** se necessário
8. **Merge** após aprovação

## 📚 Documentação

- [GitHub Actions Workflows](.github/README.md) - Documentação completa dos workflows
- [Domain Model](src/main/java/com/copacracks/core/domain/README.md) - Documentação do modelo de domínio

## 🤝 Contribuição

1. Siga os padrões de código estabelecidos
2. Mantenha a cobertura de testes alta
3. Execute todos os checks localmente antes do commit
4. Documente mudanças significativas
5. Use commits semânticos

## 📄 Licença

Este projeto é privado e proprietário da Copacracks.

