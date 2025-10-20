package br.com.pesquisas.paginacao.adapters.out.repository.firestore;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.spring.data.firestore.Document;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collectionName = "exames")
public class ExamFirestore {

    @DocumentId
    private String id;

    @PropertyName("paciente_id")
    private String pacienteId;

    @PropertyName("nome_paciente")
    private String nomePaciente;

    @PropertyName("tipo_exame")
    private String tipoExame;

    @PropertyName("tipo_exame")
    private String statusExame;

    @PropertyName("valor_resultado")
    private Double valorResultado;

    @PropertyName("data_coleta")
    private Instant dataColeta;

    @PropertyName("data_resultado")
    private Instant dataResultado;

    @PropertyName("laboratorio")
    private String laboratorio;

    @PropertyName("created_at")
    private Instant createdAt;
}
