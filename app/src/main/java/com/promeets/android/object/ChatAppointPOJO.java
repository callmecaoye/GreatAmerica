package com.promeets.android.object;

import java.util.ArrayList;

/**
 * Created by sosasang on 11/30/17.
 */

public class ChatAppointPOJO {
    public int eventId;
    public EventData eventRequest;
    public String serviceTitle;
    public ArrayList<EventLocationPOJO> eventLocationList = new ArrayList<>();
    public ArrayList<EventTimePOJO> eventDateList = new ArrayList<>();
}
