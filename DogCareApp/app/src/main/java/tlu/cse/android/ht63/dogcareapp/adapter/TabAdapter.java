package tlu.cse.android.ht63.dogcareapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.model.TabItem;
import tlu.cse.android.ht63.dogcareapp.utils.TabOnListener;

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.VH> {
    private List<TabItem> tabItemList = new ArrayList<>();
    private TabOnListener tabOnListener;
    public  void  setTabOnListener(TabOnListener tabOnListener){
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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TabAdapter.VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
