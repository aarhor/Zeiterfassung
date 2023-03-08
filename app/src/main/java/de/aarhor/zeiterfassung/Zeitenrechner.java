package de.aarhor.zeiterfassung;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Zeitenrechner {
    DatumKonverter Konverter = new DatumKonverter();
    public Double get_Differenz(String startZeit, String endZeit) throws ParseException {
        // Erstellen Sie ein SimpleDateFormat-Objekt, um das Format der Uhrzeit zu definieren
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        // Erstellen Sie zwei Calendar-Objekte und setzen Sie sie auf die gewünschten Uhrzeiten
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(Objects.requireNonNull(sdf.parse(startZeit)));
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(Objects.requireNonNull(sdf.parse(endZeit)));

        // Berechnen Sie die Differenz zwischen den beiden Uhrzeiten in Millisekunden
        double diffMillis;
        diffMillis = (double) (calendar2.getTimeInMillis() - calendar1.getTimeInMillis());

        // Konvertieren Sie die Differenz in Stunden, Minuten und Sekunden
        double diffSeconds = diffMillis / 1000;
        double diffMinutes = diffSeconds / 60;
        double diffHours = Math.floor(diffMinutes / 60);
        diffMinutes %= 60;

        double diffMinutes_decimal = (double) (diffMinutes / 60);
        double result = (double) (diffHours + diffMinutes_decimal);

        // Geben Sie die Differenz aus
        Log.d("Uhrzeit-Differenz", "Differenz Minuten : " + diffMinutes_decimal);
        Log.d("Uhrzeit-Differenz", "Differenz Stunden : " + diffHours);
        Log.d("Uhrzeit-Differenz", "Differenz Gesamt  : " + result);

        return result;
    }

    public Double get_MehrMinderStunden(String Tag, double Arbeitszeit, boolean Pause) {
        String dayOfWeekInGerman= Konverter.get_DayofWeekGerman(Tag);
        double MehrMinderStunden = 0.0;
        double Pausenzeit = 0.0;

        if (Pause) {
            Pausenzeit = 0.5;
        }

        if (Objects.equals(dayOfWeekInGerman, "Montag") ||
                Objects.equals(dayOfWeekInGerman, "Dienstag") ||
                Objects.equals(dayOfWeekInGerman, "Mittwoch") ||
                Objects.equals(dayOfWeekInGerman, "Donnerstag")) {
            MehrMinderStunden = Arbeitszeit - (8 + Pausenzeit);
        } else if (Objects.equals(dayOfWeekInGerman, "Freitag")) {
            MehrMinderStunden = Arbeitszeit - (5 + Pausenzeit);
        } else if (Objects.equals(dayOfWeekInGerman, "Samstag") ||
                Objects.equals(dayOfWeekInGerman, "Sonntag")) {
            MehrMinderStunden = Arbeitszeit - (0 + Pausenzeit);
        }
        Log.d("Uhrzeit-Differenz", dayOfWeekInGerman + " " + MehrMinderStunden);
        return MehrMinderStunden;
    }
}
