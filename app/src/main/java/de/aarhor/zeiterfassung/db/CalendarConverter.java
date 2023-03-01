package de.aarhor.zeiterfassung.db;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarConverter {
    // ISO-8601 Format für Datum und Uhrzeit
    private final static String _ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm";
    // Converter
    private final static DateFormat _ISO_8601_FORMATTER = new SimpleDateFormat(_ISO_8601_PATTERN, Locale.GERMANY);

    @TypeConverter
    public static String toString(Calendar date) {
        if (date == null) {
            return null;
        }

        return _ISO_8601_FORMATTER.format(date.getTime());
    }

    @TypeConverter
    public static Calendar toCalendar(String stringDateTime) {
        if (stringDateTime == null || stringDateTime.isEmpty()) {
            return null;
        }

        try {
            Calendar dateTime = Calendar.getInstance();
            Date dateFromDb = null;
            dateFromDb = _ISO_8601_FORMATTER.parse(stringDateTime);
            dateTime.setTime(dateFromDb);
            return dateTime;
        } catch (ParseException e) {
            // Fehler bei Konvertierung
            e.printStackTrace();
        }

        // Falsches Format in der Datenbank
        return null;
    }
}