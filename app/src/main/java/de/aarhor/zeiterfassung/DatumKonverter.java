package de.aarhor.zeiterfassung;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class DatumKonverter {
    public String englisch_deutsch_Datum(String englischesDatum) throws ParseException {
        @SuppressLint("SimpleDateFormat") DateFormat englischesFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = englischesFormat.parse(englischesDatum);

        @SuppressLint("SimpleDateFormat") DateFormat deutschesFormat = new SimpleDateFormat("dd.MM.yyyy");
        String deutschesDatum = deutschesFormat.format(date);

        return deutschesDatum;
    }

    public String get_DayofWeekGerman(String Tag) {
        LocalDate date = LocalDate.parse(Tag, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.GERMAN);
    }
}
