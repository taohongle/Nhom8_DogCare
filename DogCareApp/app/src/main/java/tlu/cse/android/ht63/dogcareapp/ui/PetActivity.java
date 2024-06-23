package tlu.cse.android.ht63.dogcareapp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityPetBinding;
import tlu.cse.android.ht63.dogcareapp.model.Pet;
import tlu.cse.android.ht63.dogcareapp.utils.Pref;

public class PetActivity extends AppCompatActivity {
    private ActivityPetBinding binding;
    private ActivityResultLauncher<Intent> startForResult;
    private Pet pet = new Pet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        Gson gson = new Gson();
        pet = gson.fromJson(getIntent().getStringExtra("pet"), Pet.class);

        if (pet == null) {
            return;
        }


        loadUI();

        binding.imvEdit.setOnClickListener(v -> {
            Intent intent = new Intent(PetActivity.this, AddPetActivity.class);
            intent.putExtra("pet", gson.toJson(pet));
            startForResult.launch(intent);
        });

        binding.imgBack.setOnClickListener(v -> finish());

        startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();

                        if (intent == null) {
                            return;
                        }
                        pet = gson.fromJson(intent.getStringExtra("pet"), Pet.class);
                        it.putExtra("key", "pet");
                        setResult(Activity.RESULT_OK, it);
                        loadUI();

                    }
                }
        );
    }
    Intent it = new Intent();

    @SuppressLint("SetTextI18n")
    private void loadUI() {
        binding.tvPetName.setText("Tên thú cưng: " + pet.getName());
        binding.tvPetAge.setText("Ngày sinh: " + Pref.convertDate(pet.getAge()));
        binding.tvPetGender.setText("Giới tính: " + pet.getGender());
        binding.tvPetKg.setText("Cân nặng: " + pet.getKg() + " Kg");
        binding.tvPetType.setText("Giống: " + pet.getType());
        Glide.with(this)
                .load(pet.getImage())
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(binding.imvPet);
    }
}