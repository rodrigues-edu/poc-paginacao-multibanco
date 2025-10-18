package br.com.pesquisas.paginacao.infra.startup;

import com.google.cloud.firestore.Firestore;
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
public class FirestoreStartupVerifier implements ApplicationRunner {

    private final Firestore firestore;

    @Value("${firestore.startup-check.enabled:true}")
    private boolean enabled;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) {
            log.info("⚙️ Verificação de Firestore desabilitada via propriedade.");
            return;
        }

        log.info("🔍 Testando conexão com Firestore...");
        try {
            firestore.listCollections().forEach(c -> log.info("📁 Coleção: {}", c.getId()));
            log.info("✅ Conexão com Firestore OK!");
        } catch (Exception e) {
            log.error("❌ Falha ao conectar ao Firestore: {}", e.getMessage(), e);
        }
    }
}