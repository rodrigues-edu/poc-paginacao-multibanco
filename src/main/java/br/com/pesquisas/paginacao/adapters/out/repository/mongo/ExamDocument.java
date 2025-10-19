package br.com.pesquisas.paginacao.adapters.out.repository.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "exames")
@CompoundIndex(def = "{'pacienteId': 1, 'dataColeta': -1}", name = "idx_paciente_data")
public class ExamDocument {

    @Id
    private String id;

    @Field("paciente_id")
    @Indexed(name = "idx_paciente")
    private String pacienteId;

    @Field("nome_paciente")
    private String nomePaciente;

    @Field("tipo_exame")
    @Indexed(name = "idx_tipo_exame")
    private String tipoExame;

    @Field("status_exame")
    @Indexed(name = "idx_status_exame")
    private String statusExame;

    @Field("valor_resultado")
    private Double valorResultado;

    @Field("data_coleta")
    @Indexed(direction = IndexDirection.DESCENDING, name = "idx_data_coleta")
    private Instant dataColeta;

    @Field("data_resultado")
    @Indexed(direction = IndexDirection.DESCENDING, name = "idx_data_resultado")
    private Instant dataResultado;

    @Field("laboratorio")
    private String laboratorio;

    @Field("created_at")
    @Indexed(direction = IndexDirection.DESCENDING, name = "idx_created_at")
    private Instant createdAt;
}
