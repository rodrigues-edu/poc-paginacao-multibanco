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
## 🧩 Geração e Importação dos Dados
Para testar a performance da paginação, a POC utiliza uma massa de dados significativa (≈ 500.000 registros) gerada automaticamente em CSV e importada para PostgreSQL e MongoDB.

### ⚙️ 1. Gerando o arquivo CSV
A classe ExameDataCsvGenerator (localizada em
`src/main/java/br/com/pesquisas/paginacao/infra/bootstrap/ExameDataCsvGenerator.java`)
é responsável por gerar um arquivo de dados sintéticos com estrutura compatível com todas as bases da POC.

Comando para executar:
 ```bash
   mvn exec:java -Dexec.mainClass="br.com.pesquisas.paginacao.infra.bootstrap.ExameDataCsvGenerator"
```
### 🧪 2. Verificando a geração
 Após a execução, confirme a quantidade de linhas geradas:
 ```bash
   wc -l src/main/resources/data/exames.csv
```
✅ O valor esperado é 500.001 (inclui o cabeçalho + 500.000 registros).

### ⚠️ 3. Evitando gerar novamente
Após gerar a massa uma vez, desative o bean para não reprocessar a cada build.
No arquivo `application.properties`:
 ```properties
   data.csv.generate=false
```

### 🐘 4. Importando para o PostgreSQL
Com o container `postgres-paginacao` ativo, importe o CSV diretamente para a tabela `exames`:
 ```bash
    cat src/main/resources/data/exames.csv | docker exec -i postgres-paginacao \
    psql -U postgres -d paginacao_db -c "\
    COPY exames(
        paciente_id,
        nome_paciente,
        tipo_exame,
        status_exame,
        valor_resultado,
        data_coleta,
        data_resultado,
        laboratorio,
        created_at
    )
    FROM STDIN
    DELIMITER ','
    CSV HEADER;"
```
#### 📌 Explicação:
- `cat ... | docker exec -i` → Envia o conteúdo do CSV diretamente para dentro do container.
- `psql -c "COPY ...` → Copia os dados para a tabela `exames` de forma otimizada.
- `cat ... | docker exec -i` → Indica que a primeira linha contém os nomes das colunas.

 **Observação** Se o volume do Postgres fosse **mapeado externamente**, o caminho seria, por exemplo: `/var/lib/postgresql/data/exames.csv` dentro do container.

### 🍃 5. Importando para o MongoDB
Como o container mongo-paginacao está sem autenticação, use o comando abaixo:
 ```bash
    docker exec -i mongo-paginacao \
    mongoimport \
      --db examesdb \
      --collection exames \
      --type csv \
      --headerline \
      --ignoreBlanks \
      --drop \
      --file /dev/stdin < src/main/resources/data/exames.csv
```
#### 📌 Explicação:
- `--drop` → Apaga a collection antes de importar.
- `--headerline` → Ignora células vazias.
- `< src/...` → Injeta o CSV do host diretamente no container.

**💡Dica** Se desejar ativar a autenticação no Mongo, basta adicionar:
 ```bash
   --username root --password example --authenticationDatabase admin
```
### 🔍 6. Verificando a importação
#### PostgreSQL:
 ```bash
    docker exec -it postgres-paginacao \
    psql -U postgres -d paginacao_db -c "SELECT COUNT(*) FROM exames;"
```

#### MongoDB:
 ```bash
    docker exec -it mongo-paginacao \
    mongosh --eval "use examesdb; db.exames.countDocuments();"
```
✅ O resultado esperado é aproximadamente 500.000 registros em ambas as bases.

### 📦 7. (Opcional) Estrutura dos dados
Os dados são compostos por campos sintéticos com coerência mínima para simular uma base real de exames laboratoriais:

| Campo           | Tipo     | Exemplo                 | Descrição                       |
| --------------- | -------- | ----------------------- | ------------------------------- |
| paciente_id     | UUID     | `2d91a12f-89ab-4bfa...` | Identificador único do paciente |
| nome_paciente   | String   | `Maria Oliveira`        | Nome do paciente                |
| tipo_exame      | String   | `Hemograma`             | Tipo do exame                   |
| status_exame    | String   | `Concluído`             | Status atual                    |
| valor_resultado | Decimal  | `142.3`                 | Valor numérico do resultado     |
| data_coleta     | Date     | `2024-09-10`            | Data da coleta                  |
| data_resultado  | Date     | `2024-09-12`            | Data do resultado               |
| laboratorio     | String   | `Laboratório Central`   | Nome do laboratório             |
| created_at      | DateTime | `2024-09-12T10:42:00`   | Timestamp de inserção           |

### 🔧 8. Observações
- Os volumes internos (mongo_data, postgres_data) são utilizados para evitar conflitos com o Colima (Foi utilizado um ambiente Mac para simulação).
- Se quiser inspecionar os arquivos dentro do container, use:
 ```bash
    docker exec -it postgres-paginacao bash
    docker exec -it mongo-paginacao bash
```
- Cada script e importação foi projetado para ser reproduzível e independente — ideal para POCs de performance.

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
