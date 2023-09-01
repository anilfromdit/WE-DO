package com.afd.wedo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.afd.wedo.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import Fragments.AccountFragment;
import Fragments.ConnectionFragment;
import Fragments.HomeFragment;


public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
FirebaseAuth auth;
FirebaseDatabase database;
String myid;
        HashMap<String, Object> hashMap= new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){
            Intent intent = new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
            finish();
        }
        hashMap.put("status","online");
        database = FirebaseDatabase.getInstance();
        myid = auth.getCurrentUser().getUid();
        database.getReference().child("Users").child(myid).updateChildren(hashMap);


        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                getSupportActionBar().hide();
        fragmentTransaction.replace(R.id.frg_cont,new HomeFragment());
        binding.btmNav.setSelectedItemId(R.id.home);

        fragmentTransaction.commit();
        binding.btmNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()){
                    case R.id.account:

                        fragmentTransaction.replace(R.id.frg_cont,new AccountFragment());
                        getSupportActionBar().hide();
                        break;
                    case R.id.home:
                        fragmentTransaction.replace(R.id.frg_cont,new HomeFragment());
                        getSupportActionBar().hide();
                        break;
                    case R.id.setting:
                        fragmentTransaction.replace(R.id.frg_cont,new ConnectionFragment());
                        getSupportActionBar().show();
                        getSupportActionBar().setTitle("Your Connections");
                        break;
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;
            }
        });
}

    @Override
    protected void onPause() {
        super.onPause();
        hashMap.put("status", "offline");
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Users").child(myid).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hashMap.put("status", "online");
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Users").child(myid).updateChildren(hashMap);
    }


}