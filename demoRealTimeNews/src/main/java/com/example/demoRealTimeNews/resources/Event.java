package com.example.demoRealTimeNews.models;

import java.util.List;
import java.util.Map;

public class Event {
    private String EventName;
    private List<Map<String,String>> EventData;

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public List<Map<String, String>> getEventData() {
        return EventData;
    }

    public void setEventData(List<Map<String, String>> eventData) {
        EventData = eventData;
    }
}
