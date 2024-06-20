package tlu.cse.android.ht63.dogcareapp.model;

public class Stories {
    private String uid;
    private String userId;
    private String title;
    private String image;
    private long time;
    private long timeStamp;

    public Stories(String uid, String userId, String title, String image, long time, long timeStamp) {
        this.uid = uid;
        this.userId = userId;
        this.title = title;
        this.image = image;
        this.time = time;
        this.timeStamp = timeStamp;
    }
    public Stories() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
