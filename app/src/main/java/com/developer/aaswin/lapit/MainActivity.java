package com.developer.aaswin.lapit;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    android.support.v7.widget.Toolbar mToolbar;

    ViewPager mViewPager;
    SectionPagerAdapter sectionPagerAdapter;
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.title_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lapit Chat");

        //Tabs
        mViewPager=findViewById(R.id.tabPager);
        sectionPagerAdapter=new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(sectionPagerAdapter);

        mTabLayout=findViewById(R.id.mainTabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Request");
        mTabLayout.getTabAt(1).setText("Chats");
        mTabLayout.getTabAt(2).setText("Friends");




    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        }
    }

    private void sendToStart() {
        startActivity(new Intent(MainActivity.this, StartActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
       switch(item.getItemId()){
           case R.id.main_logout_button:
               mAuth.signOut();
               sendToStart();
               break;
           case R.id.main_Allusers_button:
               startActivity(new Intent(getApplicationContext(),UsersActivity.class));
               break;
           case R.id.main_accountSettings_button:
               startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
               break;
       }
        return true;
    }
}
