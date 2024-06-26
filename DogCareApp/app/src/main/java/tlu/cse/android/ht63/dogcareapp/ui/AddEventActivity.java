package tlu.cse.android.ht63.dogcareapp.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Objects;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityAddEventBinding;
import tlu.cse.android.ht63.dogcareapp.model.Event;
import tlu.cse.android.ht63.dogcareapp.model.Pet;
import tlu.cse.android.ht63.dogcareapp.notification.AlarmService;
import tlu.cse.android.ht63.dogcareapp.utils.Pref;

public class AddEventActivity extends BaseActivity {
    private ActivityAddEventBinding binding;

    private Pet pet = new Pet();
    private Event event = new Event();
    private boolean isUpdate;

    private long timeInMillis = 0;
    private int rdActive = 0;
    private int rdHabit = 0;
    private String keyFilter = "";
    Calendar c = Calendar.getInstance();

    private AlarmService alarmService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        alarmService = new AlarmService(this);
        Gson gson = new Gson();
        pet = gson.fromJson(getIntent().getStringExtra("pet"), Pet.class);
        event = gson.fromJson(getIntent().getStringExtra("event"), Event.class);
        keyFilter = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        timeInMillis = c.getTimeInMillis();

        if (event == null) {
            if (pet == null) {
                return;
            }
            binding.tvPetName.setText(pet.getName());
            isUpdate = false;
            binding.tvDateTime.setText(Pref.convertDateTime(timeInMillis));

        } else {
            isUpdate = true;
            //old event
            initBase();
        }

        binding.tvDateTime.setOnClickListener(v -> showDateTimePickerDialog(binding.tvDateTime));
        binding.imgBack.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> {
            showLoading();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef;
            if (isUpdate) {
                event.setActive(rdActive);
                event.setHabit(rdHabit);
                event.setDateTime(timeInMillis);
                event.setNote(Objects.requireNonNull(binding.edtNote.getText()).toString());
                docRef = db.collection("events").document(event.getUid());

            } else {
                event = new Event();
                event.setActive(rdActive);
                event.setHabit(rdHabit);
                event.setDateTime(timeInMillis);
                event.setNote(Objects.requireNonNull(binding.edtNote.getText()).toString());
                event.setPetId(pet.getUid());
                event.setUserId(pet.getUserId());
                event.setKeyFilter(keyFilter);
                event.setTimeStamp(System.currentTimeMillis());
                event.setPetName(pet.getName());
                docRef = db.collection("events").document();
                event.setUid(docRef.getId());

            }

            String content = "";

            switch (event.getActive()) {
                case 1: {
                    content = "Đưa thú cưng đi tiêm";
                    break;
                }
                case 2: {
                    content = "Đưa thú cưng đi dạo";
                    break;
                }
                default: {
                    content = "Cho thú cưng ăn";
                    break;
                }
            }

            int notificationId = (event.getUid().hashCode() & 0x7FFFFFFF);

            switch (rdHabit) {
                case 1: {
                    alarmService.setRepetitiveAlarm(timeInMillis, notificationId, "Có hoạt động mới", content, 1);
                    break;
                }
                case 2: {
                    alarmService.setRepetitiveAlarm(timeInMillis, notificationId, "Có hoạt động mới", content, 7);
                    break;
                }
                case 3: {
                    alarmService.setRepetitiveAlarm(timeInMillis, notificationId, "Có hoạt động mới", content, 30);
                    break;
                }
                default: {
                    alarmService.setExactAlarm(timeInMillis, notificationId, "Có hoạt động mới", content);
                }
            }

            docRef.set(event, SetOptions.merge())
                    .addOnCompleteListener(task -> {
                        if (isUnavailable()) {
                            return;
                        }
                        hideLoading();
                        if (task.isSuccessful()) {
                            Intent intent = new Intent();
                            intent.putExtra("key", "event");
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            Toast.makeText(
                                    AddEventActivity.this,
                                    "Error: " + task.getException().toString(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });


        });

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rdActive0) {
                rdActive = 0;
            } else if (checkedId == R.id.rdActive1) {
                rdActive = 1;
            } else if (checkedId == R.id.rdActive2) {
                rdActive = 2;
            }
        });
        binding.radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rdHabit0) {
                rdHabit = 0;
            } else if (checkedId == R.id.rdHabit1) {
                rdHabit = 1;
            } else if (checkedId == R.id.rdHabit2) {
                rdHabit = 2;
            } else if (checkedId == R.id.rdHabit3) {
                rdHabit = 3;
            }
        });
    }

    private void initBase() {
        timeInMillis = event.getDateTime();
        binding.tvPetName.setText(event.getPetName());
        binding.tvDateTime.setText(Pref.convertDateTime(timeInMillis));
        binding.edtNote.setText(event.getNote());
        rdActive = event.getActive();
        rdHabit = event.getHabit();
        switch (event.getActive()) {
            case 1: {
                binding.rdActive1.setChecked(true);
                break;
            }
            case 2: {
                binding.rdActive2.setChecked(true);
                break;
            }
            default: {
                binding.rdActive0.setChecked(true);
                break;
            }
        }
        switch (event.getHabit()) {
            case 1: {
                binding.rdHabit1.setChecked(true);
                break;
            }
            case 2: {
                binding.rdHabit2.setChecked(true);
                break;
            }
            case 3: {
                binding.rdHabit3.setChecked(true);
                break;
            }
            default: {
                binding.rdHabit0.setChecked(true);
                break;
            }
        }
    }

    private void showDateTimePickerDialog(TextView v) {

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    final int selectedYear = year1;
                    final int selectedMonth = monthOfYear;
                    final int selectedDay = dayOfMonth;

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR,selectedYear);
                    calendar.set(Calendar.MONTH,selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH,selectedDay);

                    if (calendar.getTimeInMillis() < System.currentTimeMillis()){
                        Toast.makeText(this, "Không được chọn thời gian bé hơn hiện tại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this,
                            (view1, hourOfDay, minute1) -> {
                                Calendar selectedDateTime = Calendar.getInstance();
                                selectedDateTime.set(selectedYear, selectedMonth, selectedDay, hourOfDay, minute1);


                                if (selectedDateTime.getTimeInMillis() < System.currentTimeMillis()){
                                    Toast.makeText(this, "Không được chọn thời gian bé hơn hiện tại", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                timeInMillis = selectedDateTime.getTimeInMillis();
                                v.setText(Pref.convertDateTime(timeInMillis));
                                keyFilter = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;

                            }, hour, minute, true);
                    timePickerDialog.show();
                }, year, month, day);
        datePickerDialog.show();
    }
}