package br.com.pesquisas.paginacao.infra.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gera um arquivo CSV com dados de exames simulados.
 * Ativado apenas se 'data.csv.generate=true' em application.properties.
 */
@Slf4j
@Component
@Profile("local")
@ConditionalOnProperty(
        name = "data.csv.generate",
        havingValue = "true",
        matchIfMissing = false
)
public class ExameDataCsvGenerator implements CommandLineRunner {

    private static final int TOTAL_REGISTROS = 500_000;
    private static final int TOTAL_PACIENTES = 1_000;

    private static final List<String> TIPOS_EXAME = List.of("Glicose", "Colesterol", "Hemograma", "Creatinina", "Triglicerídeos");
    private static final List<String> STATUS_EXAME = List.of("PENDENTE", "CONCLUIDO", "CANCELADO");
    private static final List<String> LABORATORIOS = List.of("Laboratório Dasa", "Laboratório Fleury", "Laboratório São Lucas", "Lab Anhembi", "Lab Paulista");
    private static final List<String> NOMES_PACIENTES = List.of(
            "João Silva", "Maria Oliveira", "Carlos Souza", "Ana Paula", "Fernanda Lima",
            "Paulo Mendes", "Juliana Rocha", "Eduardo Alves", "Camila Costa", "Felipe Martins"
    );

    @Override
    public void run(String... args) {
        String filePath = "src/main/resources/data/exames.csv";
        log.info("📄 Iniciando geração do arquivo CSV de exames ({} registros)...", TOTAL_REGISTROS);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("paciente_id,nome_paciente,tipo_exame,status_exame,valor_resultado,data_coleta,data_resultado,laboratorio,created_at\n");

            ThreadLocalRandom random = ThreadLocalRandom.current();
            List<String> pacientesFixos = gerarPacientesFixos();

            for (int i = 0; i < TOTAL_REGISTROS; i++) {
                String pacienteId = pacientesFixos.get(random.nextInt(pacientesFixos.size()));
                String nome = NOMES_PACIENTES.get(random.nextInt(NOMES_PACIENTES.size()));
                String tipo = TIPOS_EXAME.get(random.nextInt(TIPOS_EXAME.size()));
                String status = STATUS_EXAME.get(random.nextInt(STATUS_EXAME.size()));
                String lab = LABORATORIOS.get(random.nextInt(LABORATORIOS.size()));

                LocalDateTime dataColeta = randomDateInLastMonths(6);
                LocalDateTime dataResultado = dataColeta.plusDays(random.nextInt(1, 4));
                BigDecimal valor = gerarValorPorTipo(tipo, random);

                writer.append(String.join(",",
                        pacienteId,
                        nome,
                        tipo,
                        status,
                        String.valueOf(valor),
                        dataColeta.toString(),
                        dataResultado.toString(),
                        lab,
                        dataResultado.toString()
                )).append("\n");

                if (i > 0 && i % 50_000 == 0) {
                    writer.flush(); // 🔥 força a escrita no disco
                    log.info("🧩 Gerados {} registros...", i);
                }
            }
            writer.flush(); // 🔥 garante o flush final antes do close

            log.info("✅ Arquivo CSV gerado com sucesso em: {}", filePath);

        } catch (IOException e) {
            log.error("❌ Erro ao gerar CSV: {}", e.getMessage(), e);
        }
    }

    private List<String> gerarPacientesFixos() {
        return java.util.stream.IntStream.range(0, TOTAL_PACIENTES)
                .mapToObj(i -> UUID.randomUUID().toString().substring(0, 8))
                .toList();
    }

    private LocalDateTime randomDateInLastMonths(int months) {
        var now = LocalDateTime.now();
        var start = now.minusMonths(months);
        long startEpoch = start.toEpochSecond(java.time.ZoneOffset.UTC);
        long endEpoch = now.toEpochSecond(java.time.ZoneOffset.UTC);
        long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
        return LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC);
    }

    private BigDecimal gerarValorPorTipo(String tipo, ThreadLocalRandom random) {
        return switch (tipo.toLowerCase()) {
            case "glicose" -> BigDecimal.valueOf(70 + random.nextDouble() * 90);
            case "colesterol" -> BigDecimal.valueOf(120 + random.nextDouble() * 100);
            case "hemograma" -> BigDecimal.valueOf(4 + random.nextDouble() * 3);
            case "creatinina" -> BigDecimal.valueOf(0.5 + random.nextDouble() * 1.5);
            case "triglicerídeos" -> BigDecimal.valueOf(50 + random.nextDouble() * 200);
            default -> BigDecimal.valueOf(random.nextDouble() * 100);
        };
    }
}
