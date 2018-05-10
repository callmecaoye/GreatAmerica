package com.promeets.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import com.promeets.android.pojo.CalendarEventResp;
import android.view.View;
import android.widget.TextView;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.promeets.android.api.ExpertActionApi;
import com.promeets.android.api.URL;
import com.promeets.android.custom.PromeetsDialog;
import com.google.gson.Gson;
import com.promeets.android.object.CalendarEvent;
import com.promeets.android.R;
import com.promeets.android.util.ServiceResponseHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is for showing custom calendar view synchronized with Google/Windows calendar events
 *
 * @source: ExpertAvailActivity(expert preview), EventAcceptFragment(expert accept),
 * UserRequestSettingActivity(user request)
 */

public class CalendarViewActivity extends BaseActivity implements
        View.OnClickListener,
        MonthLoader.MonthChangeListener,
        WeekView.EventClickListener,
        WeekView.EmptyViewClickListener {

    static final String TRANS_PRIMARY = "#99FF8D59";
    static final String TRANS_GRAY = "#33979797";

    @BindView(R.id.weekView)
    WeekView mWeekView;
    //@BindView(R.id.today)
    //TextView mTxtToday;
    @BindView(R.id.save)
    TextView mBtnSave;

    private List<WeekViewEvent> mAllEvents = new ArrayList<>();
    private List<WeekViewEvent> mReqEvents = new ArrayList<>();
    private ArrayList<String> mWVEventStrList;
    //private int id;
    private int duratingTime;
    private String expId;
    private boolean isPreview = false;
    private int availType;

    private boolean isSingleSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);
        ButterKnife.bind(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
        mWeekView.goToHour(8);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            expId = extras.getString("expId");
            duratingTime = (int) extras.getFloat("duratingTime");
            mWVEventStrList = extras.getStringArrayList("eventsToCalendar");
            isPreview = extras.getBoolean("isPreview");
            availType = extras.getInt("type");
        }

        /**
         * WeekViewEvents initialization
         */
        if (mWVEventStrList != null && mWVEventStrList.size() > 0) {
            Gson gson = new Gson();
            for (String mStrEvent : mWVEventStrList) {
                WeekViewEvent mEvent = gson.fromJson(mStrEvent, WeekViewEvent.class);
                mEvent.setColor(Color.parseColor(TRANS_PRIMARY));
                mEvent.setId(mEvent.getStartTime().getTimeInMillis());
                mReqEvents.add(mEvent);
                mAllEvents.add(mEvent);
                mWeekView.notifyDatasetChanged();
            }
        }

        /**
         * availType:
         * 0: AM / PM
         * 1: Calendar events
         * 2: Not set
         */
        switch (availType) {
            case 0:
                if (extras != null) {
                    int defaultDate = extras.getInt("defaultDate");
                    decodeDate(defaultDate);
                }
                break;
            case 1:
                fetchCalendarEvent();
                break;
            case 2:
                break;
        }

    }

    @Override
    public void initElement() {
        isSingleSelect = getIntent().getBooleanExtra("isSingleSelect", false);
    }

    /**
     * The week view has infinite scrolling horizontally. We have to provide the events of a month every time the month changes on the week view.
     */
    @Override
    public void registerListeners() {
        mWeekView.setMonthChangeListener(this);

        if (isPreview) mBtnSave.setVisibility(View.GONE);
        else {
            mBtnSave.setOnClickListener(this);
            mWeekView.setEmptyViewClickListener(this);
            mWeekView.setOnEventClickListener(this);
        }
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour == 12 ? "12 PM" : (hour - 12) + " PM") : (hour == 0 ? "0 AM" : hour + " AM");
            }
        });
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> matchedEvents = new ArrayList<>();
        if (mAllEvents == null || mAllEvents.size() == 0)
            return matchedEvents;

        for (WeekViewEvent event : mAllEvents) {
            if (eventMatches(event, newYear, newMonth)) {
                matchedEvents.add(event);
            }
        }
        return matchedEvents;
    }

    @Override
    public void onEmptyViewClicked(Calendar startTime) {
        // check if it is past time
        Calendar now = Calendar.getInstance();
        if (startTime.getTimeInMillis() - now.getTimeInMillis() <= 0) {
            PromeetsDialog.show(this, "Cannot select past time");
            return;
        }

        // time unit : 30 min
        if (startTime.get(Calendar.MINUTE) >= 30)
            startTime.set(Calendar.MINUTE, 30);
        else
            startTime.set(Calendar.MINUTE, 0);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.MINUTE, duratingTime);

        if (mAllEvents != null && mAllEvents.size() > 0) {
            for (WeekViewEvent event : mAllEvents) {
                // duplicate events
                if (event.getId() == startTime.getTimeInMillis()) {
                    return;
                }

                // requested time slot overlaps with existing event
                if (event.getColor() == Color.parseColor(TRANS_GRAY)
                        && event.getStartTime().getTimeInMillis() > startTime.getTimeInMillis()
                        && endTime.getTimeInMillis() > event.getStartTime().getTimeInMillis()) {
                    PromeetsDialog.show(this, "Time slot is not available", new PromeetsDialog.OnOKListener() {
                        @Override
                        public void onOKListener() {

                        }
                    });
                    return;
                }
            }
        }

        WeekViewEvent event = new WeekViewEvent(startTime.getTimeInMillis(), null, startTime, endTime);
        event.setColor(Color.parseColor(TRANS_PRIMARY));

        if (isSingleSelect) {
            mAllEvents.clear();
            mReqEvents.clear();
        }

        mAllEvents.add(event);
        mReqEvents.add(event);
        mWeekView.notifyDatasetChanged();
    }

    // sync with calendar
    private void fetchCalendarEvent() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("Calendar/loadData"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Call<CalendarEventResp> call = service.loadData(formatter.format(new Date()), 40, expId);
        call.enqueue(new Callback<CalendarEventResp>() {
            @Override
            public void onResponse(Call<CalendarEventResp> call, Response<CalendarEventResp> response) {
                PromeetsDialog.hideProgress();
                CalendarEventResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(CalendarViewActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    if (result.dataList != null && result.dataList.size() > 0) {
                        for (CalendarEvent pojo : result.dataList) {
                            Calendar startTime = Calendar.getInstance();
                            startTime.setTimeInMillis(pojo.beginTimeLong);
                            Calendar endTime = (Calendar) startTime.clone();
                            endTime.setTimeInMillis(pojo.endTimeLong);
                            WeekViewEvent event = new WeekViewEvent(startTime.getTimeInMillis(), null, startTime, endTime);
                            event.setColor(Color.parseColor(TRANS_GRAY));
                            mAllEvents.add(event);
                        }
                        mWeekView.notifyDatasetChanged();
                    }
                } else
                    PromeetsDialog.show(CalendarViewActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<CalendarEventResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(CalendarViewActivity.this, t.getLocalizedMessage());
            }
        });
    }

    private void decodeDate(int defaultDate) {
        for (int i = 0; i < 14; i++) {
            if ((defaultDate & (1 << i)) > 0) {
                switch (i) {
                    // Monday
                    case 0:
                        date2WeekViewEvent(Calendar.MONDAY, false);
                        break;
                    case 1:
                        date2WeekViewEvent(Calendar.MONDAY, true);
                        break;
                    // Tuesday
                    case 2:
                        date2WeekViewEvent(Calendar.TUESDAY, false);
                        break;
                    case 3:
                        date2WeekViewEvent(Calendar.TUESDAY, true);
                        break;
                    // Wednesday
                    case 4:
                        date2WeekViewEvent(Calendar.WEDNESDAY, false);
                        break;
                    case 5:
                        date2WeekViewEvent(Calendar.WEDNESDAY, true);
                        break;
                    // Thursday
                    case 6:
                        date2WeekViewEvent(Calendar.THURSDAY, false);
                        break;
                    case 7:
                        date2WeekViewEvent(Calendar.THURSDAY, true);
                        break;
                    // Friday
                    case 8:
                        date2WeekViewEvent(Calendar.FRIDAY, false);
                        break;
                    case 9:
                        date2WeekViewEvent(Calendar.FRIDAY, true);
                        break;
                    // Saturday
                    case 10:
                        date2WeekViewEvent(Calendar.SATURDAY, false);
                        break;
                    case 11:
                        date2WeekViewEvent(Calendar.SATURDAY, true);
                        break;
                    // Sunday
                    case 12:
                        date2WeekViewEvent(Calendar.SUNDAY, false);
                        break;
                    case 13:
                        date2WeekViewEvent(Calendar.SUNDAY, true);
                        break;
                }
            }
        }
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                Intent intent = new Intent();
                if (mReqEvents != null && mReqEvents.size() > 0) {
                    ArrayList<String> mStrEvents = new ArrayList<>();
                    Gson gson = new Gson();
                    for (WeekViewEvent event : mReqEvents) {
                        String eventStr = gson.toJson(event);
                        mStrEvents.add(eventStr);
                    }
                    intent.putStringArrayListExtra("eventsFromCalendar", mStrEvents);
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        if (mReqEvents.contains(event)) {
            mReqEvents.remove(event);
            mAllEvents.remove(event);
            mWeekView.notifyDatasetChanged();
        }
    }

    /**
     * Checks if an event falls into a specific year and month.
     * @param event The event to check for.
     * @param year The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    /**
     * create new WeekViewEvent
     * @param dow Day of Week
     * @param isPM
     * @return
     */
    private void date2WeekViewEvent(int dow, boolean isPM) {
        for (int j = 0; j < 10; j++) {
            Calendar startTime = Calendar.getInstance();
            startTime.add(Calendar.WEEK_OF_YEAR, j);
            startTime = nextDayOfWeek(startTime, dow, isPM);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 12);
            WeekViewEvent event = new WeekViewEvent(startTime.getTimeInMillis(), null, startTime, endTime);
            event.setColor(Color.parseColor(TRANS_GRAY));
            mAllEvents.add(event);
            //DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            //Log.d("caoye", formatter.format(startTime.getTime()));
        }
    }

    /**
     * get exact next coming day
     * @param curDate
     * @param dow
     * @param isPM
     * @return
     */
    private Calendar nextDayOfWeek(Calendar curDate, int dow, boolean isPM) {
        int diff = dow - curDate.get(Calendar.DAY_OF_WEEK);
        if (diff < 0) diff += 7;
        Calendar exactDate = (Calendar) curDate.clone();
        exactDate.add(Calendar.DAY_OF_MONTH, diff);
        if (!isPM)
            exactDate.set(Calendar.HOUR_OF_DAY, 0);
        else
            exactDate.set(Calendar.HOUR_OF_DAY, 12);
        exactDate.set(Calendar.MINUTE, 0);
        return exactDate;
    }
}

