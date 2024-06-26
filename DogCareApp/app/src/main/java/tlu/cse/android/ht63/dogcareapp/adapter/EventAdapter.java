package tlu.cse.android.ht63.dogcareapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tlu.cse.android.ht63.dogcareapp.R;
import tlu.cse.android.ht63.dogcareapp.model.Event;
import tlu.cse.android.ht63.dogcareapp.utils.EventListener;
import tlu.cse.android.ht63.dogcareapp.utils.Pref;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {

    private List<Event> events = new ArrayList<>();

    private EventListener eventListener;

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    @NonNull
    @Override
    public EventAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EventAdapter.VH holder, int position) {

        Event event = events.get(holder.getAdapterPosition());
        String active;
        switch (event.getActive()) {
            case 1: {
                active = "Tiêm";
                break;
            }
            case 2: {
                active = "Đi dạo";
                break;
            }
            default: {
                active = "Ăn";
                break;
            }
        }

        String habit;
        switch (event.getHabit()) {
            case 1: {
                habit = "Hàng ngày";
                break;
            }
            case 2: {
                habit = "Hàng tuần";
                break;
            }
            case 3: {
                habit = "Hàng tháng";
                break;
            }
            default: {
                habit = "Một lần";
                break;
            }
        }

        holder.tvName.setText(active);
        holder.tvPetName.setText(event.getPetName());
        holder.tvStatus.setText(habit + "  " + Pref.convertDateTime(event.getDateTime()));

        holder.itemView.setOnClickListener(v -> eventListener.onClick(holder.getAdapterPosition(), event));

        holder.itemView.setOnLongClickListener(v -> {
            eventListener.onLongClick(holder.getAdapterPosition(), event);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Event> list) {
        this.events = list;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(List<Event> list) {
        int size = this.events.size();
        this.events.addAll(list);
        notifyItemRangeChanged(size, this.events.size() - 1);
    }


    static class VH extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvStatus;
        private TextView tvPetName;

        public VH(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPetName = itemView.findViewById(R.id.tvPetName);

        }
    }
}
