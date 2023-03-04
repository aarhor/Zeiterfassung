package de.aarhor.zeiterfassung;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatumKonverter {
    public String englisch_deutsch_Datum(String englischesDatum) throws ParseException {
        @SuppressLint("SimpleDateFormat") DateFormat englischesFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = englischesFormat.parse(englischesDatum);
        @SuppressLint("SimpleDateFormat") DateFormat deutschesFormat = new SimpleDateFormat("dd.MM.yyyy");
        String deutschesDatum = deutschesFormat.format(date);

        return  deutschesDatum;
    }
}
