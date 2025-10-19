package br.com.pesquisas.paginacao.infra.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"local", "cloud"})
@RequiredArgsConstructor
public class PostgresHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            jdbcTemplate.execute("SELECT 1");
            log.debug("✅ PostgreSQL acessível.");
            return Health.up().withDetail("postgres", "Conexão OK").build();
        } catch (Exception e) {
            log.error("❌ Falha ao acessar PostgreSQL: {}", e.getMessage());
            return Health.down(e).withDetail("postgres", "Falha de conexão").build();
        }
    }
}
