package tlu.cse.android.ht63.dogcareapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import tlu.cse.android.ht63.dogcareapp.fragment.HomeFragment;
import tlu.cse.android.ht63.dogcareapp.fragment.PetFragment;
import tlu.cse.android.ht63.dogcareapp.fragment.ProfileFragment;
import tlu.cse.android.ht63.dogcareapp.fragment.StoriesFragment;

public class PagerAdapter extends FragmentStateAdapter {
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fm;
        switch (position) {
            case 0:
                fm = HomeFragment.newInstance();
                break;
            case 1:
                fm = PetFragment.newInstance();
                break;
            case 2:
                fm = StoriesFragment.newInstance();
                break;
            case 3:
                fm = ProfileFragment.newInstance();
                break;
            default:
                fm = new Fragment();
                break;
        }
        return fm;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
