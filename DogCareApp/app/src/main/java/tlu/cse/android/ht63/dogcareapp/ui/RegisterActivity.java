package tlu.cse.android.ht63.dogcareapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import tlu.cse.android.ht63.dogcareapp.MainActivity;
import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends BaseActivity {
    protected ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        binding.btLogin.setOnClickListener(v -> checkData());
        binding.tvGoBackLogin.setOnClickListener(v -> finish());
        binding.imgBack.setOnClickListener(v -> finish());
    }

    private void checkData() {
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();
        String name = Objects.requireNonNull(binding.edtName.getText()).toString().trim();
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu tối thiểu 6 kí tự", Toast.LENGTH_SHORT).show();
            } else {
                register(email, password, name);
            }
        }
    }

    private void register(String email, String password, String name) {
        showLoading();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        pushData(email, password, name, Objects.requireNonNull(user).getUid());
                    } else {
                        hideLoading();
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void pushData(String email, String password, String name, String uid) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);
        map.put("name", name);
        map.put("uid", uid);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(uid)
                .set(map, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    if (isUnavailable()) {
                        return;
                    }
                    hideLoading();
                    loginToMain();
                })
                .addOnFailureListener(e -> {
                    if (isUnavailable()) {
                        return;
                    }
                    Toast.makeText(RegisterActivity.this, "Đã gặp lỗi", Toast.LENGTH_SHORT).show();
                });
    }

    private void loginToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}