package db;

import model.Article;

import java.sql.*;

public class ArticleRepository {

    public Long save(Article article) {
        String sql = "INSERT INTO articles (content, imagePath, writerUserId) VALUES (?, ?, ?)";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, article.getContent());
            pstmt.setString(2, article.getImagePath());
            pstmt.setString(3, article.getUserId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1L;
    }

    public Article findLatest() {
        String sql = "SELECT * FROM articles ORDER BY id DESC LIMIT 1";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return new Article(
                        rs.getLong("id"),
                        rs.getString("writerUserId"),
                        rs.getString("content"),
                        rs.getString("imagePath")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}