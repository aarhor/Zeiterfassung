package de.aarhor.zeiterfassung;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.aarhor.zeiterfassung.db.WorkTime;
import de.aarhor.zeiterfassung.db.WorkTimeDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText _startDateTime;    //"textBox" Startzeit
    private EditText _endDateTime;      //"textBox" Endzeit
    private Button _startCommand;       //Button Start
    private Button _endCommand;         //Button Ende
    private DateFormat _dateFormatter;
    private DateFormat _timeFormatter;

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
                });
            } else {
                // Offener Datensatz
                getApp().getExecutors().mainThread().execute(() -> {
                    _startDateTime.setText(openWorkTime.startTime);
                    _endDateTime.setText("");
                    _endCommand.setEnabled(true);
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

        //Initialisierung Datum / Uhrzeit Formatierung
        _dateFormatter = android.text.format.DateFormat.getDateFormat(this);
        _timeFormatter = android.text.format.DateFormat.getTimeFormat(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _startCommand.setOnClickListener(view -> {
            String Meldung = "Die Start Zeit wurde eingetragen.";

            //Toast
            Toast.makeText(MainActivity.this,   //Android Context
                            Meldung,    //Toast-Nachricht aus der Variable "Meldung"
                            Toast.LENGTH_LONG)  //Toast Länge
                    .show();    //Toast anzeigen

            //In Datenbank speichern
            getApp().getExecutors().diskIO().execute(() -> {
                WorkTime workTime = new WorkTime();
                workTime.startTime = getCurrentTimestamp(1);
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
        });

        _endCommand.setOnClickListener(view -> {
            String Meldung = "Die End Zeit wurde eingetragen.";

            //In Datenbank speichern
            String CurrentTime = getCurrentTimestamp(1);

            getApp().getExecutors().diskIO().execute(() -> {
                        WorkTime startedWorkTime = getApp().getDb().workTimeDato().getOpened();
                        if (startedWorkTime == null) {
                            // Keinen Datensatz mit fehlendem Ende gefunden
                            getApp().getExecutors().mainThread()
                                    .execute(() -> _endDateTime.setText(R.string.NoEmptyStartTime));
                        } else {
                            startedWorkTime.endTime = CurrentTime;
                            getApp().getDb().workTimeDato().update(startedWorkTime);
                            getApp().getExecutors().mainThread()
                                    .execute(() -> _endDateTime.setText(CurrentTime));
                        }
                    }
            );

            // Buttons umschalten
            _startCommand.setEnabled(true);
            _endCommand.setEnabled(false);

            // Toast
            Toast.makeText(MainActivity.this,   //Android Context
                            Meldung,    //Toast-Nachricht aus der Variable "Meldung"
                            Toast.LENGTH_LONG)  //Toast Länge
                    .show();    //Toast anzeigen
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Listener deregistrieren
        _startCommand.setOnClickListener(null);
        _endCommand.setOnClickListener(null);
    }
}