package tlu.cse.android.ht63.dogcareapp.utils;

import tlu.cse.android.ht63.dogcareapp.model.Pet;

public interface PetListener {
    void onClick(int p, Pet pet);

    void onLongClick(int p, Pet pet);
}
