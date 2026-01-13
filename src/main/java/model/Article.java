package model;

public class Article {

    private Long id;
    private String userId;
    private String content;

    public Article(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
