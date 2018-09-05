package com.example.liuyb.storekey;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liuyb.storekey.Adapter.AESUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = "Liu";
    private static final String TAG_APP = "app";
    private static final String TAG_KEY = "key";
    private static final String TAG_CODE = "code";

    private static final String url_store_key = "http://119.28.204.228/api/insert.php";
    JSONParser jsonParser = new JSONParser();

    EditText app, username,password,key;
    Button submit, create;

    private String appName;
    private String appKey;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        app = (EditText) view.findViewById(R.id.app_text);
        username = (EditText) view.findViewById(R.id.username_text);
        key = (EditText) view.findViewById(R.id.password_text);
        password = (EditText) view.findViewById(R.id.password);
        submit = (Button) view.findViewById(R.id.tijiao);
        create = (Button) view.findViewById(R.id.create);
        key.setEnabled(true);
        create.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tijiao:
                if (TextUtils.isEmpty(app.getText())||TextUtils.isEmpty(username.getText())||TextUtils.isEmpty(key.getText())){
                    Toast.makeText(getActivity(), "请填写完整数据！", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password.getText())){
                    Toast.makeText(getActivity(), "请输入密码！", Toast.LENGTH_SHORT).show();
                }else{
                    appName = app.getText().toString();
                    String usernameText = username.getText().toString();
                    String keyText = key.getText().toString();
                    String passwordText = password.getText().toString();
                    String data = "{'username':'"+usernameText+"','password':'"+keyText+"'}";

                    try {
                        appKey = AESUtils.encrypt(passwordText,data);
                        new StoreKey().execute();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.create:
                createPassword();
                break;
        }
    }

    private void createPassword() {
        int num = new Random().nextInt(8)+8;
        String pass = generateString(num);
        key.setText(pass);
    }

    public static String generateString(int length) {
        String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-=+!@#$%&";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return sb.toString();
    }

    class StoreKey extends AsyncTask<String,String ,String>{

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair(TAG_APP,appName));
            param.add(new BasicNameValuePair(TAG_KEY,appKey));
            JSONObject json = jsonParser.makeHttpRequest(url_store_key,"GET",param);
            try {
                Log.i(TAG,json.toString());
                String code = json.getString(TAG_CODE);
                return code;
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                if (s.equals("1")){
                    Toast.makeText(getActivity(), "插入成功", Toast.LENGTH_SHORT).show();
                    app.setText("");
                    username.setText("");
                    key.setText("");
                    password.setText("");
                }else {
                    Toast.makeText(getActivity(), "插入失败", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
