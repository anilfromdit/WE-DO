package Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afd.wedo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.PdRqstAdapter;
import Models.Users;

public class pdr extends Fragment {
    private RecyclerView recyclerView;
    private PdRqstAdapter pdRqstAdapter;
    private List<Users> musers;
    FirebaseAuth auth;
    ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_pdr,container,false);
        recyclerView = view.findViewById(R.id.myRcv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frg_cont,new AccountFragment());
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                fragmentTransaction.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        musers = new ArrayList<>();
        pdRqstAdapter = new PdRqstAdapter(getContext(),musers);
        recyclerView.setAdapter(pdRqstAdapter);
        read_rqsts();
        return view;
    }

    private void read_rqsts() {
        pd = new ProgressDialog(getContext());
        pd.show();
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("requests").child(auth.getCurrentUser().getUid());
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull  DataSnapshot snapshot) {
               for(DataSnapshot snap : snapshot.getChildren()){
                   String Uid = snap.getKey();
                   addusertolist(Uid);

               }
               pdRqstAdapter.notifyDataSetChanged();
           }

           private void addusertolist(String uid) {
               DatabaseReference ref;
               ref=FirebaseDatabase.getInstance().getReference("Users").child(uid);
               musers.clear();
               ref.addValueEventListener(new ValueEventListener() {

                   @Override
                   public void onDataChange(@NonNull  DataSnapshot snapshot) {

                       Users user = snapshot.getValue(Users.class);
                       musers.add(user);
                       pdRqstAdapter.notifyDataSetChanged();

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
           }

           @Override
           public void onCancelled(@NonNull  DatabaseError error) {

           }
       });
       pd.dismiss();
    }
}