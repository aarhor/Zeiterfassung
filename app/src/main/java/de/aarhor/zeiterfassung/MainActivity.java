package de.aarhor.zeiterfassung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.aarhor.zeiterfassung.db.WorkTime;
import de.aarhor.zeiterfassung.db.WorkTimeDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText _startDateTime;    // "textBox" Startzeit
    private EditText _endDateTime;      // "textBox" Endzeit
    private EditText _Comment;          // "textBox" Bemerkung
    private Button _startCommand;       // Button Start
    private Button _endCommand;         // Button Ende
    private DateFormat _dateFormatter;
    private DateFormat _timeFormatter;
    private double Arbeitszeit;
    private double MehrMinder_Stunden;

    @SuppressLint("SimpleDateFormat")
    public String getCurrentTimestamp(int Auswahl) {
        String Datum = "";
        switch (Auswahl) {
            case 1:     // Komplettes Datum (Für die Anzeige)
                Datum = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar
                        .getInstance().getTime());
                break;
            case 2:     // Nur das Datum
                Datum = new SimpleDateFormat("yyyy-MM-dd").format(Calendar
                        .getInstance().getTime());
                break;
            case 3:     // Nur die aktuelle Uhrzeit
                Datum = new SimpleDateFormat("HH:mm:ss").format(Calendar
                        .getInstance().getTime());
                break;
        }
        return Datum;
    }

    private TimeTrackingApp getApp() {
        return (TimeTrackingApp) getApplication();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initFromDb();
    }

    @SuppressLint("SetTextI18n")
    private void initFromDb() {
        // Deaktivieren der beiden Buttons
        _startCommand.setEnabled(false);
        _endCommand.setEnabled(false);

        // Laden eines offenes Datensatzes
        getApp().getExecutors().diskIO().execute(() -> {
            WorkTime openWorkTime = getApp().getDb().workTimeDato().getOpened();
            if (openWorkTime == null) {
                // Keine offenen Datensätze
                getApp().getExecutors().mainThread().execute(() -> {
                    _startDateTime.setText("");
                    _endDateTime.setText("");
                    _startCommand.setEnabled(true);
                    _Comment.setEnabled(false);
                    _Comment.setText("");
                });
            } else {
                DatumKonverter Konverter = new DatumKonverter();
                String Deutsches_Datum = "";

                try {
                    Deutsches_Datum = Konverter.englisch_deutsch_Datum(openWorkTime.Datum);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                // Offener Datensatz
                String finalDeutsches_Datum = Deutsches_Datum;
                getApp().getExecutors().mainThread().execute(() -> {
                    _startDateTime.setText(finalDeutsches_Datum + " " + openWorkTime.startTime);
                    _endDateTime.setText("");
                    _endCommand.setEnabled(true);
                    _Comment.setEnabled(true);
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _startDateTime = findViewById(R.id.edtText_StartTime);
        _endDateTime = findViewById(R.id.edtText_EndTime);
        _startCommand = findViewById(R.id.btn_StartCommand);
        _endCommand = findViewById(R.id.btn_EndCommand);
        _Comment = findViewById(R.id.edtText_Comment);

        //Initialisierung Datum / Uhrzeit Formatierung
        _dateFormatter = android.text.format.DateFormat.getDateFormat(this);
        _timeFormatter = android.text.format.DateFormat.getTimeFormat(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _startCommand.setOnClickListener(onStartClick());
        _endCommand.setOnClickListener(onEndClick());
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Listener deregistrieren
        _startCommand.setOnClickListener(null);
        _endCommand.setOnClickListener(null);
    }

    private String formatForUI(Calendar currentTime) {
        return String.format(
                "%s %s", // String für Formatierung
                _dateFormatter.format(currentTime.getTime()), // Datum formatiert
                _timeFormatter.format(currentTime.getTime()) // Zeit formatiert
        );
    }

    private View.OnClickListener onStartClick() {
        return v -> {
            String Meldung = "Die Start Zeit wurde eingetragen.";

            //In Datenbank speichern
            getApp().getExecutors().diskIO().execute(() -> {
                WorkTime workTime = new WorkTime();
                workTime.Datum = getCurrentTimestamp(2);
                workTime.startTime = getCurrentTimestamp(3);
                getApp().getDb().workTimeDato().add(workTime);
            });
            WorkTimeDatabase db = Room.databaseBuilder(
                    MainActivity.this,  // Android Context
                    WorkTimeDatabase.class,    // Datentyp der Datenbank
                    "worktime_data.db"         // Name der Datenbank
            ).build();

            //Datumsausgabe für UI
            _startDateTime.setText(getCurrentTimestamp(1));

            // Buttons umschalten
            _startCommand.setEnabled(false);
            _endCommand.setEnabled(true);
            _Comment.setEnabled(true);

            //Toast
            Toast.makeText(MainActivity.this,   //Android Context
                            Meldung,    //Toast-Nachricht aus der Variable "Meldung"
                            Toast.LENGTH_LONG)  //Toast Länge
                    .show();    //Toast anzeigen
        };
    }

    @SuppressLint("SetTextI18n")
    private View.OnClickListener onEndClick() {
        return v -> {
            String Meldung = "Die End Zeit wurde eingetragen.";

            //In Datenbank speichern
            String CurrentTime = getCurrentTimestamp(3);
            final Double[] Differenz = {0.0};

            getApp().getExecutors().diskIO().execute(() -> {
                        WorkTime startedWorkTime = getApp().getDb().workTimeDato().getOpened();
                        if (startedWorkTime == null) {
                            // Keinen Datensatz mit fehlendem Ende gefunden
                            getApp().getExecutors().mainThread()
                                    .execute(() -> _endDateTime.setText(R.string.NoEmptyStartTime));
                        } else {
                            startedWorkTime.endTime = CurrentTime;

                            if (_Comment.getText() == null)
                                startedWorkTime.Bemerkung = null;
                            else
                                startedWorkTime.Bemerkung = _Comment.getText().toString();

                            try {
                                Differenz[0] = (get_Differenz(startedWorkTime.startTime, startedWorkTime.endTime));
                                // Differenz[0] = (get_Differenz("08:00:00", "16:30:00"));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            startedWorkTime.Differenz = Differenz[0];

                            getApp().getDb().workTimeDato().update(startedWorkTime);
                            getApp().getExecutors().mainThread()
                                    .execute(() -> {
                                        _endDateTime.setText(getCurrentTimestamp(1));
                                        _Comment.setText(Differenz[0].toString());
                                    });
                        }
                    }
            );

            // Buttons umschalten
            _startCommand.setEnabled(true);
            _endCommand.setEnabled(false);
            _Comment.setEnabled(false);

            // Toast
            Toast.makeText(MainActivity.this,   //Android Context
                            Meldung,    //Toast-Nachricht aus der Variable "Meldung"
                            Toast.LENGTH_LONG)  //Toast Länge
                    .show();    //Toast anzeigen
        };
    }

    public Double get_Differenz(String startZeit, String endZeit) throws ParseException {
        // Erstellen Sie ein SimpleDateFormat-Objekt, um das Format der Uhrzeit zu definieren
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        // Erstellen Sie zwei Calendar-Objekte und setzen Sie sie auf die gewünschten Uhrzeiten
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(Objects.requireNonNull(sdf.parse(startZeit)));
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(Objects.requireNonNull(sdf.parse(endZeit)));

        // Berechnen Sie die Differenz zwischen den beiden Uhrzeiten in Millisekunden
        double diffMillis = (double) (calendar2.getTimeInMillis() - calendar1.getTimeInMillis());

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

    public Double get_MehrMinderStunden() {
        return 0.0;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MenuItemListData:
                // Impliziter Intent
                // Intent googleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.de"));
                // startActivity(googleIntent);
                // Expliziter Intent
                Intent listDataIntent = new Intent(
                        this, // Context für Klassenkontext
                        ListDataActivity.class); // Activity-Klasse
                startActivity(listDataIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }
}