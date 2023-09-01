package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.afd.wedo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Models.Messages;
import Models.Users;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter{
    private Context mcontext;
private List<Messages> mMeassages;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String prvDate="";
//    String myid;


    public MessagesAdapter(Context mcontext,List <Messages> mMeassages){
        this.mcontext=mcontext;
        this.mMeassages= mMeassages;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;

        if(viewType == 1){
            View view = LayoutInflater.from(mcontext).inflate(R.layout.outgoing_msg_layout,parent,false);
            return new Sender(view);
        }
        else {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.incoming_msg_layout,parent,false);
            return new Receiver(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/YYYY");
 Messages msgs = mMeassages.get(position);
        auth = FirebaseAuth.getInstance();
        String date = msgs.getDate();

if(holder.getClass() == Sender.class){
   ((Sender) holder).msg.setText(msgs.getMessage());
    ((Sender) holder).time.setText(msgs.getTime());


if(!(prvDate.equals(date))){

    ((Sender) holder).toast.setVisibility(View.VISIBLE);
    prvDate= msgs.getDate();
}



    final String dateC = currentDate.format(cdate.getTime());
        if(msgs.getDate().equals(dateC)){

        ((Sender) holder).toast.setText(" Today ");
        }
        else
        ((Sender) holder).toast.setText(msgs.getDate());
}
else{
    String sender = msgs.getSender();
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(sender);
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.hasChild("profilePic")){
                Users user = snapshot.getValue(Users.class);
                String image = user.getProfilePic();
                Picasso.get().load(image).into( ((Receiver)holder).image );
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

//    try{
//        String image =
//        Picasso.get().load(image).into(dp);
//    } catch (Error e){
//
//    }

    ((Receiver) holder).msg.setText(msgs.getMessage());
    ((Receiver) holder).time.setText(msgs.getTime());
    if(!(prvDate.equals(date))){

        ((Receiver) holder).toast.setVisibility(View.VISIBLE);
        prvDate= msgs.getDate();
    }
    final String dateC = currentDate.format(cdate.getTime());
    if(msgs.getDate().equals(dateC)){

        ((Receiver) holder).toast.setText(" Today ");
    }
    else
        ((Receiver) holder).toast.setText(msgs.getDate());

}

    }

    @Override
    public int getItemCount() {
        return mMeassages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mMeassages.get(position).getSender().equals(FirebaseAuth.getInstance().getUid())) {
            return 1;
        }
        else
            return 2;
    }

    public class Receiver extends  RecyclerView.ViewHolder{
            TextView toast,msg,time;
                    CircleImageView image;
            public Receiver(@NonNull View itemView) {
                super(itemView);
                toast = itemView.findViewById(R.id.dateToast);
            msg = itemView.findViewById(R.id.msg);
            time = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.user_image);
            }
        }
        public class Sender extends  RecyclerView.ViewHolder{
            TextView toast,msg,time;
            public Sender(@NonNull View itemView) {
                super(itemView);
                toast = itemView.findViewById(R.id.dateToast);
            msg = itemView.findViewById(R.id.msg);
            time = itemView.findViewById(R.id.time);

            }
        }

}