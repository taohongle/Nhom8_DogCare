package tlu.cse.android.ht63.dogcareapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import tlu.cse.android.ht63.dogcareapp.adapter.TabAdapter;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityMainBinding;
import tlu.cse.android.ht63.dogcareapp.model.TabItem;
import tlu.cse.android.ht63.dogcareapp.ui.LoginActivity;
import tlu.cse.android.ht63.dogcareapp.utils.TabOnListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
    private void hideSplash() {
        Log.d("__haha", "hideSplash: haha");
        binding.splashLayout.setVisibility(View.GONE);
        binding.containerLayout.setVisibility(View.VISIBLE);
    }



    private List<TabItem> list(){
        List<TabItem> list = new ArrayList<>();
        list.add(new TabItem(getString(R.string.home), R.drawable.home, 0));
        list.add(new TabItem(getString(R.string.pet), R.drawable.pets, 0));
        list.add(new TabItem(getString(R.string.stories), R.drawable.camera, 0));
        list.add(new TabItem(getString(R.string.user), R.drawable.user, 0));
        return list;
    }

}