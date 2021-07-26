package com.monir.expensetracker.util;

import java.util.List;

public class CalendarCollection {
  public static List<CalendarCollection> calendarCollections;
  private String date;
  private String eventMessage;

  public CalendarCollection(String date, String eventMessage) {
    this.date = date;
    this.eventMessage = eventMessage;
  }

  public String getDate() {
    return this.date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getEventMessage() {
    return eventMessage;
  }

  public void setEventMessage(String eventMessage) {
    this.eventMessage = eventMessage;
  }
}