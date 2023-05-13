package com.example.bookapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.bookapp.Fragment.BookManagementFragment;
import com.example.bookapp.Fragment.CommunityFragment;
import com.example.bookapp.Fragment.HomeFragment;
import com.example.bookapp.Fragment.LibraryFragment;
import com.example.bookapp.Fragment.PostManagementFragment;
import com.example.bookapp.Fragment.SettingsFragment;
import com.example.bookapp.R;
import com.example.bookapp.databinding.ActivityAdminBinding;
import com.example.bookapp.databinding.ActivityMainBinding;

public class AdminActivity extends AppCompatActivity {
    ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new BookManagementFragment());
        binding.bottomNavigationViewAdmin.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.books:
                    replaceFragment(new BookManagementFragment());
                    break;

                case R.id.posts:
                    replaceFragment(new PostManagementFragment());
                    break;


            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutAdmin, fragment);
        fragmentTransaction.commit();
    }
}