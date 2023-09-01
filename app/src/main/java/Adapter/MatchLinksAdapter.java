package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afd.wedo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import Models.Users;
import de.hdodenhof.circleimageview.CircleImageView;

public class MatchLinksAdapter extends RecyclerView.Adapter<MatchLinksAdapter.ViewHolder> {


    private Context mcontext;
    private List<Users> musers;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public MatchLinksAdapter(Context mcontext, List<Users> musers) {
        this.mcontext = mcontext;
        this.musers = musers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mcontext).inflate(R.layout.matchlinksview,parent,false);
        return new MatchLinksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MatchLinksAdapter.ViewHolder holder, int position) {
        final Users user=musers.get(position);
//        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        holder.name.setText(user.getName());
        holder.username.setText(user.getUsername());
        if((user.getBio()).equals("")){
            holder.bio.setText(" ");
        }
        else{
            holder.bio.setText(user.getBio());
        }

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

    class ViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView profileImage,profileVer;
        ImageView gender;
        TextView name,username,age,bio;
        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.user_image);
            profileVer = itemView.findViewById(R.id.user_ver);
            gender=itemView.findViewById(R.id.gender);
            name=itemView.findViewById(R.id.nameG);
            username=itemView.findViewById(R.id.username);
            age=itemView.findViewById(R.id.user_age);
            bio= itemView.findViewById(R.id.bio);

        }
    }
}
