package br.com.pesquisas.paginacao.infra.test;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class PostgresNativeTest {

    public static void main(String[] args) throws Exception {
        // Carrega o application-local.properties
        Properties props = new Properties();
        props.load(new FileInputStream("src/main/resources/application-local.properties"));

        String url = props.getProperty("spring.datasource.url");
        String user = props.getProperty("spring.datasource.username");
        String password = props.getProperty("spring.datasource.password");

        System.out.println("🔍 Tentando conectar ao PostgreSQL...");
        System.out.println("🔗 URL: " + url);

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            System.out.println("✅ Conectado ao PostgreSQL com sucesso!");
            System.out.println("📦 Banco: " + connection.getCatalog());
            System.out.println("🧠 Listando tabelas disponíveis:");

            ResultSet rs = statement.executeQuery(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';"
            );

            boolean hasTables = false;
            while (rs.next()) {
                hasTables = true;
                System.out.println("📁 Tabela: " + rs.getString("table_name"));
            }

            if (!hasTables) {
                System.out.println("⚠️ Nenhuma tabela encontrada no schema 'public'.");
            }

        } catch (Exception e) {
            System.err.println("❌ Falha ao conectar ao PostgreSQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}