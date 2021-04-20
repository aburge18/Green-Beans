package com.example.greenbeans;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssignManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignManagerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView managerCode;
    public AssignManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssignManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AssignManagerFragment newInstance(String param1, String param2) {
        AssignManagerFragment fragment = new AssignManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assign_manager, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();//initialize firestore
        Button submitBtn = view.findViewById(R.id.submitManagerBtn);
        managerCode = view.findViewById(R.id.managerCodeETV);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!managerCode.getText().toString().matches("")){//if etv isnt empty
                    DocumentReference managerdb = db.collection("managers").document(managerCode.getText().toString());//get document for current manager
                    managerdb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            String docString = task.getResult().toString();
                            String docNull = docString.substring(docString.indexOf("doc=") + 4, docString.indexOf("doc=") + 8);
                            if(docNull.matches("null")) {

                            //toast to enter new code
                                Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Please Enter a Valid Manager Code", Toast.LENGTH_SHORT);
                                toast1.show();
                            }else {
                                managerdb.update("clients", FieldValue.arrayUnion(mAuth.getUid()));
                            }
                        }
                    });
                }
            }
        });
        return view;
    }
}