package backbencers.nub.dailycostcalc.constant;

import java.util.ArrayList;

/**
 * Created by dutchman on 5/20/17.
 */

public class CalendarCollection {
    public String date="";
    public String event_message="";

    public static ArrayList<CalendarCollection> date_collection_arr;
    public CalendarCollection(String date,String event_message){

        this.date=date;
        this.event_message=event_message;

    }

}