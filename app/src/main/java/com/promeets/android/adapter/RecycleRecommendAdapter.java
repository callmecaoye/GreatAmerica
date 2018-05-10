package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.ExpertDetailActivity;
import android.content.Intent;
import com.promeets.android.custom.CustomFlexBox;
import android.graphics.Typeface;
import com.promeets.android.object.ExpertService;
import android.support.v7.widget.RecyclerView;
import com.promeets.android.util.MixpanelUtil;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.promeets.android.util.ScreenUtil;

import com.promeets.android.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shashank Shekhar on 02-02-2017.
 */

public class RecycleRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ExpertService> listService;
    private LayoutInflater mInflater;
    private BaseActivity mBaseActivity;
    Typeface tf_semi;

    public RecycleRecommendAdapter(BaseActivity baseActivity, ArrayList<ExpertService> results) {
        listService = results;
        this.mBaseActivity = baseActivity;
        mInflater = LayoutInflater.from(mBaseActivity);
        tf_semi = Typeface.createFromAsset(mBaseActivity.getAssets(), "fonts/OpenSans-SemiBold.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycle_recommend_item, parent, false);
        return new RecycleRecommendAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ExpertService recommend = listService.get(position);
            RecycleRecommendAdapter.ViewHolder viewHolder = (RecycleRecommendAdapter.ViewHolder) holder;
            if (position == listService.size() - 1) viewHolder.divider.setVisibility(View.GONE);

            //viewHolder.mTxtTitle.setText(service.getTitle());
            viewHolder.mTxtName.setText(recommend.fullName);
            viewHolder.mTxtPosition.setText(recommend.position);
            //viewHolder.mTxtPrice.setText("$ " + service.getPrice());
            viewHolder.photo.setImageDrawable(null);
            if (!StringUtils.isEmpty(recommend.photoUrl)) {
                Glide.with(viewHolder.photo.getContext()).load(recommend.photoUrl).into(viewHolder.photo);
            }else if (!StringUtils.isEmpty(recommend.smallphotoUrl)){
                Glide.with(viewHolder.photo.getContext()).load(recommend.smallphotoUrl).into(viewHolder.photo);
            }


            viewHolder.mFlexbox.removeAllViewsInLayout();
            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            int tmp1 = ScreenUtil.convertDpToPx(4, mBaseActivity);
            int tmp2 = ScreenUtil.convertDpToPx(5, mBaseActivity);
            lp.setMargins(tmp1, tmp2, tmp1, tmp2);
            if (recommend.tags != null && recommend.tags.length > 0) {
                for (String tag : recommend.tags) {
                    final TextView mTxtTag = new TextView(mBaseActivity);
                    mTxtTag.setLayoutParams(lp);
                    mTxtTag.setText(tag);
                    mTxtTag.setTextColor(mBaseActivity.getResources().getColor(R.color.primary));
                    mTxtTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    mTxtTag.setTypeface(tf_semi);
                    tmp1 = ScreenUtil.convertDpToPx(10, mBaseActivity);
                    tmp2 = ScreenUtil.convertDpToPx(5, mBaseActivity);
                    mTxtTag.setPadding(tmp1, tmp2, tmp1, tmp2);
                    mTxtTag.setBackgroundResource(R.drawable.tag_border_primary);
                    viewHolder.mFlexbox.addView(mTxtTag);
                /*mTxtTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = ((TextView) v).getText().toString().replace("#", "");
                        Intent intent = new Intent(mBaseActivity.this, HomeActivity.class);
                        HomeActivity.currentTabIndex = 2;
                        HomeActivity.mQuery = key;
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });*/
                }
            }
    }

    @Override
    public int getItemCount() {
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTxtName, mTxtPosition;
        CircleImageView photo;
        CustomFlexBox mFlexbox;
        View divider;

        public ViewHolder(View view) {
            super(view);
            //mTxtTitle = (TextView) view.findViewById(R.id.list_recommend_service_title);
            mTxtName = (TextView) view.findViewById(R.id.list_recommend_service_name);
            mTxtPosition = (TextView) view.findViewById(R.id.list_recommend_service_position);
            //mTxtPrice = (TextView) view.findViewById(R.id.list_recommend_service_price);
            photo = (CircleImageView) view.findViewById(R.id.list_recommend_service_image);
            mFlexbox = (CustomFlexBox) view.findViewById(R.id.flexBox);
            divider = view.findViewById(R.id.divider);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ExpertService service = listService.get(position);
            HashMap<String, String> map = new HashMap<>();
            map.put("expertId", service.expId.toString());
            MixpanelUtil.getInstance(mBaseActivity).trackEvent("Expert page -> Click one of recommended experts", map);

            Intent intent = new Intent(mBaseActivity, ExpertDetailActivity.class);
            intent.putExtra("expId", service.expId.toString());
            mBaseActivity.startActivity(intent);
            mBaseActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }
}
