package com.example.footplanner.utilts;

import android.annotation.SuppressLint;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}


