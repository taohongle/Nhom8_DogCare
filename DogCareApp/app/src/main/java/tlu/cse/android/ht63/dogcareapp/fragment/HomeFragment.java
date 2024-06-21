package tlu.cse.android.ht63.dogcareapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.adapter.BannerAdapter;
import tlu.cse.android.ht63.dogcareapp.databinding.FragmentHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ...
        initBanner();
    }

    private void initBanner() {
        bannerAdapter = new BannerAdapter(requireActivity(), images);
        binding.viewPager.setAdapter(bannerAdapter);

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == images.length) {
                    currentPage = 0;
                }
                binding.viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000);
            }
        };

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
            }
        });

        handler.postDelayed(runnable, 3000);
    }
    private BannerAdapter bannerAdapter;
    private Handler handler;
    private Runnable runnable;
    private int[] images = {R.drawable.pet1, R.drawable.pet2, R.drawable.pet3};
    private int currentPage = 0;
    private ActivityResultLauncher<Intent> startForResult;

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();

    }
}