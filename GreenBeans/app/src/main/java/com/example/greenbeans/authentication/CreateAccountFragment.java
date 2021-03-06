package com.example.greenbeans.authentication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.greenbeans.Manager;
import com.example.greenbeans.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAccountFragment extends Fragment {
    EditText firstNameETV, lastNameETV, passETV, passConfirmETV, emailETV, emailConfirmETV;
    Button rgstrSubmitBtn;
    private FirebaseAuth mAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String userType;
    public CreateAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAccountFragment newInstance(String param1, String param2) {
        CreateAccountFragment fragment = new CreateAccountFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        rgstrSubmitBtn = view.findViewById(R.id.rgstrSubmitBtn);
        userType = mListener.getCreateType();
        rgstrSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();

                String firstNameTxt, lastNameTxt, emailTxt, emailConfirmTxt, passTxt, passConfirmTxt;

                firstNameETV = view.findViewById(R.id.firstname1);
                firstNameTxt = firstNameETV.getText().toString();

                lastNameETV = view.findViewById(R.id.lastname1);
                lastNameTxt = lastNameETV.getText().toString();

                emailETV = view.findViewById(R.id.email1);
                emailTxt = emailETV.getText().toString();

                emailConfirmETV = view.findViewById(R.id.verifyemail1);
                emailConfirmTxt = emailConfirmETV.getText().toString();

                passETV = view.findViewById(R.id.password1);
                passTxt = passETV.getText().toString();

                passConfirmETV = view.findViewById(R.id.verifypassword1);
                passConfirmTxt = passConfirmETV.getText().toString();



                if(!emailTxt.matches(emailConfirmTxt)){
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Emails Do Not Match", Toast.LENGTH_SHORT);
                    toast1.show();
                }else if (!passTxt.matches(passConfirmTxt)){
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Passwords Do Not Match", Toast.LENGTH_SHORT);
                    toast1.show();
                }else if (firstNameTxt.matches("") || lastNameTxt.matches("")){
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Please Enter Full Name", Toast.LENGTH_SHORT);
                    toast1.show();
                }else if (!checkEmail(emailTxt)){
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Please Enter a Valid Email", Toast.LENGTH_SHORT);
                    toast1.show();
                }else if (!validPassword(passTxt)){
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Please Enter a Valid Password\n\n" +
                            "Password Must Be At Least 8 Characters, With 1 Number and 1 Letter", Toast.LENGTH_LONG);
                    toast1.show();
                }else {
                    mAuth.createUserWithEmailAndPassword(emailTxt, passTxt).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {//if created
                            if (task.isSuccessful()) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> docData = new HashMap<>();//initialize new user in collection on firebase
                                if(userType.matches("manager")) {
                                    Manager manager = new Manager(firstNameTxt, lastNameTxt, mAuth.getUid(), emailTxt);
                                    docData.put("fName", firstNameTxt);
                                    docData.put("lName", lastNameTxt);
                                    docData.put("email", emailTxt);
                                    ArrayList clients = new ArrayList();
                                    docData.put("clients", clients);
                                    db.collection("managers").document(mAuth.getUid()).set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            getFragmentManager().popBackStack();
                                        }
                                    });
                                }else if(userType.matches("client")){
                                    ArrayList accounts = new ArrayList();
                                    docData.put("accounts", accounts);
                                    docData.put("email", emailTxt);
                                    docData.put("name", firstNameTxt);
                                    db.collection("clients").document(mAuth.getUid()).set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            getFragmentManager().popBackStack();
                                        }
                                    });
                                }

                            }
                        }
                    });
                }
            }
        });

        firstNameETV = view.findViewById(R.id.firstname1);

        return view;
    }
    public boolean checkEmail(String email){
        boolean isValid = true;
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        if (email.matches(regex) == false){
            isValid = false;
        }
        return isValid;
    }

    public boolean validPassword(String password){//check if phone number is valid
        boolean validP = true;
        String regexStr = "^(?=.*\\d).{8,15}$";
        if( password.matches(regexStr)==false  ) {
            validP = false;
        }
        return validP;
    }
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if (context instanceof LoginFragment.IListener){
            mListener = (IListener)context;
        }else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }

    IListener mListener;
    public interface IListener{
        void setUsername(String username);
        String getCreateType();
    }
}