package com.promeets.android.adapter;

import com.promeets.android.object.CreditCard;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.promeets.android.activity.BaseActivity;
import com.promeets.android.R;

import java.util.ArrayList;

/**
 * Created by sosasang on 8/25/17.
 */

public class CreditCardAdapter extends BaseAdapter {

    private ArrayList<CreditCard> listService;

    private LayoutInflater mInflater;

    private BaseActivity mBaseActivity;

    private OnItemDeleteListener mDeleteListener;

    private OnItemCheckListener mCheckListener;

    private int mSelect = -1;

    public CreditCardAdapter(BaseActivity baseActivity, ArrayList<CreditCard> results) {
        this.mBaseActivity = baseActivity;
        this.listService = results;
        mInflater = LayoutInflater.from(mBaseActivity);
    }

    @Override
    public int getCount() {
        return listService.size();
    }

    @Override
    public Object getItem(int position) {
        return listService.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.credit_card_item, null);
            holder.mLayContent = convertView.findViewById(R.id.content_lay);
            holder.mImgCheck = (ImageView) convertView.findViewById(R.id.check);
            holder.mImgCard = (ImageView) convertView.findViewById(R.id.image);
            holder.mTxtNumber = (TextView) convertView.findViewById(R.id.number);
            holder.mTxtDelete = (TextView) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CreditCard mCard = listService.get(position);
        holder.mTxtNumber.setText("* " + mCard.getLast4());
        switch (mCard.getBrand()) {
            case "Visa":
                holder.mImgCard.setImageResource(R.drawable.card_visa);
                break;
            case "MasterCard":
                holder.mImgCard.setImageResource(R.drawable.card_mastercard);
                break;
            case "Discover":
                holder.mImgCard.setImageResource(R.drawable.card_discover);
                break;
            case "American Express":
                holder.mImgCard.setImageResource(R.drawable.card_amex);
                break;
            default:
                holder.mImgCard.setImageResource(R.drawable.card_default);
                break;
        }

        // listView reuse
        if (mSelect == position && mCard.isValidated) {
            //holder.tv_cardDate.setText(mCard.getExpMonth() + "/" + mCard.getExpYear());
            //holder.iv_cardSelect.setImageResource(R.drawable.ic_check);
            holder.mImgCheck.setImageResource(R.drawable.ic_nike_round);
        } else {
            holder.mImgCheck.setImageResource(R.drawable.ic_unchoose);
        }

        holder.mLayContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckListener != null)
                    mCheckListener.onItemCheck(position);
            }
        });
        holder.mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteListener != null)
                    mDeleteListener.onItemDelete(position);
            }
        });

        return convertView;
    }

    public int getSelected() { return mSelect; }

    public void updateSelected(int position){
        if(position != mSelect){
            mSelect = position;
            notifyDataSetChanged();
        }
    }

    public void setOnItemDeleteListener(OnItemDeleteListener mListener) {
        this.mDeleteListener = mListener;
    }

    public void setOnItemCheckListener(OnItemCheckListener mListener) {
        this.mCheckListener = mListener;
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public interface OnItemCheckListener {
        void onItemCheck(int position);
    }

    final class ViewHolder {
        View mLayContent;
        ImageView mImgCheck;
        ImageView mImgCard;
        TextView mTxtNumber;
        TextView mTxtDelete;
    }
}
