# Branch Protection Rules

Este documento descreve as regras de proteção de branch recomendadas para o projeto Copacracks Backend.

## 🛡️ Branches Protegidas

### `main` Branch
- **Proteção**: Total
- **Requer**: 
  - ✅ Pull Request aprovado
  - ✅ Todos os status checks passando
  - ✅ Revisão de código obrigatória
  - ✅ Atualização da branch obrigatória

### `develop` Branch
- **Proteção**: Alta
- **Requer**:
  - ✅ Pull Request aprovado
  - ✅ Todos os status checks passando
  - ✅ Revisão de código obrigatória

## 🔒 Status Checks Obrigatórios

### Pull Request Checks
- `code-quality` - Qualidade de código
- `security` - Verificações de segurança
- `build` - Build do projeto
- `pre-commit-verification` - Verificação dos hooks

### CodeQL Analysis
- `codeql-analysis` - Análise de segurança

### SonarCloud
- `sonarcloud` - Análise de qualidade

## 📋 Configuração no GitHub

### 1. Acesse Settings > Branches
```
Repository Settings > Branches > Add rule
```

### 2. Configure para `main`
```
Branch name pattern: main
✓ Require a pull request before merging
  ✓ Require approvals: 1
  ✓ Dismiss stale PR approvals when new commits are pushed
✓ Require status checks to pass before merging
  ✓ Require branches to be up to date before merging
  ✓ Status checks that are required:
    - code-quality
    - security
    - build
    - pre-commit-verification
    - codeql-analysis
    - sonarcloud
✓ Require conversation resolution before merging
✓ Require signed commits
✓ Require linear history
✓ Include administrators
```

### 3. Configure para `develop`
```
Branch name pattern: develop
✓ Require a pull request before merging
  ✓ Require approvals: 1
  ✓ Dismiss stale PR approvals when new commits are pushed
✓ Require status checks to pass before merging
  ✓ Require branches to be up to date before merging
  ✓ Status checks that are required:
    - code-quality
    - security
    - build
    - pre-commit-verification
    - codeql-analysis
    - sonarcloud
✓ Require conversation resolution before merging
✓ Include administrators
```

## 🚨 Status Checks Explicados

### `code-quality`
Executa todos os checks de qualidade:
- Checkstyle
- PMD
- SpotBugs
- Spotless
- Testes
- JaCoCo

### `security`
Verifica vulnerabilidades:
- Análise de dependências
- Relatórios de segurança

### `build`
Verifica build do projeto:
- Compilação
- Dependências
- Artifacts

### `pre-commit-verification`
Verifica hooks Git:
- Instalação dos hooks
- Funcionamento dos checks

### `codeql-analysis`
Análise de segurança:
- Vulnerabilidades de código
- Análise estática de segurança

### `sonarcloud`
Análise de qualidade:
- Métricas de código
- Cobertura de testes
- Dívida técnica

## 🔧 Troubleshooting

### Status Check Falhou
1. **Verifique os logs** do GitHub Actions
2. **Execute localmente** o comando que falhou
3. **Corrija o problema** no código
4. **Faça novo commit** para re-executar os checks

### Branch Desatualizada
1. **Atualize sua branch** com a branch de destino
2. **Resolva conflitos** se necessário
3. **Force push** (se permitido) ou faça novo commit

### Revisão Obrigatória
1. **Peça revisão** para um membro da equipe
2. **Aguarde aprovação** antes do merge
3. **Resolva comentários** se houver

## 📚 Recursos Adicionais

- [GitHub Branch Protection](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/about-protected-branches)
- [Required Status Checks](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/troubleshooting-required-status-checks)
- [GitHub Actions Status](https://docs.github.com/en/actions/managing-workflow-runs/using-workflow-run-logs)
