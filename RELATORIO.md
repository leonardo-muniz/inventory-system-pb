# Relatório Técnico de Engenharia de Software: Implementação de CI/CD e Qualidade (TP5)

**Estudante:** Leonardo Muniz

**Data:** 04 de Abril de 2026

**Repositório:** [github.com/leonardo-muniz/inventory-system-pb](https://github.com/leonardo-muniz/inventory-system-pb/)

## 1. Objetivos do Projeto

Meu projeto se concentrou em criar um sistema de Integração Contínua (CI) e Entrega Contínua (CD) para controlar um sistema de inventário. Priorizei a automatização de proteções, assegurando que apenas códigos verificados, seguros e validados sejam implementados nos ambientes de Teste/Produção.

Para assegurar que o ambiente de compilação seja totalmente portátil, e para atender aos critérios de automatização tal como o Gradle faz, a solução escolhida foi implementar o **Maven Wrapper**.

## 2. Estratégia de Testes (V&V)

### 2.1 Testes Unitários e Mocking (Requisito 1)

Implementei testes com JUnit 5 e Mockito. A ideia foi isolar a lógica da camada de Service, assegurando que as regras do inventário (validação de quantidades e categorias, entre outras) funcionem sem depender da persistência.

- **Ferramenta de Cobertura:** JaCoCo (Java Code Coverage).

- **Meta:** Validar os principais caminhos de execução antes da fase de empacotamento.

### 2.2 Análise Estática e Qualidade de Código (Requisito 2)

O processo inclui duas abordagens de verificação sem rodar o código:

- **PMD:** É utilizado para encontrar trechos de código problemáticos (Code Smells), variáveis ​​não aproveitadas e desvios das convenções de programação Java.

- **GitHub CodeQL (SAST):** Uma avaliação de segurança minuciosa que rastreia falhas como SQL Injection, Cross-Site Scripting (XSS) e divulgação de dados confidenciais.

### 2.3 Testes de Integração e Health Checks (Requisito 3)

Ao contrário dos testes unitários, as verificações de saúde pós-implantação asseguram a validação da aplicação em execução no Railway.

- **Como Implementar:** O comando `curl --fail` garante que o endereço `/products` responda com o código HTTP 200.

- **Simulação E2E:** Um job específico simula a interação do Selenium, verificando se a rota principal e o redirecionamento de `/` para `/products` funcionam corretamente.

## 3. Estabelecendo o Pipeline de CI/CD (GitHub Actions)

O arquivo `ci-cd.yml` foi organizado em quatro etapas (jobs) que se conectam entre si:

### Etapa 1: Construção e Segurança (Portão de Entrada)

Roda o comando `mvn clean verify`. Isso inicia a compilação, executa os testes e produz o arquivo .jar. O arquivo gerado é salvo para rastreabilidade.

### Etapa 2: Implantação Contínua/Continuous Deployment (Desenvolvimento)

O Railway fica de olho na branch principal. Quando a Etapa 1 termina bem, a implantação no ambiente de desenvolvimento começa sozinha. A Etapa 2 no GitHub Actions serve para registrar e acompanhar o começo dessa tarefa.

### Etapa 3: Validação Automática (Teste Rápido)

Essa etapa espera 30 segundos (tempo para o container Spring Boot começar) e faz o Health Check. Se a aplicação não subir no Railway (por erro no banco ou na porta, por exemplo), essa etapa falha e impede que o próximo passo aconteça.

### Etapa 4: Gerenciamento de Lançamento (Aprovação de Staging)

Foi colocado Regras de Proteção de Implantação. A função de merge com a branch de staging e o deploy no ambiente de homologação precisam de aprovação manual. Isso garante a intervenção humana, que é importante para o controle da qualidade do software.

## 4. Configuração do Ambiente e Armazenamento de Dados

O projeto foi construído priorizando o uso eficiente de recursos e a agilidade no desenvolvimento, com um modelo de persistência completamente automatizado:

- **Armazenamento em Memória (Banco de Dados H2):** Para otimizar a velocidade dos processos de CI/CD e simplificar o ajuste de processos no Railway, o sistema emprega o banco de dados H2. Essa opção permite que o ambiente seja configurado rapidamente a cada nova implementação, sem a demora de conexões externas.

- **Separação de Ambientes:** O sistema utiliza `spring.datasource.url=jdbc:h2:mem:inventory_db`, assegurando que cada execução do pipeline comece de um ponto inicial consistente, evitando o perigo de dados incorretos entre diferentes versões de teste.

- **Dados Coerentes (Arquivos de Inicialização):** O arquivo `data.sql` foi adaptado para o H2, garantindo que o inventário seja preenchido automaticamente sempre que o container é iniciado.

- **Sequência de Inicialização:** A propriedade `spring.jpa.defer-datasource-initialization=true` foi usada para garantir que as tabelas sejam criadas pelo Hibernate antes que os dados sejam inseridos.

- **Reinicialização do Estado:** O script inclui comandos `DELETE FROM products;` para possibilitar novas implementações sem conflitos de chaves primárias, preservando a integridade dos dados no sistema.

- **Adaptabilidade:** Ao usar o H2, o projeto se torna completamente adaptável e independente (Self-Contained), funcionando da mesma forma no computador do desenvolvedor, no Docker e no ambiente Cloud do Railway.

A decisão de usar o H2 In-Memory foi pensada para este TP5, com o objetivo de evidenciar a capacidade de automatizar completamente o banco de dados (Infrastructure as Code), onde a estrutura e os dados são refeitos automaticamente a cada envio para a branch principal, uma vez que o H2 é temporário (apaga ao reiniciar o container).

## 5. Resultados Obtidos

Após a finalização do desenvolvimento, o projeto demonstra os seguintes avanços:

1. **Menos Falhas Humanas:** A implantação é iniciada automaticamente por meio de código, eliminando a necessidade de intervenção manual em interfaces.

2. **Acompanhamento Detalhado:** Cada alteração no código-fonte está conectada a um registro de execução no GitHub Actions, permitindo um rastreamento completo.

3. **Documentação Sempre Atualizada:** A documentação da API é gerada dinamicamente através do Swagger UI, com acesso fácil e direto nos ambientes de implantação.

## Prints e evidências

### 1. Refatoração e Clean Code (Requisito 1)
- [Print do código do Controller](./screenshots/controller-print-see-hasErrors.png)
- [Print da estrutura de pastas](./screenshots/project-structure.png)

### 2. Workflows de CI/CD e Segurança (Requisito 2 e 4)

- [Print do Grafo do Pipeline](./screenshots/pipeline-graph.png)
- [Print do Grafo do Pipeline pt2](./screenshots/pipeline-graph-2.png)
- [Print do Summary (Markdown)](./screenshots/summary-md.png)
- [Print do CodeQL (SAST)](./screenshots/code-scanning-sec.png)

### 3. Cobertura de Testes e Qualidade (Requisito Não Funcional)

- [Print do Relatório JaCoCo](./screenshots/jacoco.png)
- [Badges de Status](./screenshots/passing-badge.png)

### 4. Deploy e Gerenciamento de Ambientes (Requisito 3 e 5)

- [Print do Railway (Ambientes)](./screenshots/railway-dashboard.png)
- [Print do Railway (Deploy rodando)](./screenshots/railway-project-running.png)
- [Print da Aprovação Manual](./screenshots/manual-approval.png)
- [Print do Health Check (Log) - Staging](./screenshots/healthy-check-staging.png)
- [Print do Health Check (Log) - Main](./screenshots/healthy-check-main.png)

### 5. Documentação da API e Funcionamento

- [Print do Swagger UI](./screenshots/swagger-ui.png)
- [Print da Interface (Thymeleaf)](./screenshots/dashboard.png)
