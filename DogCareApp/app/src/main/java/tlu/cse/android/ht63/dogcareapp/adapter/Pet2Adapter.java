package tlu.cse.android.ht63.dogcareapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.List;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.model.Pet;
import tlu.cse.android.ht63.dogcareapp.utils.PetListener;

public class Pet2Adapter extends RecyclerView.Adapter<Pet2Adapter.VH> {

    private List<Pet> petList = new ArrayList<>();
    private PetListener petListener;

    public void setPetListener(PetListener petListener) {
        this.petListener = petListener;
    }

    @NonNull
    @Override
    public Pet2Adapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet2, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Pet2Adapter.VH holder, int position) {

        Pet pet = petList.get(holder.getAdapterPosition());
        holder.tvPetName.setText("Tên: "+pet.getName());
        holder.tvPetType.setText("Loại: "+pet.getType());
        holder.tvPetGender.setText("Giới tính: "+pet.getGender());
        Glide.with(holder.itemView.getContext())
                .load(pet.getImage())
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> petListener.onClick(holder.getAdapterPosition(), pet));

        holder.itemView.setOnLongClickListener(v -> {
            petListener.onLongClick(holder.getAdapterPosition(), pet);
            return true;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Pet> list) {
        this.petList = list;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(List<Pet> list) {
        int size = this.petList.size();
        this.petList.addAll(list);
        notifyItemRangeChanged(size, this.petList.size() - 1);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView tvPetName;
        private TextView tvPetType;
        private TextView tvPetGender;
        private ImageView image;
        public VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvPetType = itemView.findViewById(R.id.tvPetType);
            tvPetGender = itemView.findViewById(R.id.tvPetGender);
        }
    }
}