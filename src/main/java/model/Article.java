package model;

public class Article {

    private Long id;
    private String userId;
    private String content;
    private String imagePath;

    public Article(String userId, String content, String imagePath) {
        this.userId = userId;
        this.content = content;
        this.imagePath = imagePath;
    }

    public Article(Long id, String userId, String content, String imagePath) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
