package tlu.cse.android.ht63.dogcareapp.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.UserInfoManager;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityAddStoriesBinding;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityProfileBinding;
import tlu.cse.android.ht63.dogcareapp.model.UserInfo;
import tlu.cse.android.ht63.dogcareapp.utils.Pref;

public class ProfileActivity extends BaseActivity {
    private ActivityProfileBinding binding;

    private Calendar cal = Calendar.getInstance();
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userInfo = UserInfoManager.getInstance().getUserInfo();

        cal.setTimeInMillis(userInfo.getAge());
        Glide.with(this)
                .load(userInfo.getImage())
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(binding.image);
        binding.edtName.setText(userInfo.getName());
        binding.edtEmail.setText(userInfo.getEmail());
        binding.tvAge.setText(Pref.convertDate(userInfo.getAge()));
        binding.tvGender.setText(userInfo.getGender());
        binding.edtAddress.setText(userInfo.getAddress());

        binding.btnSave.setOnClickListener(v -> saveData());

        binding.image.setOnClickListener(v -> checkAndRequestPermissions());

        binding.tvAge.setOnClickListener(v -> showDatePicker());
        binding.tvGender.setOnClickListener(v -> showBottomSheetGenderChooser());

        binding.imgBack.setOnClickListener(v -> finish());
        binding.edtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Không được chỉnh sửa email", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showBottomSheetGenderChooser() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.layout_bottom_gender);
        dialog.setCancelable(true);
        dialog.show();

        TextView btnMale = dialog.findViewById(R.id.tvMale);
        TextView btnFemale = dialog.findViewById(R.id.tvFemale);
        View btnCancel = dialog.findViewById(R.id.cancel);

        Objects.requireNonNull(btnMale).setText("Nam");
        Objects.requireNonNull(btnFemale).setText("Nữ");

        Objects.requireNonNull(btnMale).setOnClickListener(v -> {
            binding.tvGender.setText("Nam");
            dialog.dismiss();
        });

        Objects.requireNonNull(btnFemale).setOnClickListener(v -> {
            binding.tvGender.setText("Nữ");
            dialog.dismiss();
        });

        Objects.requireNonNull(btnCancel).setOnClickListener(v -> dialog.dismiss());
    }


    private void showDatePicker() {
        final DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.tvAge.setText(Pref.convertDate(cal.getTimeInMillis()));
            userInfo.setAge(cal.getTimeInMillis());

        };

        binding.tvAge.setOnClickListener(v -> new DatePickerDialog(
                ProfileActivity.this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)


        ).show());
    }


    private void saveData() {
        userInfo.setName(Objects.requireNonNull(binding.edtName.getText()).toString().trim());
        userInfo.setAddress(Objects.requireNonNull(binding.edtAddress.getText()).toString().trim());
        userInfo.setGender(Objects.requireNonNull(binding.tvGender.getText()).toString().trim());
        showLoading();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userInfo.getUid());
        docRef.set(userInfo, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (isUnavailable()) {
                        return;
                    }
                    hideLoading();
                    if (task.isSuccessful()) {
                        UserInfoManager.getInstance().setUserInfo(userInfo);
                        Toast.makeText(this, "Cập nhật thông tin cá nhân thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }

                });
    }
    private void checkAndRequestPermissions() {
        String[] permissions = getPermissions();

        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            requestPermissionsLauncher.launch(permissionsArray);
        } else {

            showBottomSheetImageChooser();
        }
    }

    @NonNull
    private static String[] getPermissions() {
        String[] newSDKPermissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_MEDIA_IMAGES};
        String[] oldSDKPermissions = {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        String[] permissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = newSDKPermissions;
        } else {
            permissions = oldSDKPermissions;
        }
        return permissions;
    }

    private ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allPermissionsGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (allPermissionsGranted) {
                    showBottomSheetImageChooser();
                } else {
                    Toast.makeText(this, "Quyền truy cập camera và bộ nhớ bị từ chối!", Toast.LENGTH_SHORT).show();
                }
            });

    private void showBottomSheetImageChooser() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.layout_bottom_photo);
        dialog.setCancelable(true);
        dialog.show();

        View btnCamera = dialog.findViewById(R.id.tv_camera);
        View btnGallery = dialog.findViewById(R.id.tv_gallery);
        View btnCancel = dialog.findViewById(R.id.cancel);

        Objects.requireNonNull(btnCamera).setOnClickListener(v -> {
            getPhoto("camera");
            dialog.dismiss();
        });

        Objects.requireNonNull(btnGallery).setOnClickListener(v -> {
            getPhoto("gallery");
            dialog.dismiss();
        });

        Objects.requireNonNull(btnCancel).setOnClickListener(v -> dialog.dismiss());
    }

    private void getPhoto(String from) {
        try {
            Intent intent = new Intent(this, CropImageActivity.class);
            intent.putExtra("maxSize", 600);
            intent.putExtra("maxQuality", 100);
            intent.putExtra("photoFrom", from);
            croppedStartForResult.launch(intent);
        } catch (Exception exp) {
            Toast.makeText(this, exp.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private ActivityResultLauncher<Intent> croppedStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        String path = intent.getStringExtra("path");

                        Glide.with(this)
                                .load(path)
                                .centerCrop()
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .circleCrop()
                                .into(binding.image);
                        uploadAvatar(path);

                    }
                }
            }
    );
    private void uploadAvatar(String path) {
        showLoading();
        File file = new File(path);
        if (file.exists()) {
            try {
                InputStream stream = new FileInputStream(file);
                String uid = FirebaseAuth.getInstance().getUid();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(uid + "/" + file.getName());
                storageRef.putStream(stream)
                        .addOnSuccessListener(taskSnapshot -> {
                            if (isUnavailable()) {
                                return;
                            }
                            storageRef.getDownloadUrl().addOnCompleteListener(task -> {
                                if (isUnavailable()) {
                                    return;
                                }
                                hideLoading();
                                if (task.isSuccessful()) {
                                    Uri uri = task.getResult();
                                    userInfo.setImage(uri.toString());
                                } else {

                                    Toast.makeText(ProfileActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            if (isUnavailable()) {
                                return;
                            }
                            hideLoading();
                        });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                hideLoading();
            }
        }
    }
}