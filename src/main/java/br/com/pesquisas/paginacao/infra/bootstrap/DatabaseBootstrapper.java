package br.com.pesquisas.paginacao.infra.bootstrap;

import com.google.cloud.firestore.Firestore;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DatabaseBootstrapper {

    private final Firestore firestore;
    private final MongoTemplate mongoTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationContext applicationContext; // ✅ evita ambiguidade de ResourceLoader

    @Value("${bootstrap.enabled:true}")
    private boolean bootstrapEnabled;

    @Value("${firestore.startup-check.enabled:true}")
    private boolean firestoreEnabled;

    @Value("${bootstrap.scripts.path:classpath:/scripts/}")
    private String scriptsPath;

    public DatabaseBootstrapper(
            Firestore firestore,
            MongoTemplate mongoTemplate,
            JdbcTemplate jdbcTemplate,
            ApplicationContext applicationContext
    ) {
        this.firestore = firestore;
        this.mongoTemplate = mongoTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabases() {
        if (!bootstrapEnabled) {
            log.info("⚠️ Bootstrap desabilitado via 'bootstrap.enabled=false'");
            return;
        }

        log.info("🚀 Iniciando bootstrap de bancos...");
        runScriptsOrFallback();
        log.info("✨ Bootstrap concluído!");
    }

    private void runScriptsOrFallback() {
        log.info("📜 Verificando scripts externos em {}", scriptsPath);

        executeSqlScript("create_tables.sql", this::initPostgres);
        executeSqlScript("create_indexes.sql", this::initPostgresIndexes);
        executeMongoScript("mongo-init.js", this::initMongo);
        executeFirestoreJson("firestore.indexes.json", this::initFirestore);
    }

    // ======================= POSTGRES =======================
    private void initPostgres() {
        try {
            log.info("🧩 Criando tabela 'exames' no PostgreSQL (fallback)...");
            // language=PostgreSQL
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS exames (
                    id SERIAL PRIMARY KEY,
                    paciente_id VARCHAR(50),
                    nome_paciente VARCHAR(120),
                    tipo_exame VARCHAR(100),
                    status_exame VARCHAR(40),
                    valor_resultado NUMERIC(10,2),
                    data_coleta TIMESTAMP,
                    data_resultado TIMESTAMP,
                    laboratorio VARCHAR(100),
                    observacao TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            log.info("✅ Tabela criada/verificada no PostgreSQL.");
        } catch (Exception e) {
            log.error("❌ Erro ao criar tabela no PostgreSQL: {}", e.getMessage(), e);
        }
    }

    private void initPostgresIndexes() {
        try {
            log.info("🔧 Criando índices no PostgreSQL (fallback)...");
            // language=PostgreSQL
            jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS idx_paciente_data
                ON exames (paciente_id, data_coleta DESC)
            """);
            // language=PostgreSQL
            jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS idx_status_resultado
                ON exames (status_exame, data_resultado DESC)
            """);
            // language=PostgreSQL
            jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS idx_created_at
                ON exames (created_at DESC)
            """);
            log.info("✅ Índices PostgreSQL criados/verificados.");
        } catch (Exception e) {
            log.error("❌ Erro ao criar índices no PostgreSQL: {}", e.getMessage(), e);
        }
    }

    // ======================= MONGODB ========================
    private void initMongo() {
        try {
            MongoDatabase db = mongoTemplate.getDb();

            log.info("🧩 Garantindo coleção e índices no MongoDB (fallback)...");
            var collections = db.listCollectionNames().into(new java.util.ArrayList<>());
            if (!collections.contains("exames")) {
                db.createCollection("exames");
                log.info("✅ Coleção 'exames' criada.");
            }

            // índices programáticos (equivalente ao mongo-init.js)
            db.runCommand(new Document("createIndexes", "exames")
                    .append("indexes", List.of(
                            new Document("key", new Document("paciente_id", Optional.of(1))
                                    .append("data_coleta", Optional.of(-1)))
                                    .append("name", "idx_paciente_data"),
                            new Document("key", new Document("status_exame", Optional.of(1))
                                    .append("data_resultado", Optional.of(-1)))
                                    .append("name", "idx_status_resultado"),
                            new Document("key", new Document("created_at", Optional.of(-1)))
                                    .append("name", "idx_created_at")
                    )));
            log.info("✅ Índices MongoDB aplicados.");
        } catch (Exception e) {
            log.error("❌ Erro ao inicializar MongoDB: {}", e.getMessage(), e);
        }
    }

    // ======================= FIRESTORE ======================
    private void initFirestore() {
        if (!firestoreEnabled) {
            log.info("⚠️ Firestore desabilitado via 'firestore.startup-check.enabled=false'");
            return;
        }
        try {
            log.info("🧩 Criando documento sentinel no Firestore...");
            firestore.collection("exames")
                    .document("_init")
                    .set(Map.of("initializedAt", Instant.now()))
                    .get();
            log.info("✅ Firestore acessível e coleção inicializada.");
            log.info("ℹ️ Índices Firestore: use 'scripts/firestore.indexes.json' com gcloud CLI.");
        } catch (Exception e) {
            log.error("❌ Falha ao inicializar Firestore: {}", e.getMessage(), e);
        }
    }

    // ======================= EXECUTORES DE SCRIPTS =======================
    private void executeSqlScript(String fileName, Runnable fallback) {
        try {
            Resource resource = applicationContext.getResource(scriptsPath + fileName);
            if (resource.exists()) {
                log.info("📜 Executando script SQL: {}", fileName);
                String sql = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                        .lines().collect(Collectors.joining("\n"));
                // divide por ';' e executa cada statement
                for (String statement : sql.split(";")) {
                    if (!statement.isBlank()) {
                        // language=PostgreSQL
                        jdbcTemplate.execute(statement);
                    }
                }

                log.info("✅ Script '{}' executado com sucesso.", fileName);
            } else {
                log.info("⏭️ Script '{}' não encontrado, usando fallback.", fileName);
                if (fallback != null) fallback.run();
            }
        } catch (Exception e) {
            log.error("❌ Erro ao executar script '{}': {}", fileName, e.getMessage(), e);
        }
    }

    private void executeMongoScript(String fileName, Runnable fallback) {
        try {
            Resource resource = applicationContext.getResource(scriptsPath + fileName);
            if (resource.exists()) {
                log.info("📜 Executando script MongoDB: {}", fileName);
                // Em Java, não executamos JS direto; aplicamos o conteúdo esperado programaticamente.
                // O fallback já cobre a criação de índices/coleção. Apenas logamos a presença do arquivo.
                log.info("ℹ️ 'mongo-init.js' detectado — comportamento equivalente aplicado via código.");
                if (fallback != null) fallback.run(); // aplica via código
                log.info("✅ Estrutura Mongo aplicada (via fallback programático).");
            } else {
                log.info("⏭️ Script '{}' não encontrado, usando fallback.", fileName);
                if (fallback != null) fallback.run();
            }
        } catch (Exception e) {
            log.error("❌ Erro ao processar script Mongo '{}': {}", fileName, e.getMessage(), e);
        }
    }

    private void executeFirestoreJson(String fileName, Runnable fallback) {
        try {
            Resource resource = applicationContext.getResource(scriptsPath + fileName);
            if (resource.exists()) {
                log.info("📜 '{}' detectado. Aplique os índices via CLI:", fileName);
                log.info("   gcloud firestore indexes composite create --file=scripts/{}", fileName);
            } else {
                log.info("⏭️ Script '{}' não encontrado, usando fallback.", fileName);
            }
            if (fallback != null) fallback.run(); // garante doc sentinel
        } catch (Exception e) {
            log.error("❌ Erro ao verificar script Firestore '{}': {}", fileName, e.getMessage(), e);
        }
    }
}
