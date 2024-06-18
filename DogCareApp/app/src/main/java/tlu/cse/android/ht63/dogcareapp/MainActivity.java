package tlu.cse.android.ht63.dogcareapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import tlu.cse.android.ht63.dogcareapp.adapter.PagerAdapter;
import tlu.cse.android.ht63.dogcareapp.adapter.TabAdapter;
import tlu.cse.android.ht63.dogcareapp.databinding.ActivityMainBinding;
import tlu.cse.android.ht63.dogcareapp.model.TabItem;
import tlu.cse.android.ht63.dogcareapp.ui.LoginActivity;
import tlu.cse.android.ht63.dogcareapp.utils.TabOnListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityMainBinding binding;
    private final Handler mHandle = new Handler(Looper.getMainLooper());

    private TabAdapter adapterRv;
    private PagerAdapter pagerAdapter;

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
        }else {
            UserInfoManager.getInstance().loadInfoFormFirebase(new UserInfoManager.OnLoadInfoListener() {
                @Override
                public void onSuccess() {
                    mHandle.postDelayed(() -> hideSplash(), 1500);
                    initViewPager();
                    initBottomBar();


                }
                @Override
                public void onFailure(Exception e) {
                    Log.d("__haha", "onFailure: onFailure");
                }

                @Override
                public void onAuthNull() {
                    Log.d("__haha", "onAuthNull: onAuthNull__");
                }
            });

        }
    }

    private void initViewPager() {
        pagerAdapter = new PagerAdapter(this);
        binding.viewPager.setOffscreenPageLimit(4);
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setSaveEnabled(false);
        binding.viewPager.setAdapter(pagerAdapter);
    }


    private void hideSplash() {
        binding.splashLayout.setVisibility(View.GONE);
        binding.containerLayout.setVisibility(View.VISIBLE);
    }

    private void initBottomBar() {
        binding.recyclerview.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        adapterRv = new TabAdapter();
        binding.recyclerview.setAdapter(adapterRv);
        binding.recyclerview.setHasFixedSize(true);
        adapterRv.addItem(list());
        adapterRv.updateView(0);

        adapterRv.setTabOnListener(new TabOnListener() {
            @Override
            public void onItemClick(int position, TabItem tabItem) {
                switch (position) {
                    case 0:
                        binding.viewPager.setCurrentItem(0, false);
                        break;
                    case 1:
                        binding.viewPager.setCurrentItem(1, false);
                        break;
                    case 2:
                        binding.viewPager.setCurrentItem(2, false);
                        break;
                    case 3:
                        binding.viewPager.setCurrentItem(3, false);
                        break;
                }
                adapterRv.updateView(position);
            }
        });
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