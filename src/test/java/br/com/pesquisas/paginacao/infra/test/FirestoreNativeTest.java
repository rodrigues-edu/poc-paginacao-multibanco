package br.com.pesquisas.paginacao.infra.test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import java.io.FileInputStream;

public class FirestoreNativeTest {
    public static void main(String[] args) throws Exception {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream("/Users/silva.rodriguesedu/Estudos/POCs/GCP/pocs-estudos-gerais-1160c4fcc3d7.json"));

        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId("pocs-estudos-gerais")
                .build();

        Firestore firestore = firestoreOptions.getService();

        System.out.println("✅ Conectado ao Firestore no projeto: " + firestore.getOptions().getProjectId());
        firestore.listCollections().forEach(c -> System.out.println("📁 Coleção: " + c.getId()));
    }
}
