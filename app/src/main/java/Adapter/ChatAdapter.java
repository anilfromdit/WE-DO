package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afd.wedo.MainChatActivity;
import com.afd.wedo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Users;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context mcontext;
    private List<Users> musers;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public ChatAdapter(Context mcontext, List<Users> musers) {
        this.mcontext = mcontext;
        this.musers = musers;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.chatting_activity_sample,parent,false);

        return new ChatAdapter.ViewHolder(view);



    }

    @Override
    public void onBindViewHolder(@NonNull  ChatAdapter.ViewHolder holder, int position) {
        final Users user = musers.get(position);
        auth = FirebaseAuth.getInstance();

        database= FirebaseDatabase.getInstance();
        holder.name.setText(user.getName());
        if(user.getProfilePic()!=null) {
            Picasso.get().load(user.getProfilePic()).into(holder.profileImage);
        }
        else {
            holder.profileImage.setImageResource(R.drawable.logo);
        }
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("lastMessages").child(auth.getUid()).child(user.getId()).child("msg");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lastMessage = snapshot.getValue(String.class);
                    holder.lastMsg.setText(lastMessage);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch(Error e){
            holder.lastMsg.setText(" ");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, MainChatActivity.class);
                intent.putExtra("Usd",user.getId());
                intent.putExtra("Uid",auth.getCurrentUser().getUid());
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
    CircleImageView profileImage;
    TextView name,lastMsg;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
         profileImage = itemView.findViewById(R.id.user_image);
         name = itemView.findViewById(R.id.name);
            lastMsg = itemView.findViewById(R.id.lastMsg);

        }
    }


}
