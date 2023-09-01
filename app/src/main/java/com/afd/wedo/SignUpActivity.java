package com.afd.wedo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.afd.wedo.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {
ActivitySignUpBinding binding;
    private FirebaseAuth auth;
FirebaseDatabase database;
ProgressDialog progressDialog;
String Lusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();//To hide Action Bar
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please Wait While We're Creating Your Account");
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Choose Your Gender...","Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

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

            private void updateLabel() {
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                binding.DOB.setText(sdf.format(myCalendar.getTime()));
            }

        };

        binding.DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignUpActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((binding.username.getText().toString()).length()==0){
                    binding.username.setError("Please Enter Your Username");
                    return;
                }
                Lusername = binding.username.getText().toString().toLowerCase();

                String regex = "^[a-z0-9]*$";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(Lusername);

                if(!m.matches()){
                    binding.username.setError("Username Cannot contain any special character\nEg. of valid usernames: John123, johny25");
                    return;
                }


                if((binding.name.getText().toString()).length()==0){
                    binding.name.setError("Please Enter Your Name");
                    return;
                }

                if((binding.email.getText().toString()).length()==0){
                    binding.email.setError("Please Enter Your Email");
                    return;
                }
                if((binding.password.getText().toString()).length()==0){
                    binding.password.setError("Please Enter Your Password");
                    return;
                }
                if((dropdown.getSelectedItem().toString()).equals("Choose Your Gender...")){
                    ((TextView)dropdown.getSelectedView()).setError("Choose Your Gender.");
                    return;
                }
                if((binding.DOB.getText().toString()).length()==0){
                    binding.DOB.setError("Enter Your Date of Birth");
                    return;
                }
                progressDialog.show();
                Query usernameQuery=database.getReference().child("Users").orderByChild("username").equalTo(Lusername);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                     if(snapshot.getChildrenCount()>0){
                         progressDialog.dismiss();
                         Toast.makeText(SignUpActivity.this, "Username Is Already Taken", Toast.LENGTH_SHORT).show();
                         binding.username.setError("Username Already Taken");
                         return;
                     }
                     else {
                         auth.createUserWithEmailAndPassword(binding.email.getText().toString(),binding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull  Task<AuthResult> task) {
                                 progressDialog.dismiss();
                                 if(task.isSuccessful()){
                                     Toast.makeText(SignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();

                                     HashMap <String, Object> hashMap= new HashMap<>();
                                     hashMap.put("name",binding.name.getText().toString());
                                     hashMap.put("username",Lusername);
                                     hashMap.put("email", binding.email.getText().toString().toLowerCase());
                                     hashMap.put("password",binding.password.getText().toString());
                                     hashMap.put("gender", binding.spinner1.getSelectedItem().toString());
                                     hashMap.put("bio","");
                                     hashMap.put("Dob",binding.DOB.getText().toString());
                                     hashMap.put("Isver","no");
                                     hashMap.put("Id",auth.getCurrentUser().getUid());
                                     String id=task.getResult().getUser().getUid();
                                     database.getReference().child("Users").child(id).setValue(hashMap);
                                     database.getReference().child("usernames").child(binding.username.getText().toString()).setValue(auth.getCurrentUser().getUid());
                                     Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                     startActivity(intent);
                                     finish();
                                 }
                                 else{
                                     Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });

                     }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

            }
        });
    }
}