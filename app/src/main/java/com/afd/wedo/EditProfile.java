package com.afd.wedo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import Models.Users;

public class EditProfile extends AppCompatActivity {
FirebaseAuth auth;
EditText name,bio;
TextView email,username,Dob,gender;
Button update;
ImageView imageView;
Uri imageuri;
String myUri;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    HashMap<String, Object> hashMap= new HashMap<>();
    String myid;
private StorageReference storeDPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();

        auth= FirebaseAuth.getInstance();
        name=findViewById(R.id.newname);
        email=findViewById(R.id.newemail);
        username=findViewById(R.id.newusername);
        Dob=findViewById(R.id.DOB);
        bio=findViewById(R.id.newbio);
        update=findViewById(R.id.updt_btn);
        imageView=findViewById(R.id.profile_image);
        gender=findViewById(R.id.Tvgender);
        storeDPref= FirebaseStorage.getInstance().getReference().child("Profile_Pic");
myid= auth.getUid();
        userInfo();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity().setAspectRatio(1,1).start(EditProfile.this);


            }
        });
Button i=findViewById(R.id.name12);
i.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        uploadProfileImage();
    }
});


        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfile.this, "Sorry Username Can't be Changed", Toast.LENGTH_SHORT).show();

            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfile.this, "Sorry email Can't be Changed", Toast.LENGTH_SHORT).show();

            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfile.this, "To Update gender status\nPlease Contact us", Toast.LENGTH_LONG).show();
            }
        });




        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                Dob.setText(sdf.format(myCalendar.getTime()));
            }

        };
        Dob.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       new DatePickerDialog(EditProfile.this, date, myCalendar
                                               .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                               myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                                   }

                               });
update.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View v) {


        String id=auth.getCurrentUser().getUid();
                DatabaseReference database= FirebaseDatabase.getInstance().getReference("Users").child(id).child("bio");
                database.setValue(bio.getText().toString());
                database= FirebaseDatabase.getInstance().getReference("Users").child(id).child("name");
                database.setValue(name.getText().toString());
                database= FirebaseDatabase.getInstance().getReference("Users").child(id).child("Dob");
                database.setValue(Dob.getText().toString());


                Toast.makeText(EditProfile.this, "Success", Toast.LENGTH_SHORT).show();
    }
});
    }

    public void userInfo(){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                name.setText(user.getName());
                email.setText(user.getEmail());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                Dob.setText(user.getDob());
                gender.setText(user.getGender());
                if(snapshot.hasChild("profilePic")){
                    String image = user.getProfilePic();
                    Picasso.get().load(image).into(imageView);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data != null){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            imageuri=result.getUri();
            imageView.setImageURI(imageuri);
        }
        else{
            Toast.makeText(this, "An Error Occurred, Try Again", Toast.LENGTH_SHORT).show();
        }
    }
    void uploadProfileImage(){

        ProgressDialog pd = new ProgressDialog(this);
        pd.show();
        if(imageuri!=null){
            StorageReference fileref = storeDPref.child(auth.getCurrentUser().getUid()+".jpg");
            StorageTask uploadTask= fileref.putFile(imageuri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(Task task) throws Exception {
                    if(!task.isSuccessful()){
//                        Toast.makeText(EditProfile.this, (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        Uri downloadUri= (Uri)task.getResult();
                        myUri= downloadUri.toString();

                        HashMap<String,Object> usermap=new HashMap<>();
                        usermap.put("profilePic",myUri);
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
                        databaseReference.child(auth.getCurrentUser().getUid()).updateChildren(usermap);
                        pd.dismiss();
                    }
                }
            });
        }
        else {
            pd.dismiss();
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
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