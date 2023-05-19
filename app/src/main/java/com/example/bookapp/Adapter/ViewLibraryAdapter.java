package com.example.bookapp.Adapter;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bookapp.Fragment.DoneFragment;
import com.example.bookapp.Fragment.ReadingFragment;
import com.example.bookapp.Fragment.UnreadFragment;

import java.util.ArrayList;

public class ViewLibraryAdapter extends FragmentStateAdapter{


    public ViewLibraryAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ReadingFragment();

            case 1:
                return new UnreadFragment();

            case 2:
                return new DoneFragment();

            default:
                return new ReadingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
