package db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void init() {
        String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        userId VARCHAR(255) PRIMARY KEY,
                        password VARCHAR(255) NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        profileImage VARCHAR(500)
                    )
                """;

        String createArticlesTable = """
                    CREATE TABLE IF NOT EXISTS articles (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        content TEXT,
                        imagePath VARCHAR(500),
                        writerUserId VARCHAR(255),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """;

        try (Connection conn = JdbcConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createArticlesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}