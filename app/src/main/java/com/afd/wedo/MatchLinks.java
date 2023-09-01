package com.afd.wedo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import Adapter.MatchLinksAdapter;
import Models.Users;

public class MatchLinks extends AppCompatActivity {
    ProgressDialog pd;
    private RecyclerView recyclerView;
    private MatchLinksAdapter matchLinks;
    private List<Users> musers;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    HashMap<String, Object> hashMap= new HashMap<>();
    String myid;
    FirebaseAuth auth;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_links);
        Bundle bundle = getIntent().getExtras();
        String usd = (String ) bundle.get("Usd");
        auth = FirebaseAuth.getInstance();
myid= auth.getCurrentUser().getUid();
        recyclerView = findViewById(R.id.linksRcv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MatchLinks.this));

        musers = new ArrayList<>();
        pd = new ProgressDialog(MatchLinks.this);
        pd.setMessage("Loading Info...");
        pd.show();
        read_rqsts(usd);
        matchLinks = new MatchLinksAdapter(MatchLinks.this,musers);
        recyclerView.setAdapter(matchLinks);

    }

    private void read_rqsts(String usd) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("connections").child(usd);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
musers.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    String uid = snap.getKey();
                    addUsers(uid);
                    pd.dismiss();
                }
                matchLinks.notifyDataSetChanged();

            }

            private void addUsers(String uid) {

                DatabaseReference ref;
                ref=FirebaseDatabase.getInstance().getReference("Users").child(uid);
//                musers.clear();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        musers.add(user);
                        matchLinks.notifyDataSetChanged();
//                        musers.clear();




                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
        pd.dismiss();
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