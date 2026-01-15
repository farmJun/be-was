package db;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public void save(User user) {
        String sql = "INSERT INTO users (userId, password, name, email, profileImage) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getProfileImage());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findByUserId(String userId) {
        String sql = "SELECT * FROM users WHERE userId = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("userId"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email")
                    );
                    user.setProfileImage(rs.getString("profileImage"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void update(User user) {
        String sql = "UPDATE users SET password = ?, name = ?, profileImage = ? WHERE userId = ?";

        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getProfileImage());
            pstmt.setString(4, user.getUserId()); // WHERE 조건

            int result = pstmt.executeUpdate();
            if (result == 0) {
                System.out.println("업데이트 실패: 해당 userId를 찾을 수 없음");
            }

        } catch (SQLException e) {
            throw new RuntimeException("유저 업데이트 중 오류 발생", e);
        }
    }}
