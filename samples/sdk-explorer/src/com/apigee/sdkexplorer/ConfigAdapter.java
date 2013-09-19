package com.apigee.sdkexplorer;

import java.util.List;

import com.apigee.sdkexplorer.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ConfigAdapter extends BaseAdapter {
    private Context context;

    private List<NameDescription> listEntries;

    public ConfigAdapter(Context context, List<NameDescription> listEntries) {
        this.context = context;
        this.listEntries = listEntries;
    }

    @Override
	public int getCount() {
        return listEntries.size();
    }

    @Override
	public Object getItem(int position) {
        return listEntries.get(position);
    }

    @Override
	public long getItemId(int position) {
        return position;
    }

    @Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
        NameDescription entry = listEntries.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.config_row_layout, null);
        }
        
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        tvName.setText(entry.getName());

        TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
        tvDescription.setText(entry.getDescription());
        
        return convertView;
    }

}