package com.example.greenbeans;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;

    //Buttons on front page
    Button loginBtn, rgstrBtn, forgotBtn;

    //Edit Textboxes on login page
    EditText emailETV, passETV;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailETV = view.findViewById(R.id.emailfield);
        passETV = view.findViewById(R.id.passwordfield);

        loginBtn = view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailETV.getText().toString();
                String passwd = passETV.getText().toString();

                if (email.matches("")) { //Check if name and password are entered
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Please Enter an Email", Toast.LENGTH_SHORT);
                    toast1.show();
                }else if (passwd.matches("")){
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Please Enter a Password", Toast.LENGTH_SHORT);
                    toast1.show();
                } else {//if it passes all checks
                    mAuth = FirebaseAuth.getInstance();
                    //call firebase function to authorize user
                    mAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {//attempt to sign user in
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){//if authorized then proceed
                                mListener.setUsername(mAuth.getCurrentUser().getEmail());//funtion to login with provided email
                            }else{//else provide error message
                                Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Invalid Email and Password", Toast.LENGTH_SHORT);
                                toast1.show();
                            }
                        }
                    });
                }
            }
        });

        rgstrBtn = view.findViewById(R.id.rgstrBtn);

        rgstrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new CreateAccountFragment(), "New").addToBackStack(null).commit();
            }
        });

        forgotBtn = view.findViewById(R.id.forgotBtn);
        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new PasswordResetFragment(), "Reset").addToBackStack(null).commit();

            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if (context instanceof IListener){
            mListener = (IListener)context;
        }else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }
    IListener mListener;

    public interface IListener{
        void setUsername(String username);
    }
}