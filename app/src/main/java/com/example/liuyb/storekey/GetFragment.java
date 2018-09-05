package com.example.liuyb.storekey;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.liuyb.storekey.Adapter.AESUtils;
import com.example.liuyb.storekey.Adapter.Key;
import com.example.liuyb.storekey.Adapter.KeyAlertDialog;
import com.example.liuyb.storekey.Adapter.KeyListAdapter;
import com.example.liuyb.storekey.view.LogoutDialog;
import com.example.liuyb.storekey.view.RefreshableView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GetFragment extends Fragment implements KeyListAdapter.OnKeyItemClickListener,KeyListAdapter.OnKeyItemLongClickListener,View.OnClickListener{

    RefreshableView refreshableView;
    ListView listView;

    private static final String url_get_all_key = "http://119.28.204.228/api/get.php";
    private static final String url_search_key = "http://119.28.204.228/api/search.php";
    private static final String url_delete_key = "http://119.28.204.228/api/delete.php";
    JSONParser jsonParser = new JSONParser();

    private static final String TAG = "Liu";
    private static final String TAG_CODE = "code";
    private static final String TAG_DATA = "data";
    private static final String TAG_ID = "id";
    private static final String TAG_APP ="app";
    private static final String TAG_KEY = "key";
    private static final String TAG_USER ="username";
    private static final String TAG_PAS = "password";

    JSONArray keyInfo = null;
    ArrayList<Key> keyArrayList = new ArrayList<Key>();

    Button button;
    EditText editText;
    KeyAlertDialog dialog;
    LogoutDialog ldialog;
    private String content;
    private String id;
    private String app;
    private String aeskey;
    private String username;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        refreshableView = (RefreshableView) view.findViewById(R.id.refreshable_view);
        listView = (ListView) view.findViewById(R.id.list_key);
        button = (Button) view.findViewById(R.id.search_btn);
        editText = (EditText) view.findViewById(R.id.search_edit_text);
        button.setOnClickListener(this);

        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener(){
            @Override
            public void onRefresh() {
                try {
                    keyArrayList.clear();
                    new LoadAllKey().execute();
                    Thread.sleep(800);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        },0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_btn:
                Log.i(TAG,"click");
                if (TextUtils.isEmpty(editText.getText())){
                    Toast.makeText(getActivity(), "请输入搜索内容！", Toast.LENGTH_SHORT).show();
                }else{
                    keyArrayList.clear();
                    content = editText.getText().toString();
                    new SearchKey().execute();
                }
                break;
        }
    }

    @Override
    public void onKeyItemLongClick(Key key) {
        id = key.getId();
        ldialog = new LogoutDialog(getActivity());
        ldialog.setTitle("删除提醒");
        ldialog.setMessage("确定删除这条记录？");
        ldialog.setCancelable(false);
        ldialog.setYesOnclickListener("确定", new LogoutDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                new DeleteKey().execute();
                keyArrayList.clear();
                new LoadAllKey().execute();
                ldialog.dismiss();
            }
        });
        ldialog.setNoOnclickListener("取消", new LogoutDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                ldialog.dismiss();
            }
        });
        ldialog.show();
    }

    @Override
    public void onKeyItemClick(Key key) {
        Log.i(TAG,key.toString());
        app = key.getApp();
        aeskey = key.getKey();
        Log.i(TAG,"aes"+app);
        dialog = new KeyAlertDialog(getActivity());
        dialog.setOnCheckClickListener(new KeyAlertDialog.OnCheckClickListener(){
            @Override
            public void OnCheckClick() {

                dialog.setUser("");
                dialog.setPassword("");
                if (dialog.getKey().equals("")){
                    Toast.makeText(getActivity(), "请输入密钥！", Toast.LENGTH_SHORT).show();
                }else {
                    String keygen = dialog.getKey();
                    try {
                        String strResult = AESUtils.decrypt(keygen,aeskey);
                        JSONObject c = new JSONObject(strResult);
                        username = c.getString(TAG_USER);
                        password = c.getString(TAG_PAS);
                        dialog.setUser(username);
                        dialog.setPassword(password);
                        dialog.setKey("");
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "密钥错误！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        dialog.setOnCancleClickListener(new KeyAlertDialog.OnCancleClickListener() {
            @Override
            public void OnCancleClick() {
                Log.i(TAG,"tuichu");
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    class LoadAllKey extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            JSONObject json = jsonParser.makeHttpRequest(url_get_all_key,"GET",param);
            if (json!=null){
                Log.i(TAG,json.toString());
            }
            try {
                keyInfo = json.getJSONArray(TAG_DATA);

                for (int i = 0; i <keyInfo.length(); i++){
                    JSONObject c = keyInfo.getJSONObject(i);
                    String id = c.getString(TAG_ID);
                    String app = c.getString(TAG_APP);
                    String key = c.getString(TAG_KEY);

                    Key tempKey = new Key(id,app,key);
                    Log.i(TAG,tempKey.toString());
                    keyArrayList.add(tempKey);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (keyArrayList.size()!= 0){
                            KeyListAdapter adapter = new KeyListAdapter(getActivity(), keyArrayList);
                            adapter.setOnKeyItemClickListener(GetFragment.this);
                            adapter.setOnKeyItemLongClickListener(GetFragment.this);
                            listView.setAdapter(adapter);
                        }else {
                            Toast.makeText(getActivity(), "没有找到应用！", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    class SearchKey extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair(TAG_APP,content));
            JSONObject json = jsonParser.makeHttpRequest(url_search_key,"GET",param);
            try {
                keyInfo = json.getJSONArray(TAG_DATA);

                for (int i = 0; i <keyInfo.length(); i++){
                    JSONObject c = keyInfo.getJSONObject(i);
                    String id = c.getString(TAG_ID);
                    String app = c.getString(TAG_APP);
                    String key = c.getString(TAG_KEY);

                    Key tempKey = new Key(id,app,key);
                    Log.i(TAG,tempKey.toString());
                    keyArrayList.add(tempKey);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (keyArrayList.size() != 0){
                    KeyListAdapter adapter = new KeyListAdapter(getActivity(), keyArrayList);
                    adapter.setOnKeyItemClickListener(GetFragment.this);
                    adapter.setOnKeyItemLongClickListener(GetFragment.this);
                    listView.setAdapter(adapter);
                }else {
                    Toast.makeText(getActivity(), "没有找到结果！", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class DeleteKey extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair(TAG_ID,id));
            JSONObject json = jsonParser.makeHttpRequest(url_delete_key,"GET",param);
            try {
                int code = json.getInt(TAG_CODE);
                handler.sendEmptyMessage(code);
            }catch (JSONException e){
                handler.sendEmptyMessage(2);
                e.printStackTrace();
            }
            return null;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case 0:
                    Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
