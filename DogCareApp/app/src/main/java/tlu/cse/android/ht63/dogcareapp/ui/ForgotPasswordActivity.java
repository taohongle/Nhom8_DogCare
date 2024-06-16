package tlu.cse.android.ht63.dogcareapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        binding.btResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
                if (!email.isEmpty()) {
                    sendPasswordResetEmail(email);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.imgBack.setOnClickListener(v -> finish());
    }
    private void sendPasswordResetEmail(String email) {
        showLoading();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (isUnavailable()) {
                        return;
                    }
                    hideLoading();
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Đã gửi email đặt lại mật khẩu.", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                        handleError(errorCode);
                    }
                });
    }

    private void handleError(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                Toast.makeText(ForgotPasswordActivity.this, "Địa chỉ email bị định dạng sai.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(ForgotPasswordActivity.this, "Không có hồ sơ người dùng tương ứng với email này.", Toast.LENGTH_LONG).show();
                break;

            default:
                Toast.makeText(ForgotPasswordActivity.this, "Không gửi được email đặt lại.", Toast.LENGTH_LONG).show();
                break;
        }
    }

}