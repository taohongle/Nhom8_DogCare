package tlu.cse.android.ht63.dogcareapp.utils;

import tlu.cse.android.ht63.dogcareapp.model.Event;

public interface EventListener {
    void onClick(int p, Event event);

    void onLongClick(int p, Event event);
}
