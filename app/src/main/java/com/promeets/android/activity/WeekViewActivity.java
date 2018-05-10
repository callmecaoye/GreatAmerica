package com.promeets.android.activity;

import com.promeets.android.api.ExpertActionApi;
import com.promeets.android.api.URL;
import android.content.Intent;
import com.promeets.android.custom.PromeetsDialog;
import android.graphics.Color;
import android.graphics.RectF;
import com.promeets.android.object.CalendarEvent;
import android.os.Bundle;
import com.promeets.android.pojo.CalendarEventResp;
import android.view.View;
import android.widget.TextView;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;

import com.promeets.android.R;
import com.promeets.android.util.ServiceResponseHolder;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeekViewActivity extends BaseActivity implements
        View.OnClickListener,
        MonthLoader.MonthChangeListener,
        WeekView.EventClickListener,
        WeekView.EmptyViewClickListener {

    static final String TRANS_PRIMARY = "#99FF8D59";
    static final String TRANS_GRAY = "#33979797";

    static final int WORKSHOP = 0;
    static final int CALL = 1;

    @BindView(R.id.title)
    TextView mTxtTitle;
    @BindView(R.id.save)
    TextView mTxtSave;
    @BindView(R.id.weekView)
    WeekView mWeekView;

    private List<WeekViewEvent> mAllEvents = new ArrayList<>();
    private WeekViewEvent mReqEvent;
    private Gson gson = new Gson();
    private int what;


    @Override
    public void initElement() {
        String mStrEvent = getIntent().getStringExtra("eventToCalendar");
        if (!StringUtils.isEmpty(mStrEvent)) {
            Gson gson = new Gson();
            mReqEvent = gson.fromJson(mStrEvent, WeekViewEvent.class);
            mAllEvents.add(mReqEvent);
            mWeekView.notifyDatasetChanged();
        }

        what = getIntent().getIntExtra("what", -1);
        if (what == -1) finish();
        if (what == WORKSHOP) {
            mWeekView.goToHour(10);
            mTxtTitle.setText("Workshop");
        } else if (what == CALL) {
            mWeekView.goToHour(9);
            mTxtTitle.setText("On-Boarding Call");
        }

        fetchCalendarEvent();
    }

    /**
     * The week view has infinite scrolling horizontally. We have to provide the events of a month every time the month changes on the week view.
     */
    @Override
    public void registerListeners() {
        mWeekView.setMonthChangeListener(this);
        mTxtSave.setOnClickListener(this);
        mWeekView.setEmptyViewClickListener(this);
        mWeekView.setOnEventClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);
        ButterKnife.bind(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                Intent intent = new Intent();
                if (mReqEvent != null) {
                    String eventStr = gson.toJson(mReqEvent);
                    intent.putExtra("eventFromCalendar", eventStr);
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
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
        initOffHour(newYear, newMonth);


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
        if (what == WORKSHOP)
            endTime.add(Calendar.HOUR, 2);
        else if (what == CALL)
            endTime.add(Calendar.MINUTE, 30);

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
                    PromeetsDialog.show(this, "Time slot is not available");
                    return;
                }
            }
        }

        WeekViewEvent event = new WeekViewEvent(startTime.getTimeInMillis(), null, startTime, endTime);
        event.setColor(Color.parseColor(TRANS_PRIMARY));
        //mAllEvents.clear();
        mAllEvents.remove(mReqEvent);
        mAllEvents.add(event);
        mReqEvent = event;
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        if (mReqEvent == event) {
            mReqEvent = null;
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

    private void initOffHour(int year, int month) {
        int maxDate = getDaysByYearMonth(year, month);

        if (what == WORKSHOP) {
            for (int i = 1; i <= maxDate; i++) {
                Calendar start = Calendar.getInstance();
                start.set(year, month - 1, i, 0, 0, 0);
                Calendar end = (Calendar) start.clone();

                // WEEKDAY
                if (start.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                        || start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    end.add(Calendar.HOUR, 23);
                    end.add(Calendar.MINUTE, 59);
                    end.add(Calendar.SECOND, 59);
                    WeekViewEvent block = new WeekViewEvent(start.getTimeInMillis(), null, start, end);
                    block.setColor(Color.parseColor(TRANS_GRAY));
                    mAllEvents.add(block);
                    continue;
                }

                // WEEKEND
                end.add(Calendar.HOUR, 9);
                end.add(Calendar.MINUTE, 59);
                end.add(Calendar.SECOND, 59);
                WeekViewEvent block = new WeekViewEvent(start.getTimeInMillis(), null, start, end);
                block.setColor(Color.parseColor(TRANS_GRAY));
                mAllEvents.add(block);

                Calendar start2 = Calendar.getInstance();
                start2.set(year, month - 1, i, 20, 0, 0);
                Calendar end2 = (Calendar) start2.clone();
                end2.add(Calendar.HOUR, 4);
                block = new WeekViewEvent(start2.getTimeInMillis(), null, start2, end2);
                block.setColor(Color.parseColor(TRANS_GRAY));
                mAllEvents.add(block);
            }
        } else if (what == CALL) {
            for (int i = 1; i <= maxDate; i++) {
                Calendar start = Calendar.getInstance();
                start.set(year, month - 1, i, 0, 0, 0);
                Calendar end = (Calendar) start.clone();

                // WEEKDAY
                if (start.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                        || start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    end.add(Calendar.HOUR, 23);
                    end.add(Calendar.MINUTE, 59);
                    end.add(Calendar.SECOND, 59);
                    WeekViewEvent block = new WeekViewEvent(start.getTimeInMillis(), null, start, end);
                    block.setColor(Color.parseColor(TRANS_GRAY));
                    mAllEvents.add(block);
                    continue;
                }

                // WEEKEND
                end.add(Calendar.HOUR, 8);
                end.add(Calendar.MINUTE, 59);
                end.add(Calendar.SECOND, 59);
                WeekViewEvent block = new WeekViewEvent(start.getTimeInMillis(), null, start, end);
                block.setColor(Color.parseColor(TRANS_GRAY));
                mAllEvents.add(block);

                Calendar start2 = Calendar.getInstance();
                start2.set(year, month - 1, i, 16, 0, 0);
                Calendar end2 = (Calendar) start2.clone();
                end2.add(Calendar.HOUR, 8);
                block = new WeekViewEvent(start2.getTimeInMillis(), null, start2, end2);
                block.setColor(Color.parseColor(TRANS_GRAY));
                mAllEvents.add(block);
            }
        }
        mWeekView.notifyDatasetChanged();
    }

    /**
     * 根据年 月 获取对应的月份 天数
     * */
    private int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * Sync with Community Manager calendar
     */
    private void fetchCalendarEvent() {
        if (!hasInternetConnection()) {
            PromeetsDialog.show(this, getString(R.string.no_internet));
            return;
        }

        PromeetsDialog.showProgress(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL.HOST)
                .client(ServiceResponseHolder.getInstance().getRetrofitHeader("Calendar/loadAdminData"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ExpertActionApi service = retrofit.create(ExpertActionApi.class);
        String type = null;
        if (what == WORKSHOP)
            type = "workshop";
        else if (what == CALL)
            type = "call";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Call<CalendarEventResp> call = service.loadAdminData(formatter.format(new Date()), 40, type, TimeZone.getDefault().getID());
        call.enqueue(new Callback<CalendarEventResp>() {
            @Override
            public void onResponse(Call<CalendarEventResp> call, Response<CalendarEventResp> response) {
                PromeetsDialog.hideProgress();
                CalendarEventResp result = response.body();
                if (result == null) {
                    PromeetsDialog.show(WeekViewActivity.this, response.errorBody().toString());
                    return;
                }

                if (isSuccess(result.info.code)) {
                    if (result.dataList != null && result.dataList.size() > 0) {
                        for (CalendarEvent pojo : result.dataList) {
                            Calendar startTime = Calendar.getInstance();
                            startTime.setTimeInMillis(pojo.beginTimeLong);

                            // WEEKEND
                            if (startTime.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                                    || startTime.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                                continue;

                            // WEEKDAY
                            if (what == WORKSHOP
                                    && (startTime.get(Calendar.HOUR) < 10
                                    || startTime.get(Calendar.HOUR) > 20))
                                continue;
                            else if (what == CALL
                                    && (startTime.get(Calendar.HOUR) < 9
                                    || startTime.get(Calendar.HOUR) > 16))
                                continue;



                            Calendar endTime = (Calendar) startTime.clone();
                            endTime.setTimeInMillis(pojo.endTimeLong);
                            WeekViewEvent event = new WeekViewEvent(startTime.getTimeInMillis(), null, startTime, endTime);
                            event.setColor(Color.parseColor(TRANS_GRAY));
                            mAllEvents.add(event);
                        }
                        mWeekView.notifyDatasetChanged();
                    }
                } else
                    PromeetsDialog.show(WeekViewActivity.this, result.info.description);
            }

            @Override
            public void onFailure(Call<CalendarEventResp> call, Throwable t) {
                PromeetsDialog.hideProgress();
                PromeetsDialog.show(WeekViewActivity.this, t.getLocalizedMessage());
            }
        });
    }
}
