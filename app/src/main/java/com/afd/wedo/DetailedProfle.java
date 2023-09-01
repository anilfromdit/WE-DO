package com.afd.wedo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import com.afd.wedo.databinding.ActivityDetailedProfleBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import Models.Users;

public class DetailedProfle extends AppCompatActivity {
    FirebaseAuth auth;
String myid= " ";
    ProgressDialog pd;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    HashMap<String, Object> hashMap= new HashMap<>();
ActivityDetailedProfleBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         pd = new ProgressDialog(DetailedProfle.this);
        binding= ActivityDetailedProfleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String usd= (String) b.get("id");
        auth = FirebaseAuth.getInstance();
        myid = auth.getUid();
binding.updateBtn.setVisibility(View.GONE);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(usd);
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                pd.show();
                Users user = snapshot.getValue(Users.class);
                binding.name.setText(user.getName());
                binding.email.setText(user.getEmail());
                binding.username.setText(user.getUsername());
                binding.bio.setText(user.getBio());
                binding.dob.setText(user.getDob());
                binding.gender.setText(user.getGender());
                if(snapshot.hasChild("profilePic")){
                    String image = user.getProfilePic();
                    Picasso.get().load(image).into(binding.dp);
                }else{
                    binding.dp.setImageResource(R.drawable.logo);
                }
                pd.dismiss();

            }
            @Override
            public void onCancelled( DatabaseError error) {
                Toast.makeText(DetailedProfle.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });

        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference("connections").child(myid).child(usd);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String date= snapshot.getValue(String.class);
                binding.Anniversary.setText(date);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


pd.dismiss();

        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

            }
            void updateLabel() {
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                binding.dob.setText(sdf.format(myCalendar.getTime()));
            }
        };

binding.Anniversary.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        binding.updateBtn.setVisibility(View.VISIBLE);
        new DatePickerDialog(DetailedProfle.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

});
binding.updateBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        try {
            pd.show();
            String newDate = binding.dob.getText().toString();
            Task<Void> reference2 = FirebaseDatabase.getInstance().getReference("connections").child(myid).child(usd).setValue(newDate);
            Task<Void> reference3 = FirebaseDatabase.getInstance().getReference("connections").child(usd).child(myid).setValue(newDate);
            pd.dismiss();
        }
        catch (Error e){
            pd.dismiss();
            Toast.makeText(DetailedProfle.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
            return;
        }
        pd.dismiss();
        Toast.makeText(DetailedProfle.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
        binding.updateBtn.setVisibility(View.GONE);
    }
});

binding.reportBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent2 = new Intent(DetailedProfle.this, ReportUser.class);
        intent2.putExtra("usd",usd);
        intent2.putExtra("myid",myid);
        intent2.putExtra("username",binding.username.getText().toString());
        startActivity(intent2);
    }
});

binding.linkBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
Intent intent3= new Intent(DetailedProfle.this,MatchLinks.class);
intent3.putExtra("Usd",usd);
startActivity(intent3);

    }
});
binding.dissolveBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ProgressDialog pd = new ProgressDialog(DetailedProfle.this);
        pd.show();
        Task<Void> reference4 = FirebaseDatabase.getInstance().getReference().child("connections").child(myid).child(usd).removeValue();
        Task<Void> reference5 = FirebaseDatabase.getInstance().getReference().child("connections").child(usd).child(myid).removeValue();
        pd.dismiss();
        Toast.makeText(DetailedProfle.this, "Done ", Toast.LENGTH_SHORT).show();

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
