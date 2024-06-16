package tlu.cse.android.ht63.dogcareapp.model;

public class UserInfo {
    private String uid;
    private String name;
    private String password;
    private String email;
    private String gender;
    private long age;
    private String address;

    private String image;

    public UserInfo(String uid, String name, String password, String email, String gender, long age, String address, String image) {
        this.uid = uid;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.image = image;
    }

    public UserInfo() {
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

