package br.com.pesquisas.paginacao.infra.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Configuração manual do Firestore para garantir controle total
 * sobre a autenticação e o contexto de banco de dados.
 *
 * Evita dependência de autoconfiguração e permite alternar entre
 * credenciais locais e ADC (Application Default Credentials) em Cloud Run.
 */
@Configuration
public class FirestoreConfig {

    @Bean
    public Firestore firestore() throws IOException {

        // Caminho local do arquivo de credenciais
        String credentialsPath = "/Users/silva.rodriguesedu/Estudos/POCs/GCP/pocs-estudos-gerais-1160c4fcc3d7.json";

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(credentialsPath));

        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId("pocs-estudos-gerais")
                .setDatabaseId("(default)") // obrigatório para Firestore Native
                .build();

        return firestoreOptions.getService();
    }
}
