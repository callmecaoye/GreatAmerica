package com.promeets.android.activity;

import com.promeets.android.adapter.CreditCardAdapter;
import com.promeets.android.api.EventApi;
import com.promeets.android.api.PaymentApi;
import com.promeets.android.api.URL;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.promeets.android.custom.NoScrollListView;
import com.promeets.android.custom.PromeetsDialog;
import com.promeets.android.fragment.PaymentConfirmationPopupFragment;
import android.graphics.Paint;
import com.promeets.android.Constant;
import com.promeets.android.object.CreditCard;
import com.promeets.android.object.EventLocationPOJO;
import com.promeets.android.object.EventTimePOJO;
import android.os.Bundle;
import com.promeets.android.pojo.BaseResp;
import com.promeets.android.pojo.PromoResp;
import com.promeets.android.pojo.SuperResp;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import com.promeets.android.util.UserInfoHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.promeets.android.pojo.CreditCardResp;
import com.promeets.android.util.NumberFormatUtil;
import com.promeets.android.util.Utility;

import com.promeets.android.R;
import com.promeets.android.util.ScreenUtil;
import com.promeets.android.util.ServiceResponseHolder;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This Activity is for Payment detail during user payment activity
 *
 * Factors may influence the the final amount:
 * 1. topic price (could be free topic)
 * 2. use balance
 * 3. promo code (check validation)
 * 4. credit card (check validation)
 *
 * Within 1 or more factors, when final amount = 0, we enable the proceed button
 *
 * Show payment detail before actually charged PaymentConfirmationPopupFragment
 *
 * @source: AppoointStatusActivity
 *
 * @desination: AddCreditCardActivity
 *
 */

public class PaymentActivity extends BaseActivity
        implements PaymentConfirmationPopupFragment.OnConfirmClickListener {

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    // status
    @BindView(R.id.primary_line)
    View primaryLine;
    @BindView(R.id.gray_line)
    View grayLine;
    @BindView(R.id.text1)
    TextView step_text_1;
    @BindView(R.id.text2)
    TextView step_text_2;
    @BindView(R.id.circle1)
    View step_circle_1;
    @BindView(R.id.circle2)
    View step_circle_2;

    // expert service
    @BindView(R.id.photo)
    CircleImageView mImgPhoto;
    @BindView(R.id.topic)
    TextView mTxtTopic;
    @BindView(R.id.exp_name)
    TextView mTxtExpName;
    @BindView(R.id.position)
    TextView mTxtPosition;
    @BindView(R.id.price)
    TextView mTxtPrice;
    @BindView(R.id.orig_price)
    TextView mTxtOrgPrice;

    // Order Summary
    @BindView(R.id.total_txt)
    public TextView mTxtTotal;
    @BindView(R.id.total_amount)
    public TextView mTxtTotalAmount;
    @BindView(R.id.balance_checkbox)
    CheckBox mChkboxBalance;
    @BindView(R.id.balance_amount)
    public TextView mTxtBalanceAmount;
    @BindView(R.id.balance_amount_lay)
    FrameLayout mLayBalance;
    @BindView(R.id.deduct_amount)
    public TextView mTxtDedAmount;
    @BindView(R.id.final_amount)
    public TextView mTxtFinalAmount;


    //@BindView(R.id.activity_all_reviews_recyclerview)
    //RecyclerView rv_cardList;
    @BindView(R.id.card_list)
    NoScrollListView mLVCard;
    @BindView(R.id.activity_checkout_add_card)
    LinearLayout add_card_btn;
    @BindView(R.id.layout_root)
    LinearLayout mLayRoot;
    @BindView(R.id.proceed)
    TextView mTxtProceed;
    //@BindView(R.id.activity_checkout_pay)
    //Button mTxtProceed;

    @BindView(R.id.promo_code)
    EditText mTxtCode;
    @BindView(R.id.promo_code_btn)
    TextView mBtnCheckCode;
    @BindView(R.id.invalid_code_txt)
    TextView mTxtInvalidCode;

    @BindView(R.id.payment_lay)
    View mLayPayment;

    @BindView(R.id.cancel_code)
    TextView mTxtCancelCode;



    private ArrayList<CreditCard> cardList = new ArrayList<>();
    private CreditCardAdapter adapter;

    String servicePhotoUrl;
    String serviceTopic;
    String author;
    String position;
    String durationTime;
    Double servicePrice;
    Double origPrice;

    int userId;
    int expId;
    String serviceId;
    int eventRequestId;

    JSONObject timeJson, locationJson;
    EventTimePOJO time;
    EventLocationPOJO location;

    ProgressDialog progressDialog;

    String chargeTip;
    String validCode;
    Double userActiveNeedPay;
    Double userActiveNeedPayWithCode;
    Double originPriceWithCode;
    Double balance;
    Double promoAmount;

    boolean hasBalance;
    boolean useValidCode;
    boolean useBalance;
    boolean isPayable;
    boolean hasValidCard;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        mLayRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtTotalAmount.requestFocus();
                hideSoftKeyboard();
            }
        });

        add_card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, AddCreditCardActivity.class);
                startActivityForResult(intent, 220);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mTxtProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getCount() > 0) {
                    mTxtProceed.setEnabled(false);
                    int mSelect = adapter.getSelected();
                    popupConfirmationDialog(mSelect);
                } else {
                    mTxtProceed.setEnabled(false);
                    popupConfirmationDialog(-1);
                }
            }
        });

        // promo code
        mBtnCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                mTxtTotalAmount.requestFocus();
                if (StringUtils.isEmpty(mTxtCode.getText().toString().trim())) return;
                validatePromoCode(mTxtCode.getText().toString().trim());

            }
        });

        adapter.setOnItemCheckListener(new CreditCardAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(int position) {
                if (position != adapter.getSelected()) {
                    popupValidationDialog(position);
                    // update card validate status
                    for (CreditCard card : cardList) {
                        if (card.isValidated) {
                            card.setValidated(false);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    // update card validate status
                    for (CreditCard card : cardList) {
                        if (card.isValidated) {
                            card.setValidated(false);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    // update mSelect
                    adapter.updateSelected(-1);

                    hasValidCard = false;
                    calculateAmount();
                }
            }
        });
        adapter.setOnItemDeleteListener(new CreditCardAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int position) {
                popupDeleteCardDialog(position);
            }
        });

        mTxtCancelCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validCode = "";
                useValidCode = false;
                mTxtCode.setText("");

                /*if ((mTxtTotalAmount.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG)
                    mTxtTotalAmount.setPaintFlags(mTxtTotalAmount.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);*/

                calculateAmount();

                //mLayDedAmount.setVisibility(View.GONE);
                /*if (!hasBalance) {
                    //mLineDedAmount.setVisibility(View.GONE);
                    mTxtFinalAmount.setVisibility(View.GONE);
                }*/
                mTxtInvalidCode.setVisibility(View.GONE);
                //mTxtValidCode.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        //AndroidBug5497Workaround.assistActivity(this);

        initStatus();

        //init view
        Bundle bundle = getIntent().getExtras();
        servicePhotoUrl = bundle.getString("servicePhotoUrl");
        serviceTopic = bundle.getString("ServiceTopic");
        author = bundle.getString("author");
        position = bundle.getString("position");
        durationTime = bundle.getString("durationTime");
        servicePrice = StringUtils.isEmpty(bundle.getString("ServicePrice")) ? 0 : Double.parseDouble(bundle.getString("ServicePrice"));
        origPrice = StringUtils.isEmpty(bundle.getString("origPrice")) ? 0 : Double.parseDouble(bundle.getString("origPrice"));
        expId = bundle.getInt("expId");
        serviceId = bundle.getString("serviceId");
        eventRequestId = bundle.getInt("eventRequestId");
        //timeArray = bundle.getStringArray("timeArray");
        //locationArray = bundle.getStringArray("locationArray");
        time = (EventTimePOJO) bundle.getSerializable("time");
        location = (EventLocationPOJO) bundle.getSerializable("location");
        userActiveNeedPay = StringUtils.isEmpty(bundle.getString("userActiveNeedPay")) ? 0 : Double.parseDouble(bundle.getString("userActiveNeedPay"));
        chargeTip = bundle.getString("chargeTip");
        balance = StringUtils.isEmpty(bundle.getString("balance")) ? 0 : Double.parseDouble(bundle.getString("balance"));

        // expert service
        if (!StringUtils.isEmpty(servicePhotoUrl))
            Glide.with(this).load(servicePhotoUrl).into(mImgPhoto);
        mTxtTopic.setText(serviceTopic);
        mTxtExpName.setText(author);
        mTxtPrice.setText("$" + NumberFormatUtil.getInstance().getCurrency(servicePrice));
        if (origPrice > 0) {
            mTxtOrgPrice.setText("$" + NumberFormatUtil.getInstance().getCurrency(origPrice));
            mTxtOrgPrice.setPaintFlags(mTxtOrgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        mTxtPosition.setText(position);

        // Order summary
        double hour = Integer.valueOf(durationTime) / 60.0;
        DecimalFormat df = new DecimalFormat("#.#");
        if (hour <= 1)
            mTxtTotal.setText("$" + NumberFormatUtil.getInstance().getCurrency(servicePrice) + " / " + df.format(hour) + " hour");
        else
            mTxtTotal.setText("$" + NumberFormatUtil.getInstance().getCurrency(servicePrice) + " / " + df.format(hour) + " hours");
        mTxtTotalAmount.setText("$" + NumberFormatUtil.getInstance().getCurrency(userActiveNeedPay));
        if (servicePrice == 0) {
            isPayable = true;
            mTxtProceed.setBackground(getResources().getDrawable(R.drawable.btn_solid_primary));
            mTxtProceed.setEnabled(true);
            mLayPayment.setVisibility(View.GONE);
        }
        mTxtFinalAmount.setText("$" + NumberFormatUtil.getInstance().getCurrency(userActiveNeedPay));

        UserInfoHelper helper = new UserInfoHelper(this);
        userId = helper.getUserObject().id;
        fetchCardList();

        // setup listview
        adapter = new CreditCardAdapter(this, cardList);
        mLVCard.setAdapter(adapter);

        // balance
        if (balance > 0) {
            hasBalance = true;
            mTxtBalanceAmount.setText("$ " + NumberFormatUtil.getInstance().getCurrency(balance));
            mLayBalance.setVisibility(View.VISIBLE);
            //mLineDedAmount.setVisibility(View.VISIBLE);
            mTxtFinalAmount.setText("$ " + NumberFormatUtil.getInstance().getCurrency(servicePrice));
            mTxtFinalAmount.setVisibility(View.VISIBLE);
        }
        mChkboxBalance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) useBalance = true;
                else useBalance = false;
                calculateAmount();
            }
        });
    }

    private void initStatus() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 2.5f);
        primaryLine.setLayoutParams(param);
        param = new LinearLayout.LayoutParams(0, ScreenUtil.convertDpToPx(2, this), 3.5f);
        grayLine.setLayoutParams(param);
        step_text_1.setTextColor(getResources().getColor(R.color.primary));
        step_circle_1.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
        step_text_2.setTextColor(getResources().getColor(R.color.primary));
        step_circle_2.setBackground(getResources().getDrawable(R.drawable.circle_solid_primary));
    }

    /**
     * Return from AddCreditCard activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 220) {
            if (resultCode == 222) {
                fetchCardList();
            }
        }
    }

    private void fetchCardList() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        cardList.clear();

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("userpayment/retrieveCustomer"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PaymentApi payment = retrofit.create(PaymentApi.class);
        Call<CreditCardResp> call = payment.fetchCreditCard(userId);

        call.enqueue(new Callback<CreditCardResp>() {
            @Override
            public void onResponse(Call<CreditCardResp> call, Response<CreditCardResp> response) {
                PromeetsDialog.hideProgress();
                CreditCardResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(PaymentActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    if (result.cardList != null && result.cardList.size() > 0) {
                        cardList.addAll(result.cardList);
                        adapter.notifyDataSetChanged();
                    }
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(PaymentActivity.this, result.info.code);
                } else
                    PromeetsDialog.show(PaymentActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<CreditCardResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(PaymentActivity.this, t.getLocalizedMessage());
            }
        });
    }

    /**
     * Credit card validation
     *
     * @param position
     */
    private void popupValidationDialog(final int position) {
        final CreditCard mCard = cardList.get(position);

        View dialog_view = LayoutInflater.from(this).inflate(R.layout.dialog_card_validation, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialog_view);

        final TextView msg = (TextView) dialog_view.findViewById(R.id.dialog_card_validation_message);
        final EditText card_date = (EditText) dialog_view.findViewById(R.id.dialog_card_expire_date);

        card_date.setHint("MM/YY");
        card_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
                    s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
                }
            }
        });

        msg.setText("Enter expire date for card : * " + mCard.getLast4());
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mTxtTotalAmount.requestFocus();
                        hideSoftKeyboard();
                        // decode input date
                        String result = card_date.getText().toString();
                        if (result.length() > 0) {
                            String[] date = result.split("/");
                            String expMonth = date[0];
                            String expYear = date[1];

                            if (mCard.getExpMonth().equals(expMonth) &&
                                    (mCard.getExpYear().equals("20" + expYear))) {
                                // update mSelect
                                adapter.updateSelected(position);
                                // update validation status
                                mCard.setValidated(true);
                                ///btn_shadow_top
                                adapter.notifyDataSetChanged();

                                hasValidCard = true;
                                calculateAmount();
                                //mTxtProceed.setBackground(getResources().getDrawable(R.drawable.btn_solid_primary));
                                //mTxtProceed.setEnabled(true);

                            } else {

                                mTxtProceed.setBackground(getResources().getDrawable(R.drawable.btn_solid_grey));
                                mTxtProceed.setEnabled(false);

                                PromeetsDialog.show(PaymentActivity.this, "Please enter a valid expiry date");
                                hasValidCard = false;
                                calculateAmount();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mTxtTotalAmount.requestFocus();
                                hideSoftKeyboard();
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void popupDeleteCardDialog(final int position) {
        CreditCard mCard = cardList.get(position);
        PromeetsDialog.show(this, "Are you sure to delete card: * " + mCard.getLast4() + "?", new PromeetsDialog.OnSubmitListener() {
            @Override
            public void onSubmitListener() {
                deleteCreditCard(position);
            }
        });
    }

    private void popupConfirmationDialog(int position) {
        progressDialog = new ProgressDialog(this);
        //R.style.AppTheme_Dark_Dialog);

        FragmentManager fm = getSupportFragmentManager();
        final PaymentConfirmationPopupFragment fragment =
                PaymentConfirmationPopupFragment.newInstance(useBalance, useValidCode, position);
        fragment.show(fm, "fragment_payment_confirmation");
        mTxtProceed.setEnabled(true);
    }

    /**
     * callback method from PaymentConfirmationPopupFragment
     */
    @Override
    public void OnConfirmClick(int position) {
        progressDialog = new ProgressDialog(this);
        //R.style.AppTheme_Dark_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing payment...");
        progressDialog.show();

        if (isPayable)
            makePayment(null);
        else {
            CreditCard card = cardList.get(position);
            makePayment(card.getCardId());
        }
    }

    /**
     * Swipe to delete credit card
     *
     * @param position
     */
    private void deleteCreditCard(final int position) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        CreditCard mCard = cardList.get(position);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("userpayment/deleteCard"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JSONObject body = new JSONObject();
        try {
            body.put("userId", userId);
            body.put("stripeToken", mCard.cardId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body.toString());
        PaymentApi service = retrofit.create(PaymentApi.class);

        Call<BaseResp> call = service.deleteCard(requestBody);//get request, need to be post!

        call.enqueue(new Callback<BaseResp>() {
            @Override
            public void onResponse(Call<BaseResp> call, Response<BaseResp> response) {
                BaseResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(PaymentActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    if (position == adapter.getSelected()) {
                        mTxtProceed.setEnabled(false);
                        mTxtProceed.setBackground(getResources().getDrawable(R.drawable.btn_solid_grey));
                    }
                    cardList.remove(position);
                    adapter.notifyDataSetChanged();
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(PaymentActivity.this, result.info.code);
                } else
                    PromeetsDialog.show(PaymentActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<BaseResp> call, Throwable t) {

            }
        });
    }

    /**
     * Payment charge card implementation
     *
     * @param cardId
     */
    private void makePayment(String cardId) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            progressDialog.dismiss();
            return;
        }
        paymentTimeSelect(time); // update timeJson
        paymentLocationSelect(location); //update locationJson

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("eventrequest/userPaymentAll"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JSONObject body = new JSONObject();
        try {
            if (!isPayable && !StringUtils.isEmpty(cardId)) body.put("stripeToken", cardId);
            body.put("eventRequestId", eventRequestId);
            body.put("eventDate", timeJson);
            body.put("eventLocation", locationJson);
            if (useBalance) body.put("useBalance", 1);
            else body.put("useBalance", 0);
            if (useValidCode) body.put("promotionCode", validCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body.toString());
        EventApi service = retrofit.create(EventApi.class);
        Call<SuperResp> call = service.userPaymentAll(requestBody);//get request, need to be post!

        call.enqueue(new Callback<SuperResp>() {
            @Override
            public void onResponse(Call<SuperResp> call, Response<SuperResp> response) {
                progressDialog.dismiss();
                SuperResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(PaymentActivity.this, response.errorBody().toString());
                    return;
                }
                if (isSuccess(result.info.code)) {
                    // send result to EventProcessStatus activity
                    updateStatus();
                } else if (result.info.code.equals(Constant.RELOGIN_ERROR_CODE)
                        || result.info.code.equals(Constant.UPDATE_TIME_STAMP)
                        || result.info.code.equals(Constant.UPDATE_THE_APPLICATION)) {
                    Utility.onServerHeaderIssue(PaymentActivity.this, result.info.code);
                } else
                    PromeetsDialog.show(PaymentActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<SuperResp> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void paymentTimeSelect(EventTimePOJO pojo) {
        timeJson = new JSONObject();
        try {
            timeJson.put("detailDay", pojo.detailDay);
            timeJson.put("beginHourOfDay", pojo.beginHourOfDay);
            timeJson.put("endHourOfDay", pojo.endHourOfDay);
            timeJson.put("timeZone", pojo.timeZone);
        } catch (Exception e) {
            timeJson = null;
            e.printStackTrace();
        }
    }

    private void paymentLocationSelect(EventLocationPOJO pojo) {
        locationJson = new JSONObject();
        try {
            locationJson.put("status", pojo.status);
            locationJson.put("location", pojo.location);
            locationJson.put("latitude", pojo.latitude);
            locationJson.put("longitude", pojo.longitude);
        } catch (Exception e) {
            locationJson = null;
            e.printStackTrace();
        }
    }

    /**
     * send result to AppointStatusActivity
     */
    private void updateStatus() {
        PromeetsDialog.show(this, "Thank you for your payment.", new PromeetsDialog.OnOKListener() {
            @Override
            public void onOKListener() {
                Intent returnIntent = new Intent();
                setResult(111, returnIntent);
                finish();
            }
        });
    }

    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    /**
     * Promo code validation
     *
     * @param code
     */
    private void validatePromoCode(final String code) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("promotion/checkCode"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PaymentApi payment = retrofit.create(PaymentApi.class);
        Call<PromoResp> call = payment.checkPromoCode(code, eventRequestId);

        call.enqueue(new Callback<PromoResp>() {
            @Override
            public void onResponse(Call<PromoResp> call, Response<PromoResp> response) {
                PromoResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(PaymentActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    useValidCode = true;
                    validCode = code;
                    //mTxtValidCode.setVisibility(View.VISIBLE);
                    mTxtInvalidCode.setVisibility(View.GONE);


                    //mTxtTotalAmount.setPaintFlags(mTxtTotalAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    //mLayDedAmount.setVisibility(View.VISIBLE);
                    //mLineDedAmount.setVisibility(View.VISIBLE);
                    //mTxtFinalAmount.setVisibility(View.VISIBLE);

                    originPriceWithCode = StringUtils.isEmpty(result.getServicePrice()) ? 0 : Double.parseDouble(result.getServicePrice());
                    userActiveNeedPayWithCode = StringUtils.isEmpty(result.getUserActiveNeedPay()) ? 0 : Double.parseDouble(result.getUserActiveNeedPay());
                    promoAmount = StringUtils.isEmpty(result.getPromotionAmount()) ? 0 : Double.parseDouble(result.getPromotionAmount());

                    calculateAmount();

                } else if (result.info.code.equals("300")) {
                    calculateAmount();
                    mTxtInvalidCode.setVisibility(View.VISIBLE);
                } else {
                    PromeetsDialog.show(PaymentActivity.this, result.info.description);
                }
            }

            @Override
            public void onFailure(Call<PromoResp> call, Throwable t) {
            }
        });
    }

    /**
     * Update price amounts and proceed button enable
     * according to useBalance/userValidCode
     */
    private void calculateAmount() {
        Double amount;
        if (useValidCode) {
            amount = userActiveNeedPayWithCode;
            mTxtTotalAmount.setText("$" + NumberFormatUtil.getInstance().getCurrency(originPriceWithCode));
            mTxtDedAmount.setText("-$" + NumberFormatUtil.getInstance().getCurrency(promoAmount));
            mTxtDedAmount.setTextColor(getResources().getColor(R.color.primary));
        } else {
            amount = userActiveNeedPay;
            mTxtDedAmount.setText("$0.00");
            mTxtDedAmount.setTextColor(getResources().getColor(R.color.pm_dark));
        }

        if (useBalance) {
            Double num = balance > amount ? amount : balance;
            mTxtBalanceAmount.setText("-$" + NumberFormatUtil.getInstance().getCurrency(num));
            mTxtBalanceAmount.setTextColor(getResources().getColor(R.color.primary));
            mTxtFinalAmount.setText("$" + NumberFormatUtil.getInstance().getCurrency(amount - num));
        } else {
            mTxtBalanceAmount.setText("$" + NumberFormatUtil.getInstance().getCurrency(balance));
            mTxtBalanceAmount.setTextColor(getResources().getColor(R.color.pm_gray));
            mTxtFinalAmount.setText("$" + NumberFormatUtil.getInstance().getCurrency(amount));
        }

        if (amount == 0 || // (free service || with promo code)
                (amount > 0 && useBalance && balance >= amount)) {
            isPayable = true;
            mTxtProceed.setBackground(getResources().getDrawable(R.drawable.btn_solid_primary));
            mTxtProceed.setEnabled(true);
        } else {
            isPayable = false;
            if (hasValidCard) {
                mTxtProceed.setBackground(getResources().getDrawable(R.drawable.btn_solid_primary));
                mTxtProceed.setEnabled(true);
            } else {
                mTxtProceed.setBackground(getResources().getDrawable(R.drawable.btn_solid_grey));
                mTxtProceed.setEnabled(false);
            }
        }
    }
}