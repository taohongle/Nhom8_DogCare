package tlu.cse.android.ht63.dogcareapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityCropImageBinding;

public class CropImageActivity extends AppCompatActivity {
    private int maxSize = 600;
    private int maxQuality = 75;

    private Uri photoURI;

    private ActivityCropImageBinding binding;

    private final ActivityResultLauncher<Intent> takePhotoStartForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                loadToCropView(photoURI);
                            } else {
                                finish();
                            }
                        }
                    });

    private final ActivityResultLauncher<Intent> galleryStartForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null
                                && result.getData().getData() != null) {
                            loadToCropView(result.getData().getData());
                        } else {
                            finish();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityCropImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        maxSize = getIntent().getIntExtra("maxSize", 600);
        maxQuality = getIntent().getIntExtra("maxQuality", 75);
        cropSettings();
        String photoFrom = getIntent().getStringExtra("photoFrom");
        if ("camera".equals(photoFrom)) {
            takePhoto();
        } else {
            openGallery();
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> crop());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void takePhoto() {
        File photoFile = createImageFile();
        Log.d("__test", "takePhoto: " + photoFile.getPath());
        photoURI = FileProvider.getUriForFile(this, "tlu.cse.android.ht63.DogCareApp.fileprovider",
                photoFile);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        takePhotoStartForResult.launch(takePictureIntent);
    }

    private void openGallery() {
        try {
            galleryStartForResult.launch(new Intent().setAction(Intent.ACTION_GET_CONTENT)
                    .setType("image/*"));
        } catch (Exception exp) {
            Toast.makeText(this, exp.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void cropSettings() {
        binding.cropImageView.setCropMode(CropImageView.CropMode.FREE);
        binding.cropImageView.setMinFrameSizeInPx(200);
        binding.cropImageView.setOutputMaxSize(maxSize, maxSize);
        binding.cropImageView.setCompressQuality(100);
        binding.cropImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);
        binding.cropImageView.setGuideShowMode(CropImageView.ShowMode.SHOW_ALWAYS);
        binding.cropImageView.setAnimationEnabled(true);
    }

    private void crop() {
        binding.cropImageView.cropAsync(new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                try {
                    //  File f = new File(getCacheDir(), "cropped.jpg");
                    File f = createImageFile();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    cropped.compress(Bitmap.CompressFormat.JPEG, maxQuality, bos);
                    byte[] bitmapData = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapData);
                    fos.flush();
                    fos.close();
                    Log.d("__cropped", "file size :" + f.length() / 1024);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("type", "photo");
                    returnIntent.putExtra("path", f.getPath());
                    returnIntent.putExtra("w", cropped.getWidth());
                    returnIntent.putExtra("h", cropped.getHeight());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                    finishFailed();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                e.printStackTrace();
                finishFailed();
            }
        });
    }

    private void finishFailed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private File createImageFile() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }
    private void loadToCropView(Uri uri) {
        binding.cropImageView.load(uri).execute(null);
    }
}