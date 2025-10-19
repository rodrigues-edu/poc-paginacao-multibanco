package br.com.pesquisas.paginacao.infra.test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.FileInputStream;
import java.util.Properties;

public class MongoNativeTest {

    public static void main(String[] args) throws Exception {
        // Carrega o application-local.properties
        Properties props = new Properties();
        props.load(new FileInputStream("src/main/resources/application-local.properties"));

        String uri = props.getProperty("spring.data.mongodb.uri");
        String databaseName = props.getProperty("spring.data.mongodb.database");

        System.out.println("🔍 Tentando conectar ao MongoDB...");
        System.out.println("🔗 URI: " + uri);

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);

            System.out.println("✅ Conectado ao MongoDB com sucesso!");
            System.out.println("📦 Banco: " + databaseName);
            System.out.println("🧠 Listando coleções disponíveis:");

            boolean hasCollections = false;
            for (String collectionName : database.listCollectionNames()) {
                hasCollections = true;
                System.out.println("📁 Coleção: " + collectionName);
            }

            if (!hasCollections) {
                System.out.println("⚠️ Nenhuma coleção encontrada em " + databaseName + ".");
            }

            Document stats = database.runCommand(new Document("dbStats", 1));
            System.out.println("📊 Estatísticas: " + stats.toJson());

        } catch (Exception e) {
            System.err.println("❌ Falha ao conectar ao MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}