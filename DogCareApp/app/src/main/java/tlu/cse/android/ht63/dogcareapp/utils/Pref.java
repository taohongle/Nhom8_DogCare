package tlu.cse.android.ht63.dogcareapp.utils;

import java.text.DateFormat;
import java.util.Date;

public class Pref {

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
