package com.lmtri.sharespace.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.lmtri.sharespace.R;
import com.lmtri.sharespace.fragment.HousingFragment;
import com.lmtri.sharespace.helper.BottomNavigationViewHelper;
import com.lmtri.sharespace.model.DummyContent;

public class MainActivity extends AppCompatActivity implements HousingFragment.OnListFragmentInteractionListener {

//    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
                    selectedFragment = HousingFragment.newInstance(1);
                    Toast.makeText(getApplicationContext(), "Fragment 1", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.navigation_saved:
//                    mTextMessage.setText(R.string.title_saved);
                    selectedFragment = HousingFragment.newInstance(1);
                    Toast.makeText(getApplicationContext(), "Fragment 2", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.navigation_share:
//                    mTextMessage.setText(R.string.title_share);
                    selectedFragment = HousingFragment.newInstance(1);
                    Toast.makeText(getApplicationContext(), "Fragment 3", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.navigation_inbox:
                    selectedFragment = HousingFragment.newInstance(1);
                    Toast.makeText(getApplicationContext(), "Fragment 4", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.navigation_profile:
                    selectedFragment = HousingFragment.newInstance(1);
                    Toast.makeText(getApplicationContext(), "Fragment 5", Toast.LENGTH_SHORT).show();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_container, selectedFragment);
            transaction.commit();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_container, HousingFragment.newInstance(1));
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(getApplicationContext(), item.details, Toast.LENGTH_SHORT).show();
    }
}
