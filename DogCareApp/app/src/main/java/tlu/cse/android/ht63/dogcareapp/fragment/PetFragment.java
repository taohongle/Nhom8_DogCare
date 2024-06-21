package tlu.cse.android.ht63.dogcareapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.UserInfoManager;
import tlu.cse.android.ht63.dogcareapp.adapter.Pet2Adapter;
import tlu.cse.android.ht63.dogcareapp.databinding.FragmentPetBinding;
import tlu.cse.android.ht63.dogcareapp.model.Pet;
import tlu.cse.android.ht63.dogcareapp.model.UserInfo;
import tlu.cse.android.ht63.dogcareapp.ui.AddPetActivity;
import tlu.cse.android.ht63.dogcareapp.utils.PaginationRecyclerview;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PetFragment extends Fragment {
    private FragmentPetBinding binding;
    private boolean isLastItemReachedPet;
    private DocumentSnapshot lastVisiblePet;
    Gson gson = new Gson();
    private UserInfo userInfo;
    private FirebaseFirestore db;
    private boolean isLoadingPet;
    Pet2Adapter petAdapter = new Pet2Adapter();

    private ActivityResultLauncher<Intent> startForResult;

    public static PetFragment newInstance() {

        return new PetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPetBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lastVisiblePet = null;
        isLastItemReachedPet = false;
        isLoadingPet = false;
        db = FirebaseFirestore.getInstance();
        userInfo = UserInfoManager.getInstance().getUserInfo();

        initListPet();
        loadPets(false);

        startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();

                        if (intent == null) {
                            return;
                        }
                        String key = intent.getStringExtra("key");
                        if (key == null) {
                            return;
                        }
                    }
                }
        );

        binding.btnCreate.setOnClickListener(v -> startForResult.launch(new Intent(requireActivity(), AddPetActivity.class)));

    }

    private void loadPets(boolean isLoadMore) {

        Query first;
        first = db.collection("pets")
                .whereEqualTo("userId", userInfo.getUid())
                .orderBy("timeStamp", Query.Direction.ASCENDING);

        if (isLoadMore && lastVisiblePet != null) {
            first = first.startAfter(lastVisiblePet);
        }
        first = first.limit(10);
        isLoadingPet = true;
        first.get().addOnCompleteListener(task -> {
            isLoadingPet = false;
            if (getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed()) {
                return;
            }
            if (task.isSuccessful()) {
                List<Pet> list = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    Pet model = documentSnapshot.toObject(Pet.class);
                    if (model == null) {
                        continue;
                    }
                    list.add(model);
                }
                if (!isLoadMore) {
                    petAdapter.setData(list);

                } else {
                    petAdapter.addData(list);
                }
                int size = task.getResult().size();
                if (size > 0) {
                    lastVisiblePet = task.getResult().getDocuments().get(task.getResult().size() - 1);
                }
                if (size < 10) {
                    isLastItemReachedPet = true;
                }

            } else {
                Log.d("__index", "loadPets: " + task.getException().getMessage());
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
    private void initListPet() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);

        binding.recyclerviewPet.setHasFixedSize(true);
        binding.recyclerviewPet.setLayoutManager(layoutManager);
        binding.recyclerviewPet.setAdapter(petAdapter);
        binding.recyclerviewPet.addOnScrollListener(new PaginationRecyclerview() {
            @Override
            public int listSize() {
                return petAdapter.getItemCount();
            }
            @Override
            public int lastItemPosition() {
                return layoutManager.findLastVisibleItemPosition();
            }
            @Override
            public void loadMoreItem() {
                if (getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed()) {
                    return;
                }
                loadPets(true);
            }
            @Override
            public boolean isLoading() {
                return isLoadingPet;
            }
            @Override
            public boolean isLastPage() {
                return isLastItemReachedPet;
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        loadPets(false);
    }



}