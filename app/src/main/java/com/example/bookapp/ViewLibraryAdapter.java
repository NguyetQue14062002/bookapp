package com.example.bookapp;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewLibraryAdapter extends FragmentStatePagerAdapter {
    public ViewLibraryAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return  new ReadingFragment();
            case 1:
                return  new UnreadFragment();
            case 2:
                return  new DoneFragment();
            default:

                return  new ReadingFragment();

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Đang đọc";
                break;
            case 1:
                title = "Chưa đọc";
                break;
            case 2:
                title = "Đã xong";
                break;
        }
        return title;
    }
}
