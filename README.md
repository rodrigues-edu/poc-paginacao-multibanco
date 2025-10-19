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
## 🧩 Ambiente de Banco de Dados — Docker Compose

O ambiente de **bancos de dados da POC** é orquestrado via **Docker Compose**, contendo:

- **PostgreSQL 15** → Banco relacional principal
- **MongoDB 6** → Banco NoSQL para testes de paginação
- **Adminer** → Interface web para acessar o PostgreSQL
- **Mongo Express** → Interface web para acessar o MongoDB
- **Aplicação Spring Boot** → Serviço principal `poc-paginacao-multibanco`

---

### 🚀 Subindo o ambiente

```bash
  docker compose up -d
```



## 🌐 Endpoints Disponíveis
| Serviço         | Porta | URL de Acesso                                  | Descrição                   |
| --------------- | ----- | ---------------------------------------------- | --------------------------- |
| PostgreSQL      | 5432  | –                                              | Banco relacional            |
| MongoDB         | 27017 | –                                              | Banco NoSQL                 |
| Adminer         | 8081  | [http://localhost:8081](http://localhost:8081) | Interface web do PostgreSQL |
| Mongo Express   | 8082  | [http://localhost:8082](http://localhost:8082) | Interface web do MongoDB    |
| Spring Boot App | 8080  | [http://localhost:8080](http://localhost:8080) | API principal da POC        |

---
## 📖 Endpoints de Paginação

Cada endpoint demonstra uma estratégia diferente de paginação, permitindo comparar desempenho e comportamento entre abordagens SQL e NoSQL.

| Tipo de Paginação  | Endpoint            | Parâmetros            | Descrição breve                                                                                                 |
| ------------------ | ------------------- | --------------------- | --------------------------------------------------------------------------------------------------------------- |
| **Offset + Limit** | `/paginacao/offset` | `page`, `size`        | Paginação tradicional com `OFFSET` e `LIMIT`. Fácil de implementar, mas menos eficiente em grandes bases.       |
| **Cursor-Based**   | `/paginacao/cursor` | `lastId`, `limit`     | Paginação baseada em cursores (Keyset Pagination). Evita saltos grandes, ideal para alto volume de dados.       |
| **Time-Based**     | `/paginacao/time`   | `from`, `to`, `limit` | Paginação por intervalo de tempo (`timestamp`). Excelente para dados temporais como logs ou registros clínicos. |
---

## 📊 Métricas e Observabilidade
O projeto conta com instrumentação nativa via Spring Boot Actuator e Micrometer, permitindo expor métricas de saúde, desempenho e latência das requisições HTTP — essenciais para acompanhar o comportamento de cada tipo de paginação durante os testes de carga com o k6.

| Tipo                       | Endpoint                                                            | Descrição                                                          |
| -------------------------- | ------------------------------------------------------------------- | ------------------------------------------------------------------ |
| 🩺 **Health Check**        | [`/actuator/health`](http://localhost:8080/actuator/health)         | Exibe o status geral da aplicação e do Firestore (UP/DOWN).        |
| 📈 **Métricas**            | [`/actuator/metrics`](http://localhost:8080/actuator/metrics)       | Lista todas as métricas coletadas pelo Micrometer.                 |
| ⏱️ **HTTP Server Metrics** | `/actuator/metrics/http.server.requests`                            | Métricas de requisições HTTP (latência, status, método, endpoint). |
| 📊 **Prometheus Endpoint** | [`/actuator/prometheus`](http://localhost:8080/actuator/prometheus) | Exposição das métricas no formato Prometheus (caso habilitado).    |

---

## 🧭 Exemplos de Métricas Disponíveis
| Métrica                      | Descrição                                    |
| ---------------------------- | -------------------------------------------- |
| `http.server.requests.count` | Quantidade total de requisições processadas. |
| `http.server.requests.max`   | Tempo máximo de resposta registrado.         |
| `http.server.requests.mean`  | Latência média das requisições.              |
| `jvm.memory.used`            | Uso atual de memória da JVM.                 |
| `system.cpu.usage`           | Utilização média de CPU.                     |

---

## 🧩 Arquitetura e Estratégia Multi-Banco

O projeto adota **arquitetura hexagonal**, onde as interfaces de domínio (`ports`) são implementadas por adaptadores externos de banco.  
A troca entre bancos é controlada por **feature flag**:

```yaml
# application-local.properties
feature.database.active=postgres  # opções: postgres | mongo | firestore

```

## 🧱 Comandos úteis
 ```bash
   # Subir o ambiente
   docker compose up -d
```
 ```bash
   # Verificar status
   docker ps
```
 ```bash
   # Parar containers
   docker compose down
```

 ```bash
   # Reiniciar ambiente e limpar volumes
   docker compose down -v && docker compose up -d
```

---

## 🧪 Teste de Carga com k6
Os testes de carga são realizados com o k6
— uma ferramenta open source desenvolvida pela Grafana Labs, focada em testes de performance baseados em código.
Este módulo permite validar o comportamento da API sob diferentes níveis de carga, simulando usuários reais acessando os endpoints de paginação.

A pasta k6/ contém os scripts utilizados para simular carga real sobre os endpoints de paginação, medindo latência, throughput e resiliência sob estresse.
Esses scripts podem ser executados localmente ou integrados em pipelines de CI/CD.
### ⚙️ Instalação do k6
Via Homebrew (macOS)
 ```bash
   brew install k6
```

Linux (via apt)
 ```bash
   sudo apt install k6
```

Windows (via Chocolatey)
 ```bash
   choco install k6
```

Via Docker
 ```bash
   docker run -i grafana/k6 run - < script.js
```

### ▶️ Como Executar o Teste
No terminal, a partir da raiz do projeto:
 ```bash
   k6 run k6/paginacao-test.js
```

Saída esperada (resumida):
 ```makefile
running (0m50.0s), 10 virtual users, 3 complete and 0 interrupted iterations
http_req_duration..................: avg=182ms   p(95)=420ms
http_reqs.........................: 300  6.0/s
checks............................: 100.0% ✓ 0.0% ✗
```

---
## 📈 Integração com Prometheus e Grafana
Para visualização em tempo real:
#### 1. Executar o k6 em modo exportador:
 ```bash
   K6_OUT=prometheus k6 run k6/paginacao-test.js
```
#### 2. Acessar o Prometheus (http://localhost:9090)
#### 3. Visualizar os gráficos no Grafana (http://localhost:3000)

---
## 📊 Resultados e Relatório de Performance Comparativo
Abaixo, um resumo comparativo dos três tipos de paginação implementados nesta POC, com base nos resultados médios coletados via k6:

| Tipo de Paginação | Latência Média (ms) | Taxa de Sucesso | Vantagem Principal                    | Cenário Ideal                           |
| ----------------- | ------------------- | --------------- | ------------------------------------- | --------------------------------------- |
| Offset + Limit    | 230                 | 99.8%           | Simplicidade e compatibilidade ampla  | Paginação tradicional em bancos SQL     |
| Cursor-Based      | 150                 | 99.9%           | Melhor desempenho em datasets grandes | APIs com alto volume e navegação fluida |
| Time-Based        | 180                 | 99.7%           | Paginação temporal eficiente          | Consultas por faixa de datas            |

## 📘 **Análise:**
- ⚡ **Cursor-Based:** melhor performance e escalabilidade.
- ⚙️ **Offset:** simples, porém degrada com grandes datasets.
- ⏱️ **Time-Based:** ideal para consultas temporais e relatórios.
