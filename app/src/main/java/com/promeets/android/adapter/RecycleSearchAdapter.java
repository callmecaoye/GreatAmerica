package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.object.ExpertService;
import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ExpertService> listService;
    private BaseActivity mBaseActivity;
    private LayoutInflater mInflater;

    public RecycleSearchAdapter(BaseActivity baseActivity, ArrayList<ExpertService> results){
        listService = results;
        mBaseActivity = baseActivity;
        mInflater = LayoutInflater.from(mBaseActivity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cate_service_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExpertService service = listService.get(position);
        ItemViewHolder viewHolder = (ItemViewHolder) holder;

        viewHolder.title.setText(service.title);
        viewHolder.author.setText(service.expName);
        viewHolder.position.setText(service.description);

        if(!StringUtils.isEmpty(service.smallphotoUrl))
            Glide.with(mBaseActivity).load(service.smallphotoUrl).into(viewHolder.profile);
    }

    @Override
    public int getItemCount() {
        if (listService == null
                || listService.isEmpty()) return 0;
        return listService.size();
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public void refresh(ArrayList<ExpertService> items) {
        this.listService = items;
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title,author,price,position;
        CircleImageView profile;

        public ItemViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.list_search_service_title);
            author = (TextView) view.findViewById(R.id.list_search_service_author);
            price = (TextView) view.findViewById(R.id.list_search_service_price);
            position = (TextView) view.findViewById(R.id.list_search_service_position);
            profile = (CircleImageView) view.findViewById(R.id.list_search_service_image);
        }
    }
}
