package com.ilhamb.quickcam.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.ilhamb.quickcam.MainActivity;
import com.ilhamb.quickcam.R;
import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.utilities.jobManager;

import java.util.List;

public class ListViewAdapter implements ListAdapter {

    List<Job> arrayList;
    Context context;
    public ListViewAdapter(Context context, List<Job> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
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

        if(convertView==null) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView=layoutInflater.inflate(R.layout.custom_listview, null);

            Button delete = convertView.findViewById(R.id.delete);
            TextView _job = convertView.findViewById(R.id._job),
                    _prefix = convertView.findViewById(R.id._prefix);
            _job.setText(arrayList.get(position).value);

            _prefix.setVisibility(View.GONE);

            if(jobManager.folpos == position) {

                String prefix = jobManager.prefixList.size() > 0 ? jobManager.prefixList.get(jobManager.prepos).value : null;
                prefix = prefix != null ? prefix : "null";

                _job.setTextColor(context.getResources().getColor(R.color.purple_200));
                _prefix.setVisibility(View.VISIBLE);
                _prefix.setText( prefix );
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MainActivity.deleteJob(arrayList.get(position));
                }
            });
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
