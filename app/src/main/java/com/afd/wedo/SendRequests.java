package com.afd.wedo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.afd.wedo.databinding.ActivitySendRequestsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import Models.Users;
import static java.security.AccessController.getContext;

public class SendRequests extends AppCompatActivity {
ActivitySendRequestsBinding binding;
     String Uid="";

    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseDatabase database2 ;
    HashMap<String, Object> hashMap= new HashMap<>();
    String myid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySendRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.nouser.setText(" ");
        auth = FirebaseAuth.getInstance();
        myid = auth.getCurrentUser().getUid();
        database= FirebaseDatabase.getInstance();

        binding.searchbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                binding.nouser.setText(" ");
                binding.userAge.setText(" ");
                binding.userName.setText(" ");
                binding.userUsername.setText(" ");
                binding.userBio.setText(" ");
                binding.userImage.setImageResource(R.drawable.hold);
                binding.userVer.setImageResource(R.drawable.hold);
                binding.rqstBtn.setImageResource(R.drawable.hold);
                binding.gender.setImageResource(R.drawable.hold);

if((binding.searchBar.getText().toString()).length()>0) {
        reference = FirebaseDatabase.getInstance().getReference("usernames").child(binding.searchBar.getText().toString().toLowerCase());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                String u = (snapshot.getValue(String.class));
                Uid=u;
                ProgressDialog pd = new ProgressDialog(SendRequests.this);
                if(Uid!=null && Uid.length()>0){
                    pd.show();
                    try{

                        reference= FirebaseDatabase.getInstance().getReference("Users").child(Uid);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(getContext()==null){
                                    return;
                                }

                                binding.rqstBtn.setImageResource(R.drawable.ido);
                                Users user = snapshot.getValue(Users.class);
                                binding.userUsername.setText(user.getUsername());
                                binding.userName.setText(user.getName()+", ");
                                age_c(user.getDob());
                                if(snapshot.hasChild("profilePic")){
                                    String image = user.getProfilePic();
                                    Picasso.get().load(image).into(binding.userImage);
                                }
                                else{
                                    binding.userImage.setImageResource(R.drawable.logo);
                                }

                                if((user.getBio()).equals("")){
                                    binding.userBio.setText("//No Bio//");
                                }
                                else{
                                    binding.userBio.setText(user.getBio());
                                }

                                if((user.getGender()).equals("Male")){
                                    binding.gender.setImageResource(R.drawable.male);
                                }else if((user.getGender()).equals("Female")){
                                    binding.gender.setImageResource(R.drawable.female);
                                }else{
                                    binding.gender.setImageResource(R.drawable.equality);
                                }
                                if(user.getIsver().equals("yes")){
                                    binding.userVer.setImageResource(R.drawable.check);
                                }else{
                                    binding.userVer.setImageResource(R.drawable.notcheck);
                                }
                                pd.dismiss();
                                do_stuff();

                            }



                            public void age_c(String dob) {
                                int d,m,y;
                                String[] arrOfStr = dob.split("/", 5);

                                d= Integer.parseInt(arrOfStr[0]);
                                m= Integer.parseInt(arrOfStr[1]);
                                y= Integer.parseInt(arrOfStr[2]);

                                LocalDate today = LocalDate.now();
                                LocalDate birthday = LocalDate.of(y, m, d);
                                Period p = Period.between(birthday, today);
                                binding.userAge.setText(String.valueOf(p.getYears()));
                            }


                            private void do_stuff() {
                                if(auth.getCurrentUser().getUid().equals(Uid)){
                                    binding.rqstBtn.setVisibility(View.GONE);
                                    return;
                                }


                                reference = FirebaseDatabase.getInstance().getReference("connections").child(auth.getUid());
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(Uid)) {
                                        binding.rqstBtn.setImageResource(R.drawable.ic_dissolve);

                                        binding.rqstBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Task<Void> reference4 = FirebaseDatabase.getInstance().getReference().child("connections").child(myid).child(Uid).removeValue();
                                                Task<Void> reference5 = FirebaseDatabase.getInstance().getReference().child("connections").child(Uid).child(myid).removeValue();

                                            }
                                        });
                                    }
                                    else{


                                        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("requests").child(Uid);
                                        reference1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange( DataSnapshot snapshot) {

                                                if(snapshot.hasChild(auth.getUid())){
                                                    binding.rqstBtn.setImageResource(R.drawable.undo);
                                                    binding.rqstBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            database.getReference().child("requests").child(Uid).child(auth.getUid()).removeValue();
                                                            Toast.makeText(SendRequests.this, "Proposal canceled", Toast.LENGTH_SHORT).show();
                                                            binding.rqstBtn.setImageResource(R.drawable.ido);
                                                        }
                                                    });}

                                                else{
                                                    binding.rqstBtn.setImageResource(R.drawable.ido);
                                                    binding.rqstBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            pd.show();
                                                            database.getReference().child("requests").child(Uid).child(auth.getUid()).setValue("pending");
                                                            Toast.makeText(SendRequests.this, "Proposal Sent", Toast.LENGTH_SHORT).show();
                                                            binding.rqstBtn.setImageResource(R.drawable.undo);

                                                            pd.dismiss();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled( DatabaseError error) {

                                            }
                                        });

                                    }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });




                            }



                            @Override
                            public void onCancelled(DatabaseError error) {
                                Toast.makeText(SendRequests.this, "Error", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    catch (Error e){
                        pd.dismiss();
                        Toast.makeText(SendRequests.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    pd.dismiss();
                    binding.nouser.setText("No User Found\nHave You Checked Username spellings? ");

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
}
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        hashMap.put("status", "offline");
        database2 = FirebaseDatabase.getInstance();
        database2.getReference().child("Users").child(myid).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hashMap.put("status", "online");
        database2 = FirebaseDatabase.getInstance();
        database2.getReference().child("Users").child(myid).updateChildren(hashMap);
    }
}