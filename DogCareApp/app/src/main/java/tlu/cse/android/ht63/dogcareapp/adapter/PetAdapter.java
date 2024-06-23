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


public class PetAdapter extends RecyclerView.Adapter<PetAdapter.VH> {

    private List<Pet> petList = new ArrayList<>();
    private PetListener petListener;

    public void setPetListener(PetListener petListener) {
        this.petListener = petListener;
    }

    @NonNull
    @Override
    public PetAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PetAdapter.VH holder, int position) {

        Pet pet = petList.get(holder.getAdapterPosition());
        holder.name.setText(pet.getName());
        Glide.with(holder.itemView.getContext())
                .load(pet.getImage())
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
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
        private TextView name;
        private ImageView image;
        public VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }
}
