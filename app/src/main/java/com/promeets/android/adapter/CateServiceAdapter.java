package com.promeets.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.R;
import com.promeets.android.object.CustomList;
import com.promeets.android.activity.BaseActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CateServiceAdapter extends BaseAdapter {
    private BaseActivity mBaseActivity;
    private ArrayList<CustomList> list;
    private LayoutInflater inflater;

    public CateServiceAdapter(BaseActivity baseActivity, ArrayList<CustomList> list){
        this.list = list;
        this.mBaseActivity = baseActivity;
        inflater = mBaseActivity.getLayoutInflater();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CustomList getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cate_service_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.list_search_service_title);
            holder.author = (TextView) convertView.findViewById(R.id.list_search_service_author);
            holder.price = (TextView) convertView.findViewById(R.id.list_search_service_price);
            holder.position = (TextView) convertView.findViewById(R.id.list_search_service_position);
            holder.profile = (CircleImageView) convertView.findViewById(R.id.list_search_service_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CustomList customList = getItem(i);
        holder.title.setText(customList.getExpertService().title);
        holder.author.setText(customList.getExpertProfile().getFullName());
        String priceStr = "";
        try {
            double p = Double.valueOf(customList.getExpertService().price);
            priceStr = p % 1 == 0 ? String.valueOf((int) p) : String.valueOf(p);
        } catch (Exception e) {

        }
        if (priceStr.equals("0"))
            holder.price.setText("Free");
        else
            holder.price.setText("$" + priceStr);
        holder.position.setText(customList.getExpertProfile().getPosition());

        if(!StringUtils.isEmpty(customList.getExpertProfile().getSmallphotoUrl()))
            Glide.with(mBaseActivity).load(customList.getExpertProfile().getSmallphotoUrl()).into(holder.profile);

        return convertView;
    }

    public void refresh(ArrayList<CustomList> items) {
        this.list = items;
        notifyDataSetChanged();
    }

    final class ViewHolder {
        TextView title;
        TextView author;
        TextView price;
        TextView position;
        CircleImageView profile;
    }
}
