package de.aarhor.zeiterfassung;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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
    private Button _selectDB;           // Button Datenbank auswählen
    private CheckBox _chckBoxPause;     // Checkbox Pause
    Zeitenrechner get_Zeiten = new Zeitenrechner();
    DatumKonverter Konverter = new DatumKonverter();
    Uri mDbUri = null;

    private static final int PICK_DB_FILE_REQUEST_CODE = 1;
    private static final String[] DB_MIME_TYPES = {"application/x-sqlite3", "application/octet-stream"};

    public void openDatabase(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, DB_MIME_TYPES);
        startActivityForResult(intent, PICK_DB_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_DB_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                mDbUri = data.getData();

                //Toast
                Toast.makeText(MainActivity.this,   //Android Context
                                mDbUri.getPath(),    //Toast-Nachricht aus der Variable "Meldung"
                                Toast.LENGTH_LONG)  //Toast Länge
                        .show();    //Toast anzeigen
            }
        }
    }

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
                    _Comment.setText("");
                    _startCommand.setEnabled(true);
                    _Comment.setEnabled(false);
                    _Comment.setText("");
                });
            } else {
                String finalesDeutschesDatum;
                try {
                    finalesDeutschesDatum = Konverter.englisch_deutsch_Datum(openWorkTime.Datum);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                // Offener Datensatz
                getApp().getExecutors().mainThread().execute(() -> {
                    _startDateTime.setText(finalesDeutschesDatum + " " + openWorkTime.startTime);
                    _endDateTime.setText("");
                    _endCommand.setEnabled(true);
                    _Comment.setEnabled(true);
                });
            }
        });

        String HeutigerTag = Konverter.get_DayofWeekGerman(getCurrentTimestamp(2));
        if (HeutigerTag.equalsIgnoreCase("Montag") ||
                HeutigerTag.equalsIgnoreCase("Dienstag") ||
                HeutigerTag.equalsIgnoreCase("Mittwoch") ||
                HeutigerTag.equalsIgnoreCase("Donnerstag")) {
            _chckBoxPause.setChecked(true);
        } else if (HeutigerTag.equalsIgnoreCase("Freitag") ||
                HeutigerTag.equalsIgnoreCase("Samstag") ||
                HeutigerTag.equalsIgnoreCase("Sonntag")) {
            _chckBoxPause.setChecked(false);
        }
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
        _chckBoxPause = findViewById(R.id.chkBox_Pause);
        _selectDB = findViewById(R.id.btn_select_db);

        _startDateTime.setEnabled(false);
        _endDateTime.setEnabled(false);
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
            _Comment.setText(null);
            _endDateTime.setText(null);

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

                            if (_Comment.getText().toString().trim().length() == 0) {
                                startedWorkTime.Bemerkung = null;
                            } else
                                startedWorkTime.Bemerkung = _Comment.getText().toString();

                            try {
                                double value = (get_Zeiten.get_Differenz(startedWorkTime.startTime, startedWorkTime.endTime));
                                Differenz[0] = Math.round(100.0 * value) / 100.0;
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            startedWorkTime.Differenz = Differenz[0];

                            boolean Pause = _chckBoxPause.isChecked();
                            startedWorkTime.MehrMinder_Stunden = get_Zeiten.get_MehrMinderStunden(startedWorkTime.Datum, Differenz[0], Pause);

                            getApp().getDb().workTimeDato().update(startedWorkTime);
                            getApp().getExecutors().mainThread()
                                    .execute(() -> _endDateTime.setText(getCurrentTimestamp(1)));
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

    @SuppressLint("NonConstantResourceId")
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