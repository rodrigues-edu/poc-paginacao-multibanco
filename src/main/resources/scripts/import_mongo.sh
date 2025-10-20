#!/bin/bash
echo "🚀 Importando dados para MongoDB..."

mongoimport \
  --uri "mongodb://root:example@localhost:27017" \
  --db examesdb \
  --collection exames \
  --type csv \
  --headerline \
  --file src/main/resources/data/exames.csv

echo "✅ Importação concluída!"
