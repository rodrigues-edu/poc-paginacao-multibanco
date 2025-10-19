# 🧰 Scripts de Inicialização de Bancos

Esta pasta contém scripts opcionais utilizados pelo **DatabaseBootstrapper** na inicialização do projeto.

## 📄 Estrutura

| Arquivo | Banco | Descrição |
|----------|--------|------------|
| `create_tables.sql` | PostgreSQL | Cria a tabela `exames` se não existir. |
| `create_indexes.sql` | PostgreSQL | Cria índices adicionais na tabela `exames`. |
| `mongo-init.js` | MongoDB | Cria a collection `exames` e aplica índices. |
| `firestore.indexes.json` | Firestore | Define índices compostos para otimização de consultas. |

## ⚙️ Como funciona

Durante o startup da aplicação, o `DatabaseBootstrapper` busca automaticamente esses arquivos em: `classpath:/scripts/`


Se encontrados, eles são executados **em ordem de dependência**:
1️⃣ `create_tables.sql`  
2️⃣ `create_indexes.sql`  
3️⃣ `mongo-init.js`  
4️⃣ `firestore.indexes.json`

## 🚀 Dica
Os scripts são **idempotentes**, ou seja, podem ser executados várias vezes sem causar erro (uso de `IF NOT EXISTS`).


