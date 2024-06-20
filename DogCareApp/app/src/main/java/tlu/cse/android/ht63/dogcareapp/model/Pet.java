package tlu.cse.android.ht63.dogcareapp.model;

public class Pet {
    private String uid;
    private String name;
    private String image;
    private String userId;
    private String gender;
    private String type;
    private long age;
    private int kg;
    private long timeStamp;

    public Pet( String name, String image, String userId, String gender, String type, long age, int kg, long timeStamp) {
        this.name = name;
        this.image = image;
        this.userId = userId;
        this.gender = gender;
        this.type = type;
        this.age = age;
        this.kg = kg;
        this.timeStamp = timeStamp;
    }
    public Pet() {
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public long getAge() {
        return age;
    }
    public void setAge(long age) {
        this.age = age;
    }
    public int getKg() {
        return kg;
    }
    public void setKg(int kg) {
        this.kg = kg;
    }
    public long getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}

