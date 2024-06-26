package tlu.cse.android.ht63.dogcareapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
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
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.UserInfoManager;
import tlu.cse.android.ht63.dogcareapp.adapter.BannerAdapter;
import tlu.cse.android.ht63.dogcareapp.adapter.EventAdapter;
import tlu.cse.android.ht63.dogcareapp.adapter.PetAdapter;
import tlu.cse.android.ht63.dogcareapp.databinding.FragmentHomeBinding;
import tlu.cse.android.ht63.dogcareapp.model.Event;
import tlu.cse.android.ht63.dogcareapp.model.Pet;
import tlu.cse.android.ht63.dogcareapp.model.UserInfo;
import tlu.cse.android.ht63.dogcareapp.ui.AddEventActivity;
import tlu.cse.android.ht63.dogcareapp.ui.AddPetActivity;
import tlu.cse.android.ht63.dogcareapp.ui.PetActivity;
import tlu.cse.android.ht63.dogcareapp.utils.EventListener;
import tlu.cse.android.ht63.dogcareapp.utils.PaginationRecyclerview;
import tlu.cse.android.ht63.dogcareapp.utils.PetListener;
import tlu.cse.android.ht63.dogcareapp.utils.Pref;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    Gson gson = new Gson();
    private UserInfo userInfo;
    private Calendar calendar = Calendar.getInstance();
    private ActivityResultLauncher<Intent> startForResult;

    private DocumentSnapshot lastVisiblePet;

    private boolean isLastItemReachedPet;

    private boolean isLoadingPet;
    private DocumentSnapshot lastVisibleEvent;
    private boolean isLoadingEvent;
    private boolean isLastItemReachedEvent;
    PetAdapter petAdapter = new PetAdapter();
    EventAdapter eventAdapter = new EventAdapter();

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

        lastVisiblePet = null;
        isLastItemReachedPet = false;
        isLoadingPet = false;
        db = FirebaseFirestore.getInstance();
        userInfo = UserInfoManager.getInstance().getUserInfo();
        initBanner();


        binding.addPet.setOnClickListener(v -> startForResult.launch(new Intent(requireActivity(), AddPetActivity.class)));
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
        loadUI();
        initListPet();
        loadPets(false);
        initListEvent();
        loadEvents(false, calendar);
        initDialogPicker();
    }

    private void initDialogPicker() {
        final DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.tvFilter.setText(Pref.convertDate(calendar.getTimeInMillis()));

            loadEvents(false, calendar);

        };

        binding.layoutOfFilter.setOnClickListener(v -> new DatePickerDialog(
                requireActivity(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)


        ).show());
    }

    private void loadUI() {
        binding.name.setText(userInfo.getName());
        Glide.with(this)
                .load(userInfo.getImage())
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(binding.avatar);

        binding.tvFilter.setText(Pref.convertDate(calendar.getTimeInMillis()));
    }

    private void initListPet() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false);

        binding.recyclerviewPets.setHasFixedSize(true);
        binding.recyclerviewPets.setLayoutManager(layoutManager);
        binding.recyclerviewPets.setAdapter(petAdapter);
        binding.recyclerviewPets.addOnScrollListener(new PaginationRecyclerview() {
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

        petAdapter.setPetListener(new PetListener() {
            @Override
            public void onClick(int p, Pet pet) {
                Intent intent = new Intent(requireActivity(), PetActivity.class);
                intent.putExtra("pet", gson.toJson(pet));
                startForResult.launch(intent);
            }
            @Override
            public void onLongClick(int p, Pet pet) {
                showBottomSheetMenu(pet);
            }
        });
    }
    private void showBottomSheetMenu(Pet pet) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
        dialog.setContentView(R.layout.layout_pet_listener);
        dialog.setCancelable(true);
        dialog.show();

        View btnPet = dialog.findViewById(R.id.tvPet);
        View btnEvent = dialog.findViewById(R.id.tvEvent);
        View btnCancel = dialog.findViewById(R.id.cancel);
        View btnDelete = dialog.findViewById(R.id.tvDelete);
        Objects.requireNonNull(btnPet).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(requireActivity(), AddPetActivity.class);
            intent.putExtra("pet", gson.toJson(pet));
            startForResult.launch(intent);
        });

        Objects.requireNonNull(btnEvent).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(requireActivity(), AddEventActivity.class);
            intent.putExtra("pet", gson.toJson(pet));
            startForResult.launch(intent);
        });

        Objects.requireNonNull(btnDelete).setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteConfirmationDialog(pet);
        });
        Objects.requireNonNull(btnCancel).setOnClickListener(v -> dialog.dismiss());
    }

    private void showDeleteConfirmationDialog(Pet pet) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa mục này không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            db.collection("pets")
                    .document(pet.getUid())
                    .delete();
            Toast.makeText(requireActivity(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();

            lastVisiblePet = null;
            loadPets(false);
        });
        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadPets(boolean isLoadMore) {

        Log.d("__load", "is load more: " + isLoadMore);
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

    private void initListEvent() {

        LinearLayoutManager layoutManagerEvent = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);

        binding.recyclerviewEvent.setHasFixedSize(true);
        binding.recyclerviewEvent.setLayoutManager(layoutManagerEvent);
        binding.recyclerviewEvent.setAdapter(eventAdapter);
        binding.recyclerviewEvent.addOnScrollListener(new PaginationRecyclerview() {
            @Override
            public int listSize() {
                return eventAdapter.getItemCount();
            }

            @Override
            public int lastItemPosition() {
                return layoutManagerEvent.findLastVisibleItemPosition();
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
                return isLoadingEvent;
            }

            @Override
            public boolean isLastPage() {
                return isLastItemReachedEvent;
            }
        });

        eventAdapter.setEventListener(new EventListener() {
            @Override
            public void onClick(int p, Event event) {
                Intent intent = new Intent(requireActivity(), AddEventActivity.class);
                intent.putExtra("event", gson.toJson(event));
                startForResult.launch(intent);
            }

            @Override
            public void onLongClick(int p, Event event) {
                showBottomSheetEvent(event);
            }
        });
    }

    private void loadEvents(boolean isLoadMore, Calendar c) {
        String key = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        Query first;
        first = db.collection("events")
                .whereEqualTo("userId", userInfo.getUid())
                .whereEqualTo("keyFilter", key)
                .orderBy("timeStamp", Query.Direction.ASCENDING);

        if (isLoadMore && lastVisibleEvent != null) {
            first = first.startAfter(lastVisibleEvent);
        }
        first = first.limit(10);
        isLoadingEvent = true;
        first.get().addOnCompleteListener(task -> {
            isLoadingEvent = false;
            if (getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed()) {
                return;
            }
            if (task.isSuccessful()) {

                List<Event> list = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    Event model = documentSnapshot.toObject(Event.class);
                    if (model == null) {
                        continue;
                    }
                    list.add(model);
                }
                if (!isLoadMore) {
                    eventAdapter.setData(list);
                } else {
                    eventAdapter.addData(list);
                }

                if (petAdapter.getItemCount() > 0) {
                    binding.tvNoItem.setVisibility(View.GONE);
                } else {
                    binding.tvNoItem.setVisibility(View.VISIBLE);
                }

                int size = task.getResult().size();
                if (size > 0) {
                    lastVisibleEvent = task.getResult().getDocuments().get(task.getResult().size() - 1);
                }
                if (size < 10) {
                    isLastItemReachedEvent = true;
                }

            } else {
                Log.d("__index", "loadEvent: " + task.getException().getMessage());
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBottomSheetEvent(Event event) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
        dialog.setContentView(R.layout.layout_bottom_event);
        dialog.setCancelable(true);
        dialog.show();

        View btnCancel = dialog.findViewById(R.id.cancel);
        View btnDelete = dialog.findViewById(R.id.tvDelete);

        Objects.requireNonNull(btnDelete).setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteConfirmationDialogEvent(event);
        });
        Objects.requireNonNull(btnCancel).setOnClickListener(v -> dialog.dismiss());
    }

    private void showDeleteConfirmationDialogEvent(Event event) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa mục này không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            db.collection("events")
                    .document(event.getUid())
                    .delete();
            Toast.makeText(requireActivity(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();

            lastVisibleEvent = null;
            loadEvents(false,calendar);

        });


        builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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
    private int[] images = {R.drawable.pet1, R.drawable.pet2, R.drawable.pet3,R.drawable.pet4};
    private int currentPage = 0;

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadUI();
        lastVisiblePet = null;
        loadPets(false);
        lastVisibleEvent = null;
        loadEvents(false, calendar);
    }
}