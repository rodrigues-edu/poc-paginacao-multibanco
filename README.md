# 🧩 POC - Paginação Multibanco (Offset, Cursor e Time-Based)

POC de benchmark para **avaliação de estratégias de paginação** em múltiplos bancos de dados (**PostgreSQL**, **MongoDB** e **Firestore**), utilizando **Spring Boot 3**, **Micrometer**, **Actuator** e **k6** para coleta e análise de métricas de desempenho.

---

## 🎯 Objetivo

O objetivo desta POC é **comparar o comportamento e a performance** de diferentes estratégias de paginação de APIs:

- **Offset + Limit Pagination** — modelo tradicional com paginação por deslocamento.
- **Cursor-Based (Keyset Pagination)** — baseado em marcadores de posição (chaves).
- **Time-Based Pagination** — baseado em intervalos temporais.

Além disso, o projeto foi desenhado para suportar múltiplos bancos de dados por meio de **arquitetura hexagonal**, permitindo alternância dinâmica de adaptadores via **feature flag**, sem necessidade de reiniciar a aplicação.

---

## ⚙️ Stack Tecnológica

| Categoria | Tecnologia / Biblioteca                        |
|------------|------------------------------------------------|
| Linguagem | Java 21                                        |
| Framework | Spring Boot 3.5.7                              |
| Banco Relacional | PostgreSQL                                     |
| Banco NoSQL | MongoDB, Firestore (GCP)                       |
| Observabilidade | Spring Boot Actuator + Micrometer (Prometheus) |
| Teste de Carga | k6                                             |
| Build Tool | Maven                                          |
| Arquitetura | Hexagonal Architecture (Ports & Adapters)      |
| Containerização | Docker / Docker Compose                        |

---

## 🧱 Estrutura do Projeto
Aqui está um exemplo de como organizamos a aplicação `POC Paginação Multibanco` utilizando a arquitetura hexagonal:

<pre>
poc-paginacao-multibanco/
src/main/java/br/com/pesquisas/paginacao/
├── adapter               # Adaptadores de entrada e saída
│   ├── in
│   │   ├── apis          # APIs externas
│   │   ├── dtos          # Data Transfer Objects
│   │   ├── mappers       # Mapeadores
│   │   ├── resources     # Recursos estáticos
│   │   └── rest          # Controladores REST
│   └── out
│       ├── firestore     # Implementações para GCP Firestore
│       ├── feign         # Clientes Feign para chamadas a serviços externos
│       └── persistencia  # Outras implementações de persistência
├── application
│   ├── controller        # Controladores REST ou endpoints
│   ├── dto               # Objetos de Transferência de Dados (DTOs)
│   ├── service           # Implementações de casos de uso
│   └── port
│       ├── in            # Interfaces de portas de entrada (Input Ports)
│       └── out           # Interfaces de portas de saída (Output Ports)
├── domain                # Lógica de negócios (Domínio)
│   ├── model             # Entidades e Objetos de Valor
│   ├── repository        # Interfaces de repositórios (Portas de Saída)
│   └── service           # Regras de negócios
└── infra                 # Implementações técnicas (Infraestrutura)
    ├── config            # Configurações
    ├── interceptor       # Interceptores e aspectos
    └── repository        # Implementações de repositórios (adaptadores secundários)
├── pom.xml               # Adaptadores de entrada e saída
├── README.md             # Adaptadores de entrada e saída
</pre>




## 🚀 Como Executar Localmente
...
---

## 🌐 Endpoints Disponíveis
...
---

## 📊 Métricas e Observabilidade
...

## 🧩 Arquitetura e Estratégia Multi-Banco

O projeto adota **arquitetura hexagonal**, onde as interfaces de domínio (`ports`) são implementadas por adaptadores externos de banco.  
A troca entre bancos é controlada por **feature flag**:

```yaml
feature:
  database:
    active: postgres  # opções: postgres | mongo | firestore



.
