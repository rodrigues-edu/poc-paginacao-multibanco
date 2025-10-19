package br.com.pesquisas.paginacao.infra.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class PostgresStartupVerifier implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${postgres.startup-check.enabled:true}")
    private boolean enabled;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!enabled) {
            log.info("⚙️ Verificação de PostgreSQL desabilitada via propriedade.");
            return;
        }

        log.info("🔍 Testando conexão com PostgreSQL...");
        try {
            jdbcTemplate.queryForObject("SELECT version()", String.class);
            log.info("✅ Conexão com PostgreSQL OK!");
        } catch (Exception e) {
            log.error("❌ Falha ao conectar ao PostgreSQL: {}", e.getMessage(), e);
        }
    }
}
