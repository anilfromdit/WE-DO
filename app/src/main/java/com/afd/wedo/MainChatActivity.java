package com.afd.wedo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import Adapter.MessagesAdapter;
import Models.Messages;
import Models.Users;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainChatActivity extends AppCompatActivity {
    TextView name,status;
    CircleImageView profile,sendBtn;
    ImageButton backBtn;
    EditText msgTs;
    RecyclerView msgRcv;
    FirebaseDatabase database;
    ProgressDialog pd;
    private MessagesAdapter msgAdapter;
    private List<Messages> mMessages = new ArrayList<>();
    String uid;
    String usd;
    HashMap<String, Object> hashMap= new HashMap<>();
    HashMap<String, Object> msgMap= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        name = findViewById(R.id.user_name);
        status = findViewById(R.id.status);
        profile = findViewById(R.id.profile_image);
        sendBtn= findViewById(R.id.sendbtn);
        msgTs = findViewById(R.id.msgTs);
        backBtn = findViewById(R.id.backBtn);

        Bundle bundle = getIntent().getExtras();
        uid = (String) bundle.get("Uid");
        usd = (String) bundle.get("Usd");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if ((msgTs.getText().toString()).equals("")){
                    Toast.makeText(MainChatActivity.this, "Can't send empty msg, Sorry", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage();}
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(usd);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
            name.setText(user.getName());
                if(snapshot.hasChild("profilePic")){
                    String image = user.getProfilePic();
                    Picasso.get().load(image).into(profile);
                }
status.setText(user.getStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        msgRcv = findViewById(R.id.msgRcv);
        msgRcv.setHasFixedSize(true);

        msgRcv.setLayoutManager(new LinearLayoutManager(MainChatActivity.this));
        mMessages = new ArrayList<>();
        pd = new ProgressDialog(MainChatActivity.this);
        pd.setMessage("Loading Your Messages");
        pd.show();
        msgAdapter = new MessagesAdapter(MainChatActivity.this,mMessages);
        msgRcv.setAdapter(msgAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("chats").child(uid).child(usd);
        mMessages.clear();
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                mMessages.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    Messages msgs = snap.getValue(Messages.class);
                    mMessages.add(msgs);
                    msgAdapter.notifyDataSetChanged();
                    msgRcv.smoothScrollToPosition(msgRcv.getAdapter().getItemCount());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage() {
       DatabaseReference database= FirebaseDatabase.getInstance().getReference("chats").child(uid).child(usd);
        DatabaseReference database1= FirebaseDatabase.getInstance().getReference("chats").child(usd).child(uid);
        String key = database.push().getKey();
        String key1 = database1.push().getKey();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/YYYY");
        final String date = currentDate.format(cdate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String time = currentTime.format(cdate.getTime());



String msg = msgTs.getText().toString();
        msgMap.put("Date",date);
        msgMap.put("Time",time);
        msgMap.put("Message",msg);
        msgMap.put("Type","Text");
        msgMap.put("Sender",uid);
        msgMap.put("Receiver",usd);

        database.child(key).setValue(msgMap);
        database1.child(key1).setValue(msgMap);

        msgTs.setText("");


        DatabaseReference database2= FirebaseDatabase.getInstance().getReference("lastMessages").child(uid).child(usd);
        DatabaseReference database3= FirebaseDatabase.getInstance().getReference("lastMessages").child(usd).child(uid);
database2.child("msg").setValue(msg);
database3.child("msg").setValue(msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hashMap.put("status", "offline");
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Users").child(uid).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hashMap.put("status", "online");
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Users").child(uid).updateChildren(hashMap);
    }
}