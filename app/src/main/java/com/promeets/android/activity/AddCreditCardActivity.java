package com.promeets.android.activity;

import com.promeets.android.api.PaymentApi;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promeets.android.api.URL;
import com.promeets.android.custom.PromeetsDialog;
import com.google.gson.JsonObject;
import com.promeets.android.Constant;
import com.promeets.android.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.promeets.android.util.AndroidBug5497Workaround;
import com.promeets.android.util.ServiceResponseHolder;
import com.promeets.android.util.UserInfoHelper;
import com.promeets.android.util.Utility;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This activity is for adding credit card during user payment status
 *
 * We do some validations and dynamic match the card type image according to card number
 *
 * Steps to add credit card (integrated with Stripe):
 * 1. instantiate a Stripe Card class from user input
 * 2. call Stripe SDK to get token
 * 3. pass to token to our backend
 * 4. return intent to Payment activity and refresh the card list
 *
 * @source: PaymentActivity
 *
 */

public class AddCreditCardActivity extends BaseActivity {


    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = ' ';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;

    //private static final String PUBLISHABLE_KEY = "pk_live_opjHVERrGt94n1Md68r2yuAF";//"pk_test_ezfdqOFiuEg2IkmySMS5NrRW";
    private static final String PUBLISHABLE_KEY = "pk_test_ezfdqOFiuEg2IkmySMS5NrRW";
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.number)
    EditText card_no;
    @BindView(R.id.date)
    EditText card_expire_date;
    @BindView(R.id.cvv)
    EditText card_cvc;
    @BindView(R.id.save)
    TextView save_and_continue;
    @BindView(R.id.root_layout)
    LinearLayout ly_root;
    @BindView(R.id.tip_lay)
    FrameLayout mLayTip;
    @BindView(R.id.tips)
    TextView mTips;

    @BindView(R.id.error_name)
    TextView errorName;
    @BindView(R.id.error_number)
    TextView errorNumber;
    @BindView(R.id.error_date)
    TextView errorDate;
    @BindView(R.id.error_cvv)
    TextView errorCvv;

    private ArrayList<String> listOfPattern = new ArrayList<>();
    String userId;

    private Animation inAnimation;
    private Animation outAnimation;


    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        ly_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_and_continue.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        mTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly_root.callOnClick();
                mLayTip.startAnimation(inAnimation);
                mLayTip.setVisibility(View.VISIBLE);
            }
        });
        mLayTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayTip.startAnimation(outAnimation);
                mLayTip.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        AndroidBug5497Workaround.assistActivity(this);
        savePattern();
        ButterKnife.bind(this);

        inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_dialog);
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_dialog);

        UserInfoHelper helper = new UserInfoHelper(this);
        userId = helper.getUserObject().id.toString();

        save_and_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorName.setVisibility(View.GONE);
                errorNumber.setVisibility(View.GONE);
                errorDate.setVisibility(View.GONE);
                errorCvv.setVisibility(View.GONE);

                /**
                 * Check to ensure no-null information
                 */
                if (StringUtils.isEmpty(name.getText().toString().trim())) {
                    errorName.setVisibility(View.VISIBLE);
                } else if (StringUtils.isEmpty(card_no.getText().toString().trim())
                        || !isInputCorrect(card_no.getText(), CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
                    errorNumber.setVisibility(View.VISIBLE);
                } else if (StringUtils.isEmpty(card_expire_date.getText().toString().trim())
                        || card_expire_date.getText().toString().trim().length() < 5) {
                    errorDate.setVisibility(View.VISIBLE);
                } else if (StringUtils.isEmpty(card_cvc.getText().toString().trim())
                        || card_cvc.getText().toString().trim().length() < 3) {
                    errorCvv.setVisibility(View.VISIBLE);
                } else {
                    // decode card information
                    save_and_continue.setEnabled(false);
                    String cardNum = card_no.getText().toString().replace("-", "");
                    String[] expDate = card_expire_date.getText().toString().split("/");
                    Integer expMonth = Integer.parseInt(expDate[0]);
                    Integer expYear = Integer.parseInt("20" + expDate[1]);
                    String cvc = card_cvc.getText().toString();

                    Card card = new Card(cardNum, expMonth, expYear, cvc);

                    /**
                     * call stripe SDK to get token
                     */
                    try {
                        Stripe stripe = new Stripe(PUBLISHABLE_KEY);
                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        // Send token to server
                                        checkout(token.getId());
                                    }

                                    public void onError(Exception error) {
                                        // Show localized error message
                                        PromeetsDialog.show(AddCreditCardActivity.this, error.getLocalizedMessage());
                                        save_and_continue.setEnabled(true);
                                    }
                                }
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        save_and_continue.setEnabled(true);
                    }
                }
            }
        });
    }

    private void savePattern() {

        listOfPattern.clear();

        String ptVisa = "^4[0-9]{6,}$";
        listOfPattern.add(ptVisa);
        String ptMasterCard = "^5[1-5][0-9]{5,}$";
        listOfPattern.add(ptMasterCard);
        String ptAmeExp = "^3[47][0-9]{5,}$";
        listOfPattern.add(ptAmeExp);
        String ptDinClb = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$";
        listOfPattern.add(ptDinClb);
        String ptDiscover = "^6(?:011|5[0-9]{2})[0-9]{3,}$";
        listOfPattern.add(ptDiscover);
        String ptJcb = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$";
        listOfPattern.add(ptJcb);
    }

    /**
     * P
     * @param token_id
     */
    private void checkout(String token_id) {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL.HOST)
                    .client(ServiceResponseHolder.getInstance().getRetrofitHeader("userpayment/checkout"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PaymentApi payment = retrofit.create(PaymentApi.class);
            Call<JsonObject> call = payment.checkoutPayment(userId, token_id);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    PromeetsDialog.hideProgress();
                    JsonObject json = response.body();
                    if (json == null) {
                        PromeetsDialog.show(AddCreditCardActivity.this, response.errorBody().toString());
                        return;
                    }

                    String code = json.get("info").getAsJsonObject().get("code").getAsString();
                    if (code.equals("200")) {
                        // send result to Payment activity
                        updateStatus();
                    } else if (code.equals(Constant.RELOGIN_ERROR_CODE)
                            || code.equals(Constant.UPDATE_TIME_STAMP)
                            || code.equals(Constant.UPDATE_THE_APPLICATION)) {
                        Utility.onServerHeaderIssue(AddCreditCardActivity.this, code);
                    } else {
                        save_and_continue.setEnabled(true);
                        PromeetsDialog.show(AddCreditCardActivity.this, json.get("info").getAsJsonObject().get("description").getAsString());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    PromeetsDialog.hideProgress();
                    PromeetsDialog.show(AddCreditCardActivity.this, t.getLocalizedMessage());
                    save_and_continue.setEnabled(true);
                }
            });


    }

    /**
     * send result to Payment activity
     */
    private void updateStatus() {
        Intent returnIntent = new Intent();
        setResult(222, returnIntent);
        finish();
    }

    @OnTextChanged(value = R.id.number, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
        }
        Utility.setCardLogo(s.toString().replace(" ", ""), (ImageView) findViewById(R.id.card_image));
    }

    @OnTextChanged(value = R.id.date, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardDateTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.cvv, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardCVCTextChanged(Editable s) {
        if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
            s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
        }
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
}