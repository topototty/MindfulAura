package com.example.MAU;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.MAU.Articles.ArticlesFragment;
import com.example.MAU.Notes.ProfileFragment;
import com.example.MAU.Player.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavbar);
        bottomNavigationView.setSelectedItemId(R.id.profile_nav);
        SetFragment(new ProfileFragment());

        if (getIntent().hasExtra("fragment")) {
            String fragmentName = getIntent().getStringExtra("fragment");
            if (fragmentName.equals("articles")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ArticlesFragment()).commit();
            } else if (fragmentName.equals("songs")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            }
        } else {

        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.profile_nav) {
                SetFragment(new ProfileFragment());
                return true;
            } else if (itemId == R.id.settings_nav) {
                SetFragment(new SettingsFragment());
                return true;
            } else if (itemId == R.id.articles_nav) {
                SetFragment(new ArticlesFragment());
                return true;
            }
            else if (itemId == R.id.home_nav) {
                SetFragment(new HomeFragment());
                return true;
            }
            return false;
        });
    }

    public void SetFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, null)
                .commit();
    }
}

