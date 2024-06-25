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
import tlu.cse.android.ht63.dogcareapp.model.Stories;
import tlu.cse.android.ht63.dogcareapp.utils.Pref;
import tlu.cse.android.ht63.dogcareapp.utils.StoriesOnListener;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.VH>{
    private List<Stories> stories = new ArrayList<>();

    private StoriesOnListener storiesOnListener;
    public void setStoriesOnListener(StoriesOnListener storiesOnListener) {
        this.storiesOnListener = storiesOnListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stories, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesAdapter.VH holder, int position) {
        Stories st = stories.get(holder.getAdapterPosition());
        holder.tvTitle.setText(st.getTitle());
        holder.tvDateTime.setText(Pref.convertDateTime(st.getTime()));
        Glide.with(holder.itemView.getContext())
                .load(st.getImage())
                .centerCrop()
                .error(R.drawable.ic_launcher_foreground)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> storiesOnListener.onItemClick(holder.getAdapterPosition(), st));
        holder.itemView.setOnLongClickListener(v -> {
            storiesOnListener.onLongClick(holder.getAdapterPosition(), st);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Stories> list) {
        this.stories = list;
        notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(List<Stories> list) {
        this.stories.addAll(list);
        notifyDataSetChanged();

    }
    public class VH extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvDateTime;
        private ImageView image;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            image = itemView.findViewById(R.id.image);

        }
    }
}