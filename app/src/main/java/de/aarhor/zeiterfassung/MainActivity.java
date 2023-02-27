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

        _startCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Meldung = "Die Start Zeit wurde eingetragen.";

                //Toast
                Toast.makeText(MainActivity.this,   //Android Context
                                Meldung,    //Toast-Nachricht aus der Variable "Meldung"
                                Toast.LENGTH_LONG)  //Toast Länge
                        .show();    //Toast anzeigen

                //Datumsausgabe für UI
                _startDateTime.setText(getCurrentTimestamp(1));

                //In Datenbank speichern
                final TimeTrackingApp app = (TimeTrackingApp) getApplication();

                app.getExecutors().diskIO().execute(() -> {
                    WorkTime workTime = new WorkTime();
                    workTime.startTime = getCurrentTimestamp(1);
                    app.getDb().workTimeDato().add(workTime);
                });
                WorkTimeDatabase db = Room.databaseBuilder(
                        MainActivity.this,  // Android Context
                        WorkTimeDatabase.class,    // Datentyp der Datenbank
                        "worktime_data.db"         // Name der Datenbank
                ).build();
            }
        });

        _endCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Meldung = "Die End Zeit wurde eingetragen.";

                //Toast
                Toast.makeText(MainActivity.this,   //Android Context
                                Meldung,    //Toast-Nachricht aus der Variable "Meldung"
                                Toast.LENGTH_LONG)  //Toast Länge
                        .show();    //Toast anzeigen

                //Datumsausgabe für UI
                _endDateTime.setText(getCurrentTimestamp(1));
            }
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