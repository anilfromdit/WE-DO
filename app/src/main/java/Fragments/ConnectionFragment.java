package Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afd.wedo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import Adapter.WedoAdapter;
import Models.Users;

public class ConnectionFragment extends Fragment {
    ProgressDialog pd;
private RecyclerView recyclerView;
private WedoAdapter wedoAdapter;
private List<Users> musers;
FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        View rootView = inflater.inflate(R.layout.fragment_connection,container,false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.wedoRcv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        musers = new ArrayList<>();
        wedoAdapter = new WedoAdapter(getContext(),musers);
        recyclerView.setAdapter(wedoAdapter);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading Your Connections");
        pd.setCancelable(false);
        pd.show();
        read_connections();
        System.out.print("\n\n\n\n\n\nHello "+musers.size()+" ch\n");
        pd.dismiss();
        return rootView;
    }

    private void read_connections() {

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("connections").child(auth.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musers.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    String Uid = snap.getKey();
                    addusertolist(Uid);
                }
            }



            private void addusertolist(String uid) {

                DatabaseReference ref;
                ref=FirebaseDatabase.getInstance().getReference("Users").child(uid);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        musers.add(user);
                        wedoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}