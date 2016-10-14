package com.tk.mediapicker.photopicker.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by TK on 2016/9/27.
 */

public final class DateUtils {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");

    /**
     * 得到解析时间
     *
     * @param date
     * @return
     */
    public static final String getDateStr(long date) {
        Date nowDate = new Date();
        Date calDate = new Date(date * 1000);
        Calendar[] calendars = new Calendar[2];
        calendars[0] = Calendar.getInstance();
        calendars[1] = Calendar.getInstance();
        //计算时间
        calendars[0].setTime(calDate);
        //最新时间
        calendars[1].setTime(nowDate);
        if (calendars[0].get(Calendar.YEAR) != calendars[1].get(Calendar.YEAR)) {
            //不同年
            StringBuilder sb = new StringBuilder();
            sb.append(calendars[0].get(Calendar.YEAR));
            sb.append("/");
            sb.append(calendars[0].get(Calendar.MONTH) + 1);
            return sb.toString();
        } else {
            if (calendars[0].get(Calendar.MONTH) != calendars[1].get(Calendar.MONTH)) {
                //不同月
                StringBuilder sb = new StringBuilder();
                sb.append(calendars[0].get(Calendar.YEAR));
                sb.append("/");
                sb.append(calendars[0].get(Calendar.MONTH) + 1);
                clear(calendars);
                return sb.toString();
            } else {
                if (calendars[0].get(Calendar.WEEK_OF_MONTH) == calendars[1].get(Calendar.WEEK_OF_MONTH)) {
                    if (calendars[0].get(Calendar.DAY_OF_WEEK) == calendars[1].get(Calendar.DAY_OF_WEEK)) {
                        clear(calendars);
                        return "今天";
                    } else {
                        clear(calendars);
                        return "本周";
                    }
                } else {
                    clear(calendars);
                    return "本月";
                }
            }
        }

    }

    private static final void clear(Calendar[] calendars) {
        for (Calendar calendar : calendars) {
            calendar.clear();
            calendar = null;
        }
    }
}
