package tlu.cse.android.ht63.dogcareapp.utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Pref {
    public static String convertDateTime(long time) {
        Date date = new Date();
        date.setTime(time);
        if (time == 0) {
            return "Bấm để chọn";
        }
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        return format.format(date);
    }

    public static String convertDate(long time) {
        Date date = new Date();
        date.setTime(time);
        if (time == 0) {
            return "Bấm để chọn";
        }
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        return format.format(date);
    }
}
