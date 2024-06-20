package tlu.cse.android.ht63.dogcareapp.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityAddPetBinding;
import tlu.cse.android.ht63.dogcareapp.model.Pet;

public class AddPetActivity extends AppCompatActivity {
    private ActivityAddPetBinding binding;
    private Pet pet = new Pet();
    private Calendar cal;
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

        cal = Calendar.getInstance();

        binding.tvGender.setOnClickListener(v -> showBottomSheetGenderChooser());

        binding.tvAge.setOnClickListener(v -> showDatePicker());

        binding.imgBack.setOnClickListener(v -> finish());
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