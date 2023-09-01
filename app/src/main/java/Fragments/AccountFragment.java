package Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.afd.wedo.EditProfile;
import com.afd.wedo.R;
import com.afd.wedo.SendRequests;
import com.afd.wedo.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.time.LocalDate;
import java.time.Period;
import Models.Users;

public class AccountFragment extends Fragment {
    ImageView dp,ver,gender;
    TextView name,username,age,bio;
    FirebaseAuth auth;
    Button btn,btn2,btn3,pendingRqsts,wedo,sendRqst;
    String vf;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        View view= inflater.inflate(R.layout.fragment_account,container,false);
dp=view.findViewById(R.id.profile_image);
    name = view.findViewById(R.id.fullname);
    username = view.findViewById(R.id.username);
    age=view.findViewById(R.id.age);
    gender=view.findViewById(R.id.gender);
    bio=view.findViewById(R.id.Bio);
    ver=view.findViewById(R.id.profilecheck);
userInfo();
btn=view.findViewById(R.id.edit_pro);
btn2=view.findViewById(R.id.rqst_ver_btn);
btn3=view.findViewById(R.id.sign_out);
        pendingRqsts=view.findViewById(R.id.pendingRqst);
        wedo=view.findViewById(R.id.weDo);
        sendRqst=view.findViewById(R.id.sendRqst);

        pendingRqsts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Pending Requests");
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frg_cont,new pdr());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        wedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frg_cont,new ConnectionFragment());
                fragmentTransaction.commit();
            }
        });
        sendRqst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SendRequests.class);
                startActivity(intent);
            }
        });



btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), EditProfile.class);
        startActivity(intent);

    }
});


btn2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(vf.equals("yes")){
            Toast.makeText(getContext(), "Your Account is Already Verified", Toast.LENGTH_SHORT).show();
        }
        else{

            String [] email = {"anilfromdit@gmail.com"};
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, email);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Verification Request " +auth.getUid());
            intent.putExtra(Intent.EXTRA_TEXT, "Here I'm Attaching my Govt. Issued id to confirm my identity and  Request Verification Tick on my Account. I here by agree to All T&C of usage of this app");
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        }
    }
});

btn3.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        auth.signOut();
        Intent intent = new Intent(getContext(),SignInActivity.class);
        startActivity(intent);
getActivity().finish();
    }
});
        return view;
    }


    public void userInfo(){

    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
    reference.addValueEventListener(new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot snapshot) {
        if(getContext()==null){
            return;
        }
        Users user = snapshot.getValue(Users.class);
        username.setText(user.getUsername());
        name.setText(user.getName()+", ");
        age_c(user.getDob());
        if(snapshot.hasChild("profilePic")){
        String image = user.getProfilePic();
            Picasso.get().load(image).into(dp);
        }


        if((user.getBio()).equals("null")){
            bio.setText("//No Bio//");
        }
        else{
            bio.setText(user.getBio());
        }

            if((user.getGender()).equals("Male")){
                gender.setImageResource(R.drawable.male);
            }else if((user.getGender()).equals("Female")){
                gender.setImageResource(R.drawable.female);
            }else{
                gender.setImageResource(R.drawable.equality);
            }
vf=user.getIsver();
        if(user.getIsver().equals("yes")){
            ver.setImageResource(R.drawable.check);
        }else{
            ver.setImageResource(R.drawable.notcheck);
        }
        ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((user.getIsver())==("yes")){
                    Toast.makeText(getContext(), "Your Profile is Verified", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Your Profile isn't Verified", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            age.setText(String.valueOf(p.getYears()));
        }

        @Override
        public void onCancelled(DatabaseError error) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

        }
    });
}
}