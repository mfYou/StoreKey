package com.example.liuyb.storekey.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.liuyb.storekey.R;

import java.util.ArrayList;

/**
 * Created by liuyb on 2017/8/15.
 */

public class KeyListAdapter extends BaseAdapter implements SectionIndexer {

    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private Context context;
    private ArrayList<Key> keyArrayList;

    public interface OnKeyItemClickListener{
        void onKeyItemClick(Key key);
    }
    public interface OnKeyItemLongClickListener{
        void onKeyItemLongClick(Key key);
    }

    public OnKeyItemClickListener mOnKeyItemClickListener;
    public OnKeyItemLongClickListener mOnKeyItemLongClickListener;

    public void setOnKeyItemClickListener(OnKeyItemClickListener listener){
        this.mOnKeyItemClickListener=listener;
    }

    public void setOnKeyItemLongClickListener(OnKeyItemLongClickListener listerner){
        this.mOnKeyItemLongClickListener=listerner;
    }

    public KeyListAdapter(Context context, ArrayList<Key> key) {
        this.context=context;
        this.keyArrayList=key;
    }

    public void setKeys(ArrayList<Key> key){
        this.keyArrayList=key;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return keyArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return keyArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.key_list, null);
            holder = new ViewHolder();
            holder.layout=(LinearLayout)convertView.findViewById(R.id.key_list_layout);
            holder.id = (TextView) convertView.findViewById(R.id.key_id);
            holder.app = (TextView) convertView.findViewById(R.id.key_app);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String finalCity = "";
        if(!keyArrayList.isEmpty()) {
            holder.id.setText(keyArrayList.get(position).getId());
            holder.app.setText(keyArrayList.get(position).getApp());
        }
        if(mOnKeyItemClickListener!=null){
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnKeyItemClickListener.onKeyItemClick(keyArrayList.get(position));
                }
            });
        }
        if (mOnKeyItemLongClickListener != null){
            holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnKeyItemLongClickListener.onKeyItemLongClick(keyArrayList.get(position));
                    return true;
                }
            });
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
    }

    @Override
    public int getPositionForSection(int section) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return position;
    }

    class ViewHolder{
        private View layout;
        private TextView id, app;
    }
}
