package com.promeets.android.adapter;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.activity.ExpertDetailActivity;
import android.content.Context;

import android.content.Intent;
import com.promeets.android.object.ExpertCardPOJO;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import com.promeets.android.util.GlideUtils;
import android.util.Log;
import com.promeets.android.util.MixpanelUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;


/**
 * Created by sosasang on 5/2/17.
 */

public class RecycleExpertAdapter extends RecyclerView.Adapter<RecycleExpertAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<ExpertCardPOJO> mExpCards;

    int width;

    public RecycleExpertAdapter(Context context, ArrayList<ExpertCardPOJO> cardItems) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mExpCards = cardItems;

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        ((BaseActivity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        width = ((BaseActivity)mContext).getWidth() - ScreenUtil.convertDpToPx(32, mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.
                inflate(R.layout.recycle_expert_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExpertCardPOJO mCard = mExpCards.get(position);
        //holder.mImgView.getLayoutParams().height = (int) (0.58 * width);
        holder.mImgView.getLayoutParams().height = width / 3 * 2;
        holder.mImgView.requestLayout();

        if (!StringUtils.isEmpty(mCard.getPhotoUrl())) {
            //holder.mImgView.setScaleType(ImageView.ScaleType.FIT_START);

            if (!GlideUtils.haveCache(mContext, mCard.getPhotoUrl())) {
                GlideUtils.cacheImage(mCard.getPhotoUrl(), mContext);
                Log.d("CacheExpert", "Caching...");
            }

            if (!StringUtils.isEmpty(GlideUtils.getCache(mContext, mCard.getPhotoUrl()))) {
                Log.d("CacheExpert", "use cache");
                Glide.with(mContext).load(GlideUtils.getCache(mContext, mCard.getPhotoUrl())).into(holder.mImgView);
            } else {
                Log.d("CacheExpert", "no cache");
                Glide.with(mContext).load(mCard.getPhotoUrl()).into(holder.mImgView);
            }

            holder.mSmallImgView.setVisibility(View.GONE);
        } else if (!StringUtils.isEmpty(mCard.getSmallphotoUrl())) {
            //holder.mImgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.mLayTrans.setVisibility(View.VISIBLE);
            holder.mSmallImgView.setVisibility(View.VISIBLE);

            if (!GlideUtils.haveCache(mContext, mCard.getSmallphotoUrl())) {
                GlideUtils.cacheImage(mCard.getSmallphotoUrl(), mContext);
                Log.d("CacheExpert", "Caching...");
            }

            if (!StringUtils.isEmpty(GlideUtils.getCache(mContext, mCard.getSmallphotoUrl()))) {
                Log.d("CacheExpert", "use cache");
                Glide.with(mContext).load(GlideUtils.getCache(mContext, mCard.getSmallphotoUrl()))
                        .bitmapTransform(new BlurTransformation(mContext, 40)).into(holder.mImgView);
                Glide.with(mContext).load(GlideUtils.getCache(mContext, mCard.getSmallphotoUrl())).into(holder.mSmallImgView);
            } else {
                Log.d("CacheExpert", "no cache");
                Glide.with(mContext).load(mCard.getSmallphotoUrl())
                        .bitmapTransform(new BlurTransformation(mContext, 40)).into(holder.mImgView);
                Glide.with(mContext).load(mCard.getSmallphotoUrl()).into(holder.mSmallImgView);
            }
        }
        holder.mTxtExpName.setText(mCard.getFullName());
        if (!StringUtils.isEmpty(mCard.getPosition()))
            holder.mTxtExpTitle.setText(mCard.getPosition().replace(", ", " | "));
        holder.mTxtTopic.setText(mCard.getTitle());
    }

    @Override
    public int getItemCount() {
        return mExpCards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImgView;
        View mLayTrans;
        CircleImageView mSmallImgView;
        TextView mTxtExpName;
        TextView mTxtExpTitle;
        TextView mTxtTopic;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgView = (ImageView) itemView.findViewById(R.id.imageView);
            mLayTrans = itemView.findViewById(R.id.trans_layer);
            mSmallImgView = (CircleImageView) itemView.findViewById(R.id.small_imageView);
            mTxtExpName = (TextView) itemView.findViewById(R.id.expert_name);
            mTxtExpTitle = (TextView) itemView.findViewById(R.id.expert_title);
            mTxtTopic = (TextView) itemView.findViewById(R.id.topic);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            HashMap<String, String> map = new HashMap<>();
            map.put("expertId", mExpCards.get(position).getExpId());
            if (position <= 4)
                MixpanelUtil.getInstance((BaseActivity)mContext).trackEvent("Home page -> Click one of the expert(See more)", map);
            else
                MixpanelUtil.getInstance((BaseActivity)mContext).trackEvent("Home page -> Click one of the expert(top recommended)", map);

            Intent intent = new Intent(mContext, ExpertDetailActivity.class);
            intent.putExtra("expId",mExpCards.get(position).getExpId());
            if (GlideUtils.haveCache(mContext, mExpCards.get(position).getPhotoUrl())) {
                intent.putExtra("photoPath", GlideUtils.getCache(mContext, mExpCards.get(position).getPhotoUrl()));
            } else if (GlideUtils.haveCache(mContext, mExpCards.get(position).getSmallphotoUrl())) {
                intent.putExtra("smallPhotoPath", GlideUtils.getCache(mContext, mExpCards.get(position).getSmallphotoUrl()));
            }
            mContext.startActivity(intent);
            ((BaseActivity)mContext).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }
}
