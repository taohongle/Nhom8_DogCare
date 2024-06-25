package tlu.cse.android.ht63.dogcareapp.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityAddStoriesBinding;
import tlu.cse.android.ht63.dogcareapp.model.Stories;

public class AddStoriesActivity extends BaseActivity {
    private ActivityAddStoriesBinding binding;
    private Calendar cal;

    private Stories stories = new Stories();
    private Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        stories = gson.fromJson(getIntent().getStringExtra("stories"), Stories.class);
        if (stories != null) {
            Glide.with(this)
                    .load(stories.getImage())
                    .error(R.drawable.ic_launcher_foreground)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(binding.image);

            binding.edtTitle.setText(stories.getTitle());
        }


        binding.image.setOnClickListener(v -> checkAndRequestPermissions());

        binding.btnSave.setOnClickListener(v -> checkData());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void checkData() {
        String title = Objects.requireNonNull(binding.edtTitle.getText()).toString().trim();

        if (stories == null) {
            if (title.isEmpty() || path.isEmpty()) {
                Toast.makeText(this, "Vui lòng hãy điền đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                uploadData(title);
            }
        } else {
            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng hãy điền đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                uploadData(title);
            }
        }
    }

    private void uploadData(String title) {
        showLoading();

        if (!path.isEmpty()) {
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

                                    if (task.isSuccessful()) {
                                        Uri uri = task.getResult();
                                        pushToFirebase(title, uri.toString(), uid);
                                    } else {
                                        hideLoading();
                                        Toast.makeText(AddStoriesActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
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
        } else {
            pushToFirebase(title, stories.getImage(), stories.getUserId());
        }
    }

    private void pushToFirebase(String title, String image, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef;
        if (stories == null) {
            stories = new Stories();
            stories.setTimeStamp(System.currentTimeMillis());
            stories.setTime(System.currentTimeMillis());
            docRef = db.collection("stories").document();
        } else {
            docRef = db.collection("stories").document(stories.getUid());
        }
        stories.setTitle(title);
        stories.setImage(image);
        stories.setUid(docRef.getId());
        stories.setUserId(userId);


        docRef.set(stories, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (isUnavailable()) {
                        return;
                    }
                    hideLoading();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent();
                        intent.putExtra("key", "stories");
                        intent.putExtra("stories", gson.toJson(stories));
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        Toast.makeText(
                                AddStoriesActivity.this,
                                "Error: " + task.getException().toString(),
                                Toast.LENGTH_SHORT
                        ).show();
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

    private String path = "";
    private ActivityResultLauncher<Intent> croppedStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        path = intent.getStringExtra("path");

                        Glide.with(this)
                                .load(path)
                                .centerCrop()
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(binding.image);
                    }
                }
            }
    );

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
}