package tlu.cse.android.ht63.dogcareapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.FragmentStoriesBinding;
import tlu.cse.android.ht63.dogcareapp.ui.AddStoriesActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoriesFragment extends Fragment {
    private FragmentStoriesBinding binding;

    public static Fragment newInstance() {
        return new StoriesFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStoriesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(),AddStoriesActivity.class);
                startActivity(intent);
            }
        });


    }

}