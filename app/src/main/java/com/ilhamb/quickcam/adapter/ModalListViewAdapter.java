package com.ilhamb.quickcam.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.ilhamb.quickcam.MainActivity;
import com.ilhamb.quickcam.R;
import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.database.Prefix;
import com.ilhamb.quickcam.fragment.ListModalDialog;

import java.util.List;

public class ModalListViewAdapter implements ListAdapter {

    List<Prefix> arrayList;
    Context context;
    public ModalListViewAdapter(Context context, List<Prefix> arrayList) {

        this.arrayList = arrayList;
        this.context = context;
    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_view_item, null);

            TextView text = convertView.findViewById(R.id.textView);
            text.setText(this.arrayList.get(position).value);

            if(MainActivity.jobManager.prepos == position) {

                text.setTextColor(context.getResources().getColor(R.color.light_blue));
            }
        }

        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
}
