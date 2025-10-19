package br.com.pesquisas.paginacao.infra.health;

import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"local", "cloud"})
@RequiredArgsConstructor
public class MongoHealthIndicator implements HealthIndicator {

    private final MongoClient mongoClient;


    @Override
    public Health health() {
        try {
            mongoClient.listDatabaseNames().first();
            log.debug("✅ MongoDB acessível.");
            return Health.up().withDetail("mongo", "Conexão OK").build();
        } catch (Exception e) {
            log.error("❌ Falha ao acessar MongoDB: {}", e.getMessage());
            return Health.down(e).withDetail("mongo", "Falha de conexão").build();
        }
    }
}
