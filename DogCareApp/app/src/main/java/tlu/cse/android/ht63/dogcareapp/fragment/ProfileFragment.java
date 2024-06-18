package tlu.cse.android.ht63.dogcareapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.databinding.FragmentProfileBinding;
import tlu.cse.android.ht63.dogcareapp.databinding.FragmentStoriesBinding;
import tlu.cse.android.ht63.dogcareapp.ui.LoginActivity;
import tlu.cse.android.ht63.dogcareapp.ui.ProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;

    public static Fragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        binding.btnEdit.setOnClickListener(v -> startActivity(new Intent(requireActivity(), ProfileActivity.class)));
        binding.tvLogout.setOnClickListener(v -> showLogoutAccountConfirmationDialog());
        binding.tvDeleteUser.setOnClickListener(v -> showDeleteAccountConfirmationDialog());
        binding.tvContact.setOnClickListener(v -> Toast.makeText(requireActivity(), "Liên hệ", Toast.LENGTH_SHORT).show());
        binding.tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Phiên bản hiện tại là 1.0", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutAccountConfirmationDialog() {

        new AlertDialog.Builder(requireActivity())
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất tài khoản?.")
                .setPositiveButton("Có", (dialog, which) -> logout())
                .setNegativeButton("Không", null)
                .show();
    }

    private void logout(){

        mAuth.signOut();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showDeleteAccountConfirmationDialog() {
    }


}