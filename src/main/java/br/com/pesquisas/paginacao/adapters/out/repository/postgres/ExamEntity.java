package br.com.pesquisas.paginacao.adapters.out.repository.postgres;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "exames")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "paciente_id", length = 36, nullable = false)
    private String pacienteId;

    @Column(name = "nome_paciente", length = 120)
    private String nomePaciente;

    @Column(name = "tipo_exame", length = 80)
    private String tipoExame;

    @Column(name = "status_exame", length = 40)
    private String statusExame;

    @Column(name = "valor_resultado")
    private Double valorResultado;

    @Column(name = "data_coleta")
    private Instant dataColeta;

    @Column(name = "data_resultado")
    private Instant dataResultado;

    @Column(name = "laboratorio", length = 80)
    private String laboratorio;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
