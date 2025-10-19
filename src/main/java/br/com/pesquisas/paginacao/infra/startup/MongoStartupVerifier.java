package br.com.pesquisas.paginacao.infra.startup;

import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class MongoStartupVerifier  implements ApplicationRunner {

    private final MongoClient mongoClient;

    @Value("${mongo.startup-check.enabled:true}")
    private boolean enabled;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) {
            log.info("⚙️ Verificação de MongoDB desabilitada via propriedade.");
            return;
        }

        log.info("🔍 Testando conexão com MongoDB...");
        try {
            mongoClient.listDatabaseNames().forEach(name -> log.info("📁 Banco: {}", name));
            log.info("✅ Conexão com MongoDB OK!");
        } catch (Exception e) {
            log.error("❌ Falha ao conectar ao MongoDB: {}", e.getMessage(), e);
        }
    }
}
