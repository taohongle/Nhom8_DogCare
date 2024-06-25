package tlu.cse.android.ht63.dogcareapp.utils;

import tlu.cse.android.ht63.dogcareapp.model.Stories;

public interface StoriesOnListener {
    void onItemClick(int p, Stories stories);
    void onLongClick(int p, Stories stories);
}
