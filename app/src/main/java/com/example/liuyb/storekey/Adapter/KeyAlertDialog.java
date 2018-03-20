package com.example.liuyb.storekey.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.liuyb.storekey.R;

/**
 * Created by liuyb on 2017/8/16.
 */

public class KeyAlertDialog extends Dialog {

    private Button cancle,check;
    private EditText username, password, key;

    private OnCancleClickListener onCancleClickListener;
    private OnCheckClickListener onCheckClickListener;


    public void setOnCancleClickListener (OnCancleClickListener onCancleClickListener){
        this.onCancleClickListener = onCancleClickListener;
    }

    public void setOnCheckClickListener(OnCheckClickListener onCheckClickListener){
        this.onCheckClickListener = onCheckClickListener;
    }

    public KeyAlertDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_key);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        initView();
        initEvent();
    }

    private void initEvent() {
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancleClickListener!=null){
                    onCancleClickListener.OnCancleClick();
                }
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCheckClickListener != null){
                    onCheckClickListener.OnCheckClick();
                }
            }
        });
    }

    private void initView() {
        cancle = (Button) findViewById(R.id.cancle);
        check = (Button) findViewById(R.id.check);
        username = (EditText) findViewById(R.id.usetext);
        password = (EditText) findViewById(R.id.pastext);
        key = (EditText) findViewById(R.id.keytext);
    }

    public void setPassword(String pass){
        password.setText(pass);
    }

    public void setUser(String user){
        username.setText(user);
    }

    public void setKey(String user){
        key.setText(user);
    }

    public String getKey(){
        return key.getText().toString();
    }



    public interface OnCancleClickListener{
        public void OnCancleClick();
    }

    public interface OnCheckClickListener{
        public void OnCheckClick();
    }
}
