package com.example.greenbeans;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAlpacaAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAlpacaAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String apiKey;
    String secret;
    public AddAlpacaAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddAlpacaAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAlpacaAccountFragment newInstance(String param1, String param2) {
        AddAlpacaAccountFragment fragment = new AddAlpacaAccountFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_alpaca_account, container, false);
        WebView webView = view.findViewById(R.id.alpacaWebView);
        EditText apiKeyETV = view.findViewById(R.id.apiKeyETV);
        EditText apiSecretKeyETV = view.findViewById(R.id.apiSecretKeyETV);
        Boolean trustedDevice = true;

        Button loginBtn = view.findViewById(R.id.loginToAlpacaBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://app.alpaca.markets/login";//url user must go to to authenticate and get token
                webView.getSettings().setDomStorageEnabled(true);
                // webView.loadUrl(url);//loads first webpage
                webView.loadUrl(url);
            }
        });


        Button addAPIBtn = view.findViewById(R.id.addAPIKeysBtn);
        addAPIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiKey = apiKeyETV.getText().toString();
                secret = apiSecretKeyETV.getText().toString();
                if(apiKey.matches("")){

                }else if(secret.matches("")){

                }else {
                    GetAccountRunnable runnable = new GetAccountRunnable();
                    new Thread(runnable).start();
                }
            }
        });



        return view;
    }


    class GetAccountRunnable implements Runnable{

        @Override
        public void run() {
            //FormBody formBody = new FormBody.Builder().add("grant_type", "authorization_code").add("access_type", "offline").add("code", code).add("client_id", "HJ8DN850FB0BCX4ZCYCZK85SDKLKPLX7").add("redirect_uri", "http://localhost").build();
            //create post request with specified header
            final Request request = new Request.Builder().url("https://api.alpaca.markets/v2/account").get().addHeader("APCA-API-KEY-ID", apiKey).addHeader("APCA-API-SECRET-KEY", secret).build();

            String response1Body;//will hold entire response body



                try(Response response1 = client.newCall(request).execute()) {
                    if (!response1.isSuccessful()){
                        throw new IOException("Unexpected code " + response1);
                    }

                    response1Body = response1.body().string();
                    System.out.println(response1Body);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();//initialize firestore
                    Map<String, Object> data = new HashMap<>();
                    data.put("accountType", "Alpaca");
                    data.put("apiKey", apiKey);
                    data.put("secret", secret);
                    String clientID = mListener.getUserID();


                    DocumentReference accounts = db.collection("accounts").document();
                    accounts.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DocumentReference currentClient = db.collection("clients").document(clientID);

                            currentClient.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    //JSONObject clientInfo = new JSONObject(document.getData());
                                    System.out.println("Account Present: " + clientID);
                                    currentClient.update("accounts", FieldValue.arrayUnion(accounts.getId()));
                                    getFragmentManager().popBackStack();
                                }

                            });

                            System.out.println(accounts.getId());
                        }
                    });

            } catch (IOException e) {
                System.out.println("ERRRRRROR:");
                e.printStackTrace();

            }
        }
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

        String getUserID();

    }
}