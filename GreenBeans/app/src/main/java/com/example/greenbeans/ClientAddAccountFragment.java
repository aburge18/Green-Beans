package com.example.greenbeans;

import android.os.Build;
import android.os.Bundle;

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


        String js3 = null;



        //used this section to automate the process from the moment the app is run but its recommended the user has some
        // input before authentication and then use refresh tokens to automate future uses

      /*
        String js = "javascript:document.getElementById('username0').value='" + username + "'; " + //enter username
                               "document.getElementById('password1').value='" + password + "'; " + //enter password
                               "document.getElementById('accept').disabled=false;" +               //enable submit button
                               "document.getElementById('accept').click(); " ;                     //click submit button
        String js2;

        String[] secQuestions = {"What is your mother"};
        String[] secAnswers = {"Ann"};
        String js4 = "javascript:document.getElementById('accept').disabled=false; document.getElementById('accept').click();";
        HashMap<String, String> data = new HashMap<String, String>();
        String js3 ="javascript:var question = document.querySelectorAll('p')[2].innerHTML;" +
                "if(question.includes('"+ secQuestions[0] + "')){ document.getElementById('secretquestion0').value='Ann'; }" +
                "document.getElementById('accept').disabled=false; document.getElementById('accept').click(); document.getElementById('accept').click();";*/




        if(trustedDevice) {









            String url = "https://auth.tdameritrade.com/auth?response_type=code&redirect_uri=http://localhost&client_id=HJ8DN850FB0BCX4ZCYCZK85SDKLKPLX7@AMER.OAUTHAP";//url user must go to to authenticate and get token


            webView.loadUrl(url);//loads first webpage
            WebView.setWebContentsDebuggingEnabled(true);//helps inspect element of app on desktop
            String js0 = "javascript:document.getElementById('accept').disabled=false;"; //login button gets locked for some reason
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
//            webView.evaluateJavascript(js0, new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                }
//            });

            webView.setWebViewClient(new WebViewClient() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onPageFinished(WebView view4, String url){//once this new page loads

                    String code = url.substring(19, 23); //the url will contain a code after "code="

                    if (code.matches("code")){//if the url contains "code"
                        try {
                            //decodedToken = URLDecoder.decode(url, "UTF-8");//decode the provided code

                            decodedToken = URLDecoder.decode(url.substring(url.indexOf('=')), "UTF-8");//decode the provided code
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        decodedToken = decodedToken.substring(1);//remove first "=" char from code
                        System.out.println("DECODDDDDE: " + decodedToken);
                        //new HeavyWork().execute();

                    }
                }
            });

        }/*else if(js3 == "sdfs"){


        }else if(!trustedDevice){ //testing manual inital signin (user signs in manually their first use to recieve auth token
            //and that token can be used to retrieve refresh tokens for future auth purposes)
            //This should help with not having to store other sites credentials
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

                        String clientID = "HJ8DN850FB0BCX4ZCYCZK85SDKLKPLX7";
                        String redirect = "http://localhost";
                        String js10 = "javascript:document.getElementById('method_content').style.display = 'block'; " +//loads required parameters on specified site
                                "document.getElementsByName('grant_type')[0].value = 'authorization_code';" +
                                "document.getElementsByName('access_type')[0].value = 'offline';" +
                                "document.getElementsByName('code')[0].value = '" + decodedToken +"';" +
                                "document.getElementsByName('client_id')[0].value = '" + clientID + "';" +
                                "document.getElementsByName('redirect_uri')[0].value = '" + redirect + "';";
                        webView.loadUrl("https://developer.tdameritrade.com/authentication/apis/post/token-0");//specify next site
                        view4.setWebViewClient(new WebViewClient(){
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onPageFinished(WebView view4, String url) {//once the page has loaded
                                webView.evaluateJavascript(js10, new ValueCallback<String>() {//run js
                                    @Override
                                    public void onReceiveValue(String value) {
                                        System.out.println(value + url);
                                    }
                                });
                            }


                        });
                    }
                }
            });
        }*/
        return view;
    }
}