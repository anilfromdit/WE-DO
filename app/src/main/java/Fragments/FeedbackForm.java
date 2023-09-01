package Fragments;

import android.app.ProgressDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.afd.wedo.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;

public class FeedbackForm extends Fragment {
    Button submit;
    FirebaseAuth auth;
    EditText feedback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_form,container,false);
        submit = view.findViewById(R.id.sendbtn);
        feedback= view.findViewById(R.id.feedback);
        auth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = feedback.getText().toString();
                if(text.length()<4){
                    feedback.setError("Feedback should contain atleast 4 letter");
                    Toast.makeText(getContext(), "Feedback should contain atleast 4 letter", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProgressDialog pd = new ProgressDialog(getContext());
                pd.show();
                Calendar calendar = Calendar.getInstance();
                Date now = calendar.getTime();


                Task<Void> database = FirebaseDatabase.getInstance().getReference("feedbacks").child(auth.getUid()).child(now.toString()).setValue(text);
                feedback.setText(" ");
                pd.dismiss();
                Toast.makeText(getContext(), "Thank you for your feedback ♥", Toast.LENGTH_SHORT).show();


            }
        });
        return view;


    }
}