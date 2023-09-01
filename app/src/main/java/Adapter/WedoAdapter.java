package Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afd.wedo.DetailedProfle;
import com.afd.wedo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;

import Fragments.ConnectionFragment;
import Models.Users;
import de.hdodenhof.circleimageview.CircleImageView;

public class WedoAdapter extends RecyclerView.Adapter<WedoAdapter.ViewHolder>{
    private Context mcontext;
    private List<Users> musers;
            String myid;
//    String usd;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String d;
    int atd;

    public WedoAdapter(Context mcontext, List<Users> musers) {
        this.mcontext = mcontext;
        this.musers = musers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
        View view = LayoutInflater.from(mcontext).inflate(R.layout.wedo,parent,false);

        return new WedoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  WedoAdapter.ViewHolder holder, int position) {
        final Users user=musers.get(position);

auth = FirebaseAuth.getInstance();
database = FirebaseDatabase.getInstance();
String usd = user.getId();
myid = auth.getUid();
        holder.username.setText(user.getUsername());
        holder.name.setText(user.getName());

        if((user.getGender()).equals("Male")){
            holder.gender.setImageResource(R.drawable.male);
        }else if((user.getGender()).equals("Female")){
            holder.gender.setImageResource(R.drawable.female);
        }else{
            holder.gender.setImageResource(R.drawable.equality);
        }



        if(user.getIsver().equals("yes")){
            holder.profileVer.setImageResource(R.drawable.check);
        }else{
            holder.profileVer.setImageResource(R.drawable.notcheck);
        }
        int age= age_c(user.getDob());
        holder.age.setText( String.valueOf(age));

        if(user.getProfilePic()!=null) {
            Picasso.get().load(user.getProfilePic()).into(holder.profileImage);
        }
        else {
            holder.profileImage.setImageResource(R.drawable.logo);
        }



        holder.More.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(mcontext, DetailedProfle.class);
               intent.putExtra("id",usd);
               mcontext.startActivity(intent);
           }
       });

        int dtb= calculate_bday(user.getDob());
        holder.birthday.setText("In "+ dtb + " Day(s)");


            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("connections").child(myid).child(usd);
            reference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {

                    String date = snapshot.getValue(String.class);
                    int udate = calculate_bday(date);
                    holder.anniversary.setText("In "+ udate+" Day(s)");


                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });






    }



    private int calculate_bday(String dob) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dt = null;
        try
        {
            dt = sdf.parse(dob);
        }
        catch (final java.text.ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final Calendar BDay = Calendar.getInstance();
        BDay.setTime(dt);
        final Calendar today = Calendar.getInstance();
        final int BMonth = BDay.get(Calendar.MONTH);
        final int CMonth = today.get(Calendar.MONTH);
        BDay.set(Calendar.YEAR, today.get(Calendar.YEAR));
        if(BMonth < CMonth)
        {
            BDay.set(Calendar.YEAR, today.get(Calendar.YEAR) + 1);
        }
        final long millis = BDay.getTimeInMillis() - today.getTimeInMillis();
        long days = millis / 86400000; // Precalculated (24 * 60 * 60 * 1000)

        if(days>=366){
            days= days % 364;
        }
        if(days<0){
            days = days+365;
        }
        return (int) days;
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

    class ViewHolder extends RecyclerView.ViewHolder{

            CircleImageView profileImage,profileVer;
            ImageView gender;
            TextView name,username,age;
            Button birthday,anniversary,More;
        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileVer = itemView.findViewById(R.id.profilecheck);
            gender=itemView.findViewById(R.id.gender);
            name=itemView.findViewById(R.id.fullname);
            username=itemView.findViewById(R.id.username);
            age=itemView.findViewById(R.id.age);
            birthday=itemView.findViewById(R.id.birthday);
            anniversary=itemView.findViewById(R.id.anniversary);
            More=itemView.findViewById(R.id.More);



        }
    }
}
