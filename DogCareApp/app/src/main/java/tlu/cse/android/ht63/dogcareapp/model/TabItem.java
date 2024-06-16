package tlu.cse.android.ht63.dogcareapp.model;

public class TabItem {

    private String name;
    private int image;
    private int badge;
    private boolean isSelected;
    public TabItem(String name, int image, int badge) {
        this.name = name;
        this.image = image;
        this.badge = badge;

    }
    public TabItem() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
