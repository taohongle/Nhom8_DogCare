package tlu.cse.android.ht63.dogcareapp.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationRecyclerview extends RecyclerView.OnScrollListener {
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (!isLoading() && !isLastPage()) {
            if (lastItemPosition() == listSize() - 1) {
                loadMoreItem();
            }
        }
    }

    public abstract int listSize();
    public abstract int lastItemPosition();
    public abstract void loadMoreItem();
    public abstract boolean isLoading();
    public abstract boolean isLastPage();
}
