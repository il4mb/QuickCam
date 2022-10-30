package com.ilhamb.quickcam.adapter;

import static com.ilhamb.quickcam.MainActivity.jobManager;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ilhamb.quickcam.MainActivity;
import com.ilhamb.quickcam.R;
import com.ilhamb.quickcam.database.Job;
import com.ilhamb.quickcam.fragment.ListModalDialog;
import com.ilhamb.quickcam.utilities.TODO;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    List<Job> arrayList;
    Context context;
    FragmentManager fragmentManager;

    public ListViewAdapter(Context context, FragmentManager fm, List<Job> arrayList) {

        this.arrayList = arrayList;
        this.context = context;
        this.fragmentManager = fm;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.custom_listview, null);
            viewHolder = new ViewHolder(convertView);

            viewHolder.jobView.setText(arrayList.get(position).value);
            viewHolder.prexView.setVisibility(View.GONE);
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.jobView.setText(arrayList.get(position).value);
        viewHolder.prexView.setVisibility(View.GONE);

        if (jobManager.folpos == position) {

            String prefix = jobManager.prefixList.size() > 0 ? jobManager.prefixList.get(jobManager.prepos).value : null;
            prefix = prefix != null ? prefix : "null";

            viewHolder.jobView.setTextColor(context.getResources().getColor(R.color.light_blue));
            viewHolder.prexView.setVisibility(View.VISIBLE);
            viewHolder.prexView.setText(prefix);
        }

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.deleteJob(arrayList.get(position));
            }
        });
        viewHolder.prexView.setOnClickListener(new View.OnClickListener() {
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

                        viewHolder.prexView.setText(data);
                    }
                });
                fragment.show(fragmentManager, "HALLO");
            }
        });

        return convertView;
    }

    private class ViewHolder {

        TextView jobView;
        TextView prexView;
        TextView delete;

        public ViewHolder(View view) {

            jobView = (TextView) view.findViewById(R.id._job);
            prexView = (TextView) view.findViewById(R.id._prefix);
            delete = (TextView) view.findViewById(R.id.delete);
        }
    }


}

