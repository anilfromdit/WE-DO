package com.afd.wedo;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.afd.wedo.databinding.ActivityReportUserBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;
import java.util.HashMap;

public class ReportUser extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    HashMap<String, Object> hashMap= new HashMap<>();
    String myid;
ActivityReportUserBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle bundle = getIntent().getExtras();
        String usd= (String) bundle.get("usd");
        myid= (String) bundle.get("myid");
        String username= (String) bundle.get("username");

        binding.username.setText(username);
        binding.reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String complaint= binding.report.getText().toString();

                if((binding.report.getText().toString()).length()<4){
                    Toast.makeText(ReportUser.this, "Enter Atleast 4 Character", Toast.LENGTH_SHORT).show();
                    binding.report.setError("Enter Atleast 4 Character");
                    return;
                }
                ProgressDialog pd = new ProgressDialog(ReportUser.this);
                pd.show();
                Calendar calendar = Calendar.getInstance();
                Date now = calendar.getTime();
        Task<Void> database = FirebaseDatabase.getInstance().getReference("reports").child(myid).child(now.toString()).child(usd).setValue (complaint);
            binding.report.setText(" ");
            pd.dismiss();
                Toast.makeText(ReportUser.this, "Reported " +username+ " Successfully", Toast.LENGTH_SHORT).show();
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