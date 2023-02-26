package de.aarhor.zeiterfassung;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

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

        _startDateTime = findViewById(R.id.edtText_StartTime);
        _endDateTime = findViewById(R.id.edtText_EndTime);
        _startCommand = findViewById(R.id.btn_StartCommand);
        _endCommand = findViewById(R.id.btn_EndCommand);

        //Initialisierung Datum / Uhrzeit Formatierung
        _dateFormatter = android.text.format.DateFormat.getDateFormat(this);
        _timeFormatter= android.text.format.DateFormat.getTimeFormat(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _startCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Meldung = "Die Start Zeit wurde eingetragen.";

                // Logging
                Log.d("MainActivity",   // Tag für Filterung
                        Meldung); // Log-Nachricht

                //Toast
                Toast.makeText(MainActivity.this,   //Android Context
                                Meldung,    //Toast-Nachricht aus der Variable "Meldung"
                                Toast.LENGTH_LONG)  //Toast Länge
                        .show();    //Toast anzeigen

                //Datumsausgabe für UI
                Calendar currentTime = Calendar.getInstance();
                String currentTimeString=String.format(
                        "%s %s",    //String für die Formatierung
                        _dateFormatter.format(currentTime.getTime()),   //Datum formatieren
                        _timeFormatter.format(currentTime.getTime())    //Zeit formatieren
                );

                _startDateTime.setText(currentTimeString);
            }
        });

        _endCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Meldung = "Die End Zeit wurde eingetragen.";

                // Logging
                Log.d("MainActivity",   // Tag für Filterung
                        Meldung); // Log-Nachricht

                //Toast
                Toast.makeText(MainActivity.this,   //Android Context
                                Meldung,    //Toast-Nachricht aus der Variable "Meldung"
                                Toast.LENGTH_LONG)  //Toast Länge
                        .show();    //Toast anzeigen

                //Datumsausgabe für UI
                Calendar currentTime = Calendar.getInstance();
                String currentTimeString=String.format(
                        "%s %s",    //String für die Formatierung
                        _dateFormatter.format(currentTime.getTime()),   //Datum formatieren
                        _timeFormatter.format(currentTime.getTime())    //Zeit formatieren
                );

                _endDateTime.setText(currentTimeString);
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