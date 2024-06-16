package tlu.cse.android.ht63.dogcareapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import tlu.cse.android.ht63.dogcareapp.model.UserInfo;

public class UserInfoManager {
    private static UserInfoManager instance;
    private UserInfo userInfo;


    public static UserInfoManager getInstance() {
        if (instance == null) {
            instance = new UserInfoManager();
        }
        return instance;
    }

    private UserInfoManager() {
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }


    public void loadInfoFormFirebase(OnLoadInfoListener listener) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        userInfo = documentSnapshot.toObject(UserInfo.class);
                        if (userInfo == null) {
                            listener.onAuthNull();
                        } else {
                            listener.onSuccess();
                        }
                    })
                    .addOnFailureListener(listener::onFailure);
        } else {
            if (listener != null) {
                listener.onAuthNull();
            }
        }

    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public interface OnLoadInfoListener {
        void onSuccess();

        void onFailure(Exception e);

        void onAuthNull();

    }


}
