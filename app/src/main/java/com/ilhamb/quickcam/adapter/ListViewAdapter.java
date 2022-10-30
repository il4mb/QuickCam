package com.ilhamb.quickcam.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.ilhamb.quickcam.MainActivity;
import com.ilhamb.quickcam.R;
import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.fragment.ListModalDialog;
import com.ilhamb.quickcam.utilities.TODO;

import java.util.List;

public class ListViewAdapter implements ListAdapter {

    List<Job> arrayList;
    Context context;
    FragmentManager fragmentManager;
    public ListViewAdapter(Context context, FragmentManager fm, List<Job> arrayList) {

        this.arrayList = arrayList;
        this.context = context;
        this.fragmentManager = fm;
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

            TextView delete = convertView.findViewById(R.id.delete),
                    _job = convertView.findViewById(R.id._job),
                    _prefix = convertView.findViewById(R.id._prefix);
            _job.setText(arrayList.get(position).value);

            _prefix.setVisibility(View.GONE);

            if(MainActivity.jobManager.folpos == position) {

                String prefix = MainActivity.jobManager.prefixList.size() > 0 ? MainActivity.jobManager.prefixList.get(MainActivity.jobManager.prepos).value : null;
                prefix = prefix != null ? prefix : "null";

                _job.setTextColor(context.getResources().getColor(R.color.light_blue));
                _prefix.setVisibility(View.VISIBLE);
                _prefix.setText( prefix );
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MainActivity.deleteJob(arrayList.get(position));
                }
            });

            _prefix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //MainActivity.jobManager.forwardPrefix();

                    ListModalDialog fragment = ListModalDialog.newInstance(position);
                    fragment.onSave(new TODO() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onCallBack(int key, String data) {

                            _prefix.setText(data);
                        }
                    });
                    fragment.show(fragmentManager, "HALLO");
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
