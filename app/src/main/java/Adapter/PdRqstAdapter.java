package Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afd.wedo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import Models.Users;
import de.hdodenhof.circleimageview.CircleImageView;

public class PdRqstAdapter extends  RecyclerView.Adapter<PdRqstAdapter.ViewHolder> {
    ProgressDialog pd;
    private Context mcontext;
    private List<Users> musers;
    String myid;
    String usd;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.pendingrqst,parent,false);
        return new PdRqstAdapter.ViewHolder(view);
    }

    public PdRqstAdapter(Context mcontext, List<Users> musers) {
        this.mcontext = mcontext;
        this.musers = musers;
    }

    @Override
    public void onBindViewHolder(@NonNull  PdRqstAdapter.ViewHolder holder, int position) {
final Users user=musers.get(position);
holder.acceptBtn.setImageResource(R.drawable.ido);
holder.rejectBtn.setImageResource(R.drawable.no);
holder.userName.setText(user.getName());
holder.userUsername.setText(user.getUsername());
auth = FirebaseAuth.getInstance();
database= FirebaseDatabase.getInstance();
myid = auth.getUid();
usd = user.getId();
//        Log.d("Tag"," "+usd);
        if((user.getBio()).equals("")){
            holder.userBio.setText(" ");
        }
        else{
            holder.userBio.setText(user.getBio());
        }

        if((user.getGender()).equals("Male")){
            holder.userGender.setImageResource(R.drawable.male);
        }else if((user.getGender()).equals("Female")){
            holder.userGender.setImageResource(R.drawable.female);
        }else{
            holder.userGender.setImageResource(R.drawable.equality);
        }

        if(user.getIsver().equals("yes")){
            holder.userVer.setImageResource(R.drawable.check);
        }else{
            holder.userVer.setImageResource(R.drawable.notcheck);
        }

int age= age_c(user.getDob());
        holder.userAge.setText( String.valueOf(age));
        if(user.getProfilePic()!=null) {
                Picasso.get().load(user.getProfilePic()).into(holder.userImage);
        }
        else {
                holder.userImage.setImageResource(R.drawable.logo);
        }

holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
//        Toast.makeText(mcontext, myid, Toast.LENGTH_SHORT).show();
//        Toast.makeText(mcontext, usd, Toast.LENGTH_SHORT).show();
        try {
            ProgressDialog pd = new ProgressDialog(mcontext);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime now = LocalDateTime.now();
            database.getReference("connections").child(user.getId()).child(myid).setValue(dtf.format(now));
            database.getReference("connections").child(myid).child(user.getId()).setValue(dtf.format(now));
            database.getReference().child("requests").child(myid).child(user.getId()).removeValue();
            pd.dismiss();
            Toast.makeText(mcontext, "congratulations 🎉", Toast.LENGTH_SHORT).show();
        }
        catch (Error e){
            Toast.makeText(mcontext, "Some Error Occured", Toast.LENGTH_SHORT).show();
        }

    }
});
        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference("requests").child("requests").child(myid).child(user.getId()).removeValue();
//                PdRqstAdapter.this.notifyAll();
            }
        });
    }

    private int age_c(String dob) {
            int d,m,y;
            String[] arrOfStr = dob.split("/", 5);

            d= Integer.parseInt(arrOfStr[0]);
            m= Integer.parseInt(arrOfStr[1]);
            y= Integer.parseInt(arrOfStr[2]);

            LocalDate today = LocalDate.now();
            LocalDate birthday = LocalDate.of(y, m, d);
            Period p = Period.between(birthday, today);
        int i = p.getYears();
        return i;
    }

    @Override
    public int getItemCount() {
        return musers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView userImage,acceptBtn,rejectBtn,userVer;
        TextView userUsername,userName,userAge,userBio;
        ImageView userGender;


        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            acceptBtn = itemView.findViewById(R.id.accept_btn);
            rejectBtn = itemView.findViewById(R.id.deny_btn);
            userVer = itemView.findViewById(R.id.user_ver);
            userUsername = itemView.findViewById(R.id.user_username);
            userName = itemView.findViewById(R.id.user_name);
            userAge = itemView.findViewById(R.id.user_age);
            userBio = itemView.findViewById(R.id.user_bio);
            userGender = itemView.findViewById(R.id.gender);

        }
    }
}
