package br.com.pesquisas.paginacao.infra.health;

import com.google.cloud.firestore.Firestore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"local", "cloud"}) // opcional — ativa em ambos se quiser
public class FirestoreHealthIndicator implements HealthIndicator {

    private final Firestore firestore;

    public FirestoreHealthIndicator(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Health health() {
        try {
            // Tenta listar coleções apenas como validação leve
            var collections = firestore.listCollections();
            if (collections.iterator().hasNext()) {
                log.debug("✅ Firestore acessível (coleções detectadas).");
                return Health.up()
                        .withDetail("firestore", "Conexão OK")
                        .withDetail("projectId", firestore.getOptions().getProjectId())
                        .build();
            } else {
                log.warn("⚠️ Firestore respondeu, mas nenhuma coleção encontrada.");
                return Health.up()
                        .withDetail("firestore", "Sem coleções detectadas")
                        .build();
            }
        } catch (Exception e) {
            log.error("❌ Falha ao acessar Firestore: {}", e.getMessage());
            return Health.down(e)
                    .withDetail("firestore", "Falha de conexão ou credencial inválida")
                    .build();
        }
    }
}
