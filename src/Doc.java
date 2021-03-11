public class Doc {
    private String id, title, data;

    public Doc(String id, String title, String data) {
        this.id = id;
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Doc{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

}
