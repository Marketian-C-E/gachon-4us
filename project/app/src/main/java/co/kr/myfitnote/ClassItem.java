package co.kr.myfitnote;

public class ClassItem {
    String name;
    String date;
    String partipants;
    String thumbnailUrl;

    public ClassItem(String name, String date, String partipants, String thumbnailUrl) {
        this.name = name;
        this.date = date;
        this.partipants = partipants;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getpartipants() {
        return partipants;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

}