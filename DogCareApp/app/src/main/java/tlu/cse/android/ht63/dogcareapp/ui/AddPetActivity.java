package tlu.cse.android.ht63.dogcareapp.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
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
import com.bumptech.glide.request.RequestOptions;
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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityAddPetBinding;
import tlu.cse.android.ht63.dogcareapp.model.Pet;
import tlu.cse.android.ht63.dogcareapp.utils.Pref;

public class AddPetActivity extends BaseActivity {
    private ActivityAddPetBinding binding;
    private Pet pet = new Pet();
    private Calendar cal;

    Gson gson;
    private DateFormat dateFormat = DateFormat.getDateInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        gson = new Gson();

        cal = Calendar.getInstance();

        pet = gson.fromJson(getIntent().getStringExtra("pet"), Pet.class);

        if (pet != null) {
            cal.setTimeInMillis(pet.getAge());
            binding.edtName.setText(pet.getName());
            binding.tvGender.setText(pet.getGender());
            binding.edtType.setText(pet.getType());
            binding.tvAge.setText(Pref.convertDate(pet.getAge()));
            binding.edtKg.setText(String.valueOf(pet.getKg()));
            Glide.with(AddPetActivity.this)
                    .load(pet.getImage())
                    .error(R.drawable.ic_launcher_foreground)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .circleCrop()
                    .into(binding.image);
        }
        binding.tvAge.setText(dateFormat.format(cal.getTime()));
        binding.layoutOfImage.setOnClickListener(v -> checkAndRequestPermissions());

        binding.imgBack.setOnClickListener(v -> finish());

        binding.tvGender.setOnClickListener(v -> showBottomSheetGenderChooser());

        binding.btnSave.setOnClickListener(v->checkData());

        binding.tvAge.setOnClickListener(v -> showDatePicker());
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
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .circleCrop()
                                .apply(RequestOptions.circleCropTransform())
                                .into(binding.image);
                    }
                }
            }
    );

    private void checkData() {
        String name = Objects.requireNonNull(binding.edtName.getText()).toString().trim();
        String gender = Objects.requireNonNull(binding.tvGender.getText()).toString().trim();
        String type = Objects.requireNonNull(binding.edtType.getText()).toString().trim();
        String age = String.valueOf(cal.getTimeInMillis());
        String kg = Objects.requireNonNull(binding.edtKg.getText()).toString().trim();
        if (pet == null) {
            if (name.isEmpty() || gender.isEmpty() || type.isEmpty() || age.isEmpty() || kg.isEmpty() || path.isEmpty()) {
                Toast.makeText(this, "Vui lòng hãy điền đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                uploadData(name, gender, type, age, kg);
            }
        } else {
            if (name.isEmpty() || gender.isEmpty() || type.isEmpty() || age.isEmpty() || kg.isEmpty()) {
                Toast.makeText(this, "Vui lòng hãy điền đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                uploadData(name, gender, type, age, kg);
            }
        }
    }

    private void uploadData(String name, String gender, String type, String age, String kg) {
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
                                        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                        pushToFirebase(name, gender, type, age, kg, uri.toString(), uid);
                                    } else {
                                        hideLoading();
                                        Toast.makeText(AddPetActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
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
            pushToFirebase(name, gender, type, age, kg, pet.getImage(), pet.getUserId());
        }
    }

    private void pushToFirebase(String name, String gender, String type, String age, String kg, String url, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef;
        if (pet == null) {
            pet = new Pet();
            pet.setTimeStamp(System.currentTimeMillis());
            docRef = db.collection("pets").document();
        } else {
            docRef = db.collection("pets").document(pet.getUid());
        }
        pet.setName(name);
        pet.setGender(gender);
        pet.setType(type);
        pet.setAge(Long.parseLong(age));
        pet.setKg(Integer.parseInt(kg));
        pet.setImage(url);
        pet.setUid(docRef.getId());
        pet.setUserId(userId);

        docRef.set(pet, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (isUnavailable()) {
                        return;
                    }
                    hideLoading();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent();
                        intent.putExtra("key", "pet");
                        intent.putExtra("pet", gson.toJson(pet));
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        Toast.makeText(
                                AddPetActivity.this,
                                "Error: " + task.getException().toString(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showDatePicker() {
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                binding.tvAge.setText(dateFormat.format(cal.getTime()));
            }
        };

        binding.tvAge.setOnClickListener(v -> new DatePickerDialog(
                AddPetActivity.this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show());
    }

    private void showBottomSheetGenderChooser() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.layout_bottom_gender);
        dialog.setCancelable(true);
        dialog.show();

        View btnMale = dialog.findViewById(R.id.tvMale);
        View btnFemale = dialog.findViewById(R.id.tvFemale);
        View btnCancel = dialog.findViewById(R.id.cancel);

        Objects.requireNonNull(btnMale).setOnClickListener(v -> {
            binding.tvGender.setText("Con đực");
            dialog.dismiss();
        });

        Objects.requireNonNull(btnFemale).setOnClickListener(v -> {
            binding.tvGender.setText("Con cái");
            dialog.dismiss();
        });
        Objects.requireNonNull(btnCancel).setOnClickListener(v -> dialog.dismiss());
    }
}