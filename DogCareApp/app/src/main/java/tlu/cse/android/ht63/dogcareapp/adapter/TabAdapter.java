package tlu.cse.android.ht63.dogcareapp.adapter;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.model.TabItem;
import tlu.cse.android.ht63.dogcareapp.utils.TabOnListener;

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.VH> {
    private List<TabItem> tabItemList = new ArrayList<>();
    private TabOnListener tabOnListener;

    public void setTabOnListener(TabOnListener tabOnListener) {
        this.tabOnListener = tabOnListener;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void  addItem(List<TabItem> tabItemList){
        this.tabItemList = tabItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TabItem tabItem = tabItemList.get(holder.getAdapterPosition());
        holder.name.setText(tabItem.getName());
        holder.image.setImageResource(tabItem.getImage());
        if (tabItem.isSelected()) {
            holder.image.setColorFilter(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.main_color),
                    PorterDuff.Mode.SRC_IN
            );
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.main_color));
        } else {
            holder.image.setColorFilter(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.icon_bottom),
                    PorterDuff.Mode.SRC_IN
            );
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.icon_bottom));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabOnListener.onItemClick(holder.getAdapterPosition(), tabItem);
            }
        });

    }


    @Override
    public int getItemCount() {
        return tabItemList.size();
    }


    public void updateView(int p) {
        for (int i = 0; i < this.tabItemList.size(); i++) {
            if (this.tabItemList.get(i).isSelected()) {
                this.tabItemList.get(i).setSelected(false);
                notifyItemChanged(i);
                break;
            }
        }
        this.tabItemList.get(p).setSelected(true);
        notifyItemChanged(p);
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
