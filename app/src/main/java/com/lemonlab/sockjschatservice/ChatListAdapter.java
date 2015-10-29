package com.lemonlab.sockjschatservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lk on 15. 10. 30..
 */
public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> chatData;

    public ChatListAdapter(Context context, ArrayList<String> chatData){
        this.context = context;
        this.chatData = chatData;
    }

    @Override
    public int getCount() {
        return chatData.size();
    }

    @Override
    public Object getItem(int position) {
        return chatData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chatting_message, parent, false);
        }
        TextView textView = (TextView)row.findViewById(R.id.chatdata);
        textView.setText(chatData.get(position));
        return row;
    }
}
