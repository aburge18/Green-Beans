package com.example.greenbeans;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClientAddAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientAddAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String decodedToken;
    public ClientAddAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientAddAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientAddAccountFragment newInstance(String param1, String param2) {
        ClientAddAccountFragment fragment = new ClientAddAccountFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_add_account, container, false);
        WebView webView = view.findViewById(R.id.webView);

        Boolean trustedDevice = true;


        if(mListener.getAddAccount().matches("TD")) {

            String url = "https://auth.tdameritrade.com/auth?response_type=code&redirect_uri=http://localhost&client_id=HJ8DN850FB0BCX4ZCYCZK85SDKLKPLX7@AMER.OAUTHAP";//url user must go to to authenticate and get token

            webView.loadUrl(url);//loads first webpage
            WebView.setWebContentsDebuggingEnabled(true);//helps inspect element of app on desktop


            String js0 = "javascript:document.getElementById('accept').disabled=false;"; //login button gets locked for some reason
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.evaluateJavascript(js0, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                }
            });


            webView.setWebViewClient(new WebViewClient() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onPageFinished(WebView view4, String url){//once this new page loads

                    String code = url.substring(19, 23); //the url will contain a code after "code="

                    if (code.matches("code")){//if the url contains "code"
                        try {

                            decodedToken = URLDecoder.decode(url.substring(url.indexOf('=')), "UTF-8");//decode the provided code
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        decodedToken = decodedToken.substring(1);//remove first "=" char from code
                        Account newAccount = new Account();
                        newAccount.accountType = "TD";
                        newAccount.code = decodedToken;
                        newAccount.getFirstRefreshToken(mListener.getUserID());
                        while(newAccount.refreshToken == null){
                            System.out.println("Waiting");
                        }
                        Client tempClient = mListener.getCurrentClient();
                        tempClient.accounts.add(newAccount);
                        System.out.println("Done Waiting");
                        mListener.setCurrentClient(tempClient);
                        //Client client = new Client("Noah","nspen@gmail.com", )
                        System.out.println("DECODDDDDE: " + decodedToken);
                        //new HeavyWork().execute();

                    }
                }
            });
        }
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
        Client getCurrentClient();
        String getUserID();
        String getAddAccount();
        void setCurrentClient(Client client);
    }
}
