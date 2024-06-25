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
import tlu.cse.android.ht63.dogcareapp.adapter.StoriesAdapter;
import tlu.cse.android.ht63.dogcareapp.databinding.FragmentStoriesBinding;
import tlu.cse.android.ht63.dogcareapp.model.Stories;
import tlu.cse.android.ht63.dogcareapp.model.UserInfo;
import tlu.cse.android.ht63.dogcareapp.ui.AddStoriesActivity;
import tlu.cse.android.ht63.dogcareapp.utils.PaginationRecyclerview;
import tlu.cse.android.ht63.dogcareapp.utils.StoriesOnListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoriesFragment extends Fragment {
    private ActivityResultLauncher<Intent> startForResult;
    private FragmentStoriesBinding binding;
    private boolean isLastItemReached;
    private DocumentSnapshot lastVisible;

    private UserInfo userInfo;
    private FirebaseFirestore db;
    private boolean isLoading;

    private Gson gson = new Gson();

    public static StoriesFragment newInstance() {

        return new StoriesFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStoriesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userInfo = UserInfoManager.getInstance().getUserInfo();
        db = FirebaseFirestore.getInstance();
        initListStories();
        loadStories(false);


        binding.btnCreate.setOnClickListener(v -> startForResult.launch(new Intent(requireActivity(), AddStoriesActivity.class)));

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
                        if (key.equals("stories")) {
                            loadStories(false);
                        }

                    }
                }
        );
    }

    private StoriesAdapter storiesAdapter = new StoriesAdapter();

    private void initListStories() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);

        binding.recyclerviewStories.setHasFixedSize(true);
        binding.recyclerviewStories.setLayoutManager(layoutManager);
        binding.recyclerviewStories.setAdapter(storiesAdapter);
        binding.recyclerviewStories.addOnScrollListener(new PaginationRecyclerview() {
            @Override
            public int listSize() {
                return storiesAdapter.getItemCount();
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
                loadStories(true);
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastItemReached;
            }
        });

        storiesAdapter.setStoriesOnListener(new StoriesOnListener() {
            @Override
            public void onItemClick(int p, Stories stories) {
                Intent intent = new Intent(requireActivity(), AddStoriesActivity.class);
                intent.putExtra("stories", gson.toJson(stories));
                startForResult.launch(intent);
            }

            @Override
            public void onLongClick(int p, Stories stories) {
                showBottomSheetMenu(stories);
            }
        });
    }

    private void showBottomSheetMenu(Stories stories) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
        dialog.setContentView(R.layout.layout_stories_listener);
        dialog.setCancelable(true);
        dialog.show();

        View btnEdit = dialog.findViewById(R.id.tvEdit);
        View btnDelete = dialog.findViewById(R.id.tvDelete);
        View btnCancel = dialog.findViewById(R.id.cancel);

        Objects.requireNonNull(btnEdit).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(requireActivity(), AddStoriesActivity.class);
            intent.putExtra("stories", gson.toJson(stories));
            startForResult.launch(intent);

        });

        Objects.requireNonNull(btnDelete).setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteConfirmationDialog(stories);
        });

        Objects.requireNonNull(btnCancel).setOnClickListener(v -> dialog.dismiss());
    }

    private void loadStories(boolean isLoadMore) {

        Query first;
        first = db.collection("stories")
                .whereEqualTo("userId", userInfo.getUid())
                .orderBy("timeStamp", Query.Direction.ASCENDING);

        if (isLoadMore && lastVisible != null) {
            first = first.startAfter(lastVisible);
        }
        first = first.limit(10);
        isLoading = true;
        first.get().addOnCompleteListener(task -> {
            isLoading = false;
            if (getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed()) {
                return;
            }
            if (task.isSuccessful()) {

                List<Stories> list = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    Stories model = documentSnapshot.toObject(Stories.class);
                    if (model == null) {
                        continue;
                    }
                    list.add(model);
                }
                if (!isLoadMore) {
                    storiesAdapter.setData(list);

                } else {
                    storiesAdapter.addData(list);
                }
                int size = task.getResult().size();
                if (size > 0) {
                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                }
                if (size < 10) {
                    isLastItemReached = true;
                }

            } else {
                Log.d("__index", "loadPets: " + task.getException().getMessage());
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void showDeleteConfirmationDialog(Stories stories) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa mục này không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            db.collection("stories")
                    .document(stories.getUid())
                    .delete();
            Toast.makeText(requireActivity(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();

            lastVisible = null;
            loadStories(false);

        });


        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());


        AlertDialog dialog = builder.create();
        dialog.show();
    }
}