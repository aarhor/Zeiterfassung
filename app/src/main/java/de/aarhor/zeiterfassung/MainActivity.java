package de.aarhor.zeiterfassung;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.aarhor.zeiterfassung.db.WorkTime;

public class MainActivity extends AppCompatActivity {
    private EditText _startDateTime;    //"textBox" Startzeit
    private EditText _endDateTime;      //"textBox" Endzeit
    private Button _startCommand;       //Button Start
    private Button _endCommand;         //Button Ende
    private DateFormat _dateFormatter;
    private DateFormat _timeFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // "Suchen" der UI Elemente
        _startDateTime = findViewById(R.id.edtText_StartTime);
        _endDateTime = findViewById(R.id.edtText_EndTime);
        _startCommand = findViewById(R.id.btn_StartCommand);
        _endCommand = findViewById(R.id.btn_EndCommand);

        // Deaktivieren der Tastatureingaben
        _startDateTime.setKeyListener(null);
        _endDateTime.setKeyListener(null);

        // Initialisierung Datum / Uhrzeit Formatierung
        _dateFormatter = android.text.format.DateFormat.getDateFormat(this);
        _timeFormatter = android.text.format.DateFormat.getTimeFormat(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initFromDb();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Listener registrieren
        _startCommand.setOnClickListener(onStartClicked());
        _endCommand.setOnClickListener(onEndClicked());
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Listener deregistrieren
        _startCommand.setOnClickListener(null);
        _endCommand.setOnClickListener(null);
    }

    private TimeTrackingApp getApp() {
        return (TimeTrackingApp) getApplication();
    }

    private void setStartTimeforUI(String startTime) {
        getApp().getExecutors().mainThread().execute(() -> {
            _startCommand.setEnabled(false);
            _startDateTime.setText(startTime);
            _endCommand.setEnabled(true);
        });
    }

    private void setEndTimeforUI(String endTime) {
        getApp().getExecutors().mainThread().execute(() -> {
            _endCommand.setEnabled(false);
            _endDateTime.setText(endTime);
            _startCommand.setEnabled(true);
        });
    }

    private void resetStartEnd() {
        getApp().getExecutors().mainThread().execute(() -> {
            _startDateTime.setText("");
            _endDateTime.setText("");
            _startCommand.setEnabled(true);
        });
    }

    private void initFromDb() {
        // Deaktivieren der beiden Buttons
        _startCommand.setEnabled(false);
        _endCommand.setEnabled(false);

        // Laden eines offenes Datensatzes
        getApp().getExecutors().diskIO().execute(() -> {
            WorkTime openWorkTile = getApp().getDb().workTimeDato().getOpened();
            if (openWorkTile == null) {
                // Keine offenen Datensätze
                resetStartEnd();
            } else {
                // Offener Datensatz
                setStartTimeforUI(formatforUI(openWorkTile.startTime));
            }
        });
    }

    private View.OnClickListener onStartClicked() {
        return v -> {
            _startCommand.setEnabled(false);

            // In Datenbank speichern
            getApp().getExecutors().diskIO().execute(() -> {
                WorkTime workTime = new WorkTime();
                getApp().getDb().workTimeDato().add(workTime);

                setStartTimeforUI(formatforUI(workTime.startTime));
            });
        };
    }

    private View.OnClickListener onEndClicked() {
        return v -> {
            _endCommand.setEnabled(false);

            getApp().getExecutors().diskIO().execute(() -> {
                WorkTime startedWorkTime = getApp().getDb().workTimeDato().getOpened();
                if (startedWorkTime == null) {
                    // Kein Datensatz mit offenen Enden gefunden
                    resetStartEnd();
                } else {
                    Calendar currentTime=Calendar.getInstance();
                    startedWorkTime.endTime = currentTime;
                    getApp().getDb().workTimeDato().update(startedWorkTime);
                }
            });

            String Meldung = "Die End Zeit wurde eingetragen.";

            //Toast
            Toast.makeText(MainActivity.this,    // Android Context
                            Meldung,                    // Toast-Nachricht aus der Variable "Meldung"
                            Toast.LENGTH_LONG)          // Toast Länge
                    .show();                            // Toast anzeigen
        };
    }

    private String formatforUI(Calendar currentTime) {
        return String.format(
                "%s %s",        // String Format
                _dateFormatter.format(currentTime.getTime()),
                _timeFormatter.format(currentTime.getTime())
        );
    }
}