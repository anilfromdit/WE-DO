package Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.afd.wedo.ChatActivity;
import com.afd.wedo.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
Button feedbackBtn;
CircleImageView infoBtn;
ImageView chatBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
feedbackBtn = view.findViewById(R.id.feedbackbtn);
infoBtn = view.findViewById(R.id.infoBtn);
chatBtn = view.findViewById(R.id.chatBtn);
feedbackBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frg_cont,new FeedbackForm());
        fragmentTransaction.addToBackStack(null);
fragmentTransaction.commit();

    }
});


infoBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frg_cont,new AboutUs());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        }
});

chatBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        startActivity(intent);
    }
});


return view;

    }
}