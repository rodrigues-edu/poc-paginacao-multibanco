// ===============================================
// Script: mongo-init.js
// Objetivo: Criação da collection 'exames' e índices no MongoDB
// ===============================================

db = db.getSiblingDB('paginacao'); // use o nome do database Mongo

if (!db.getCollectionNames().includes('exames')) {
    print('📁 Criando coleção "exames"...');
    db.createCollection('exames');
}

print('🔧 Aplicando índices na coleção "exames"...');

db.exames.createIndexes([
    {
        key: { paciente_id: 1, data_coleta: -1 },
        name: 'idx_paciente_data'
    },
    {
        key: { status_exame: 1, data_resultado: -1 },
        name: 'idx_status_resultado'
    },
    {
        key: { created_at: -1 },
        name: 'idx_created_at'
    }
]);

print('✅ Índices aplicados com sucesso no MongoDB.');
