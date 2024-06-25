package tlu.cse.android.ht63.dogcareapp.model;

public class Event {
    private String uid;
    private String petId;
    private String userId;
    private String petName;
    private int active;
    private long dateTime;
    private String keyFilter;
    private int habit;
    private String note;
    private long timeStamp;

    public Event(String uid, String petId, String userId, String petName, int active, long dateTime, String keyFilter, int habit, String note, long timeStamp) {
        this.uid = uid;
        this.petId = petId;
        this.userId = userId;
        this.petName = petName;
        this.active = active;
        this.dateTime = dateTime;
        this.keyFilter = keyFilter;
        this.habit = habit;
        this.note = note;
        this.timeStamp = timeStamp;
    }

    public Event() {
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
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

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getKeyFilter() {
        return keyFilter;
    }

    public void setKeyFilter(String keyFilter) {
        this.keyFilter = keyFilter;
    }

    public int getHabit() {
        return habit;
    }

    public void setHabit(int habit) {
        this.habit = habit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
