package com.afd.wedo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import Adapter.ChatAdapter;
import Models.Users;

public class ChatActivity extends AppCompatActivity {
FirebaseAuth auth;
ProgressDialog pd ;
private RecyclerView recyclerView;
 ChatAdapter chatAdapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    HashMap<String, Object> hashMap= new HashMap<>();
    String myid;
List<Users> musers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setTitle("Chats");
        pd = new ProgressDialog(ChatActivity.this);
        pd.setMessage("Loading Your Chats");
        pd.setCancelable(false);
        pd.show();
        auth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {

        super.onStart();

        myid= auth.getCurrentUser().getUid();
        recyclerView = findViewById(R.id.chatRcv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        musers = new ArrayList<>();
        chatAdapter = new ChatAdapter(ChatActivity.this,musers);
        recyclerView.setAdapter(chatAdapter);


        String uid= auth.getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("connections").child(uid);
            musers.clear();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    musers.clear();
                    for(DataSnapshot snap : snapshot.getChildren()){
                        String usd = snap.getKey();
                        DatabaseReference ref;
                        ref=FirebaseDatabase.getInstance().getReference("Users").child(usd);

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                Users user = snapshot.getValue(Users.class);
                                musers.add(user);
                                chatAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                pd.dismiss();
                            }
                        });
                    }
                    pd.dismiss();

                }
                @Override
                public void onCancelled(@NonNull  DatabaseError error) {
                    pd.dismiss();
                }
            });
int numUser=recyclerView.getAdapter().getItemCount();
        Toast.makeText(ChatActivity.this, "NOU: "+numUser, Toast.LENGTH_SHORT).show();

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