package com.promeets.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.promeets.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is for choosing availability for expert
 *
 * Mon - Sun    AM / PM
 *
 * @source: ExpertAvailActivity
 *
 *
 */

public class AvailabilityTimePickerActivity extends BaseActivity {

    @BindView(R.id.mon_am)
    TextView monAm;
    @BindView(R.id.mon_pm)
    TextView monPm;
    @BindView(R.id.tue_am)
    TextView tueAm;
    @BindView(R.id.tue_pm)
    TextView tuePm;
    @BindView(R.id.wed_am)
    TextView wedAm;
    @BindView(R.id.wed_pm)
    TextView wedPm;
    @BindView(R.id.thu_am)
    TextView thuAm;
    @BindView(R.id.thu_pm)
    TextView thuPm;
    @BindView(R.id.fri_am)
    TextView friAm;
    @BindView(R.id.fri_pm)
    TextView friPm;
    @BindView(R.id.sat_am)
    TextView satAm;
    @BindView(R.id.sat_pm)
    TextView satPm;
    @BindView(R.id.sun_am)
    TextView sunAm;
    @BindView(R.id.sun_pm)
    TextView sunPm;
    @BindView(R.id.time_picker_submit)
    TextView btn_submit;

    /**
     * 14 digit bit number
     *
     * from MonAm - SunPm
     */
    int data = 0;
    @BindView(R.id.mon_am_lay)
    FrameLayout monAmLay;
    @BindView(R.id.mon_pm_lay)
    FrameLayout monPmLay;
    @BindView(R.id.tue_am_lay)
    FrameLayout tueAmLay;
    @BindView(R.id.tue_pm_lay)
    FrameLayout tuePmLay;
    @BindView(R.id.wed_am_lay)
    FrameLayout wedAmLay;
    @BindView(R.id.wed_pm_lay)
    FrameLayout wedPmLay;
    @BindView(R.id.thu_am_lay)
    FrameLayout thuAmLay;
    @BindView(R.id.thu_pm_lay)
    FrameLayout thuPmLay;
    @BindView(R.id.fri_am_lay)
    FrameLayout friAmLay;
    @BindView(R.id.fri_pm_lay)
    FrameLayout friPmLay;
    @BindView(R.id.sat_am_lay)
    FrameLayout satAmLay;
    @BindView(R.id.sat_pm_lay)
    FrameLayout satPmLay;
    @BindView(R.id.sun_am_lay)
    FrameLayout sunAmLay;
    @BindView(R.id.sun_pm_lay)
    FrameLayout sunPmLay;

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("defaultDate", data);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_time_picker);
        ButterKnife.bind(this);
        // init time picker
        data = getIntent().getIntExtra("defaultDate", 0);
        for (int i = 0; i < 14; i++) {
            if ((data & (1 << i)) > 0) {
                switch (i) {
                    // Monday
                    case 0:
                        monAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        monAm.setText("");
                        break;
                    case 1:
                        monPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        monPm.setText("");
                        break;
                    // Tuesday
                    case 2:
                        tueAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        tueAm.setText("");
                        break;
                    case 3:
                        tuePm.setBackgroundResource(R.drawable.ic_nike_primary);
                        tuePm.setText("");
                        break;
                    // Wednesday
                    case 4:
                        wedAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        wedAm.setText("");
                        break;
                    case 5:
                        wedPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        wedPm.setText("");
                        break;
                    // Thursday
                    case 6:
                        thuAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        thuAm.setText("");
                        break;
                    case 7:
                        thuPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        thuPm.setText("");
                        break;
                    // Friday
                    case 8:
                        friAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        friAm.setText("");
                        break;
                    case 9:
                        friPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        friPm.setText("");
                        break;
                    // Saturday
                    case 10:
                        satAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        satAm.setText("");
                        break;
                    case 11:
                        satPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        satPm.setText("");
                        break;
                    // Sunday
                    case 12:
                        sunAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        sunAm.setText("");
                        break;
                    case 13:
                        sunPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        sunPm.setText("");
                        break;
                }
            } else {
                switch (i) {
                    // Monday
                    case 0:
                        monAm.setBackground(null);
                        monAm.setText("AM");
                        break;
                    case 1:
                        monPm.setBackground(null);
                        monPm.setText("PM");
                        break;
                    // Tuesday
                    case 2:
                        tueAm.setBackground(null);
                        tueAm.setText("AM");
                        break;
                    case 3:
                        tuePm.setBackground(null);
                        tuePm.setText("PM");
                        break;
                    // Wednesday
                    case 4:
                        wedAm.setBackground(null);
                        wedAm.setText("AM");
                        break;
                    case 5:
                        wedPm.setBackground(null);
                        wedPm.setText("PM");
                        break;
                    // Thursday
                    case 6:
                        thuAm.setBackground(null);
                        thuAm.setText("AM");
                        break;
                    case 7:
                        thuPm.setBackground(null);
                        thuPm.setText("PM");
                        break;
                    // Friday
                    case 8:
                        friAm.setBackground(null);
                        friAm.setText("AM");
                        break;
                    case 9:
                        friPm.setBackground(null);
                        friPm.setText("PM");
                        break;
                    // Saturday
                    case 10:
                        satAm.setBackground(null);
                        satAm.setText("AM");
                        break;
                    case 11:
                        satPm.setBackground(null);
                        satPm.setText("PM");
                        break;
                    // Sunday
                    case 12:
                        sunAm.setBackground(null);
                        sunAm.setText("AM");
                        break;
                    case 13:
                        sunPm.setBackground(null);
                        sunPm.setText("PM");
                        break;
                }
            }

            // Monday
            monAmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 0);
                    if ((data & (1 << 0)) > 0) {
                        monAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        monAm.setText("");
                    } else {
                        monAm.setBackground(null);
                        monAm.setText("AM");
                    }
                }
            });
            monPmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 1);
                    if ((data & (1 << 1)) > 0) {
                        monPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        monPm.setText("");
                    } else {
                        monPm.setBackground(null);
                        monPm.setText("PM");
                    }
                }
            });
            // Tuesday
            tueAmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 2);
                    if ((data & (1 << 2)) > 0) {
                        tueAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        tueAm.setText("");
                    } else {
                        tueAm.setBackground(null);
                        tueAm.setText("AM");
                    }
                }
            });
            tuePmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 3);
                    if ((data & (1 << 3)) > 0) {
                        tuePm.setBackgroundResource(R.drawable.ic_nike_primary);
                        tuePm.setText("");
                    } else {
                        tuePm.setBackground(null);
                        tuePm.setText("PM");
                    }
                }
            });
            // Wednesday
            wedAmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 4);
                    if ((data & (1 << 4)) > 0) {
                        wedAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        wedAm.setText("");
                    } else {
                        wedAm.setBackground(null);
                        wedAm.setText("AM");
                    }
                }
            });
            wedPmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 5);
                    if ((data & (1 << 5)) > 0) {
                        wedPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        wedPm.setText("");
                    } else {
                        wedPm.setBackground(null);
                        wedPm.setText("PM");
                    }
                }
            });
            // Thursday
            thuAmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 6);
                    if ((data & (1 << 6)) > 0) {
                        thuAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        thuAm.setText("");
                    } else {
                        thuAm.setBackground(null);
                        thuAm.setText("AM");
                    }
                }
            });
            thuPmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 7);
                    if ((data & (1 << 7)) > 0) {
                        thuPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        thuPm.setText("");
                    } else {
                        thuPm.setBackground(null);
                        thuPm.setText("PM");
                    }
                }
            });
            // Friday
            friAmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 8);
                    if ((data & (1 << 8)) > 0) {
                        friAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        friAm.setText("");
                    } else {
                        friAm.setBackground(null);
                        friAm.setText("AM");
                    }
                }
            });
            friPmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 9);
                    if ((data & (1 << 9)) > 0) {
                        friPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        friPm.setText("");
                    } else {
                        friPm.setBackground(null);
                        friPm.setText("PM");
                    }

                }
            });
            // Saturday
            satAmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 10);
                    if ((data & (1 << 10)) > 0) {
                        satAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        satAm.setText("");
                    } else {
                        satAm.setBackground(null);
                        satAm.setText("AM");
                    }
                }
            });
            satPmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 11);
                    if ((data & (1 << 11)) > 0) {
                        satPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        satPm.setText("");
                    } else {
                        satPm.setBackground(null);
                        satPm.setText("PM");
                    }
                }
            });
            // Sunday
            sunAmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 12);
                    if ((data & (1 << 12)) > 0) {
                        sunAm.setBackgroundResource(R.drawable.ic_nike_primary);
                        sunAm.setText("");
                    } else {
                        sunAm.setBackground(null);
                        sunAm.setText("AM");
                    }
                }
            });
            sunPmLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data = data ^ (1 << 13);
                    if ((data & (1 << 13)) > 0) {
                        sunPm.setBackgroundResource(R.drawable.ic_nike_primary);
                        sunPm.setText("");
                    } else {
                        sunPm.setBackground(null);
                        sunPm.setText("PM");
                    }
                }
            });
        }
    }
}
