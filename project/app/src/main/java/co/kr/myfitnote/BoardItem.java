package co.kr.myfitnote;

public class BoardItem {
    private String title;
    private String content;
    private String thumbnail;
    private String writer;
    private String date;

    public BoardItem(String title, String content, String thumbnail) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
    }

    public BoardItem(String title, String content, String thumbnail, String writer, String date) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.writer = writer;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


    public String getThumbnail() {
        return thumbnail;
    }

    public String getWriter() {
        return writer;
    }

    public String getDate() {
        return date;
    }
}
