package de.aarhor.zeiterfassung.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "time_data")
public class WorkTime {
    /*
    CREATE TABLE Zeiten (_id INTEGER PRIMARY KEY, Datum DATE, Start DATETIME, Ende DATETIME, Differenz DOUBLE, MehrMinder_Stunden DOUBLE, Bemerkung TEXT
default NULL);
     */

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id;

    @NonNull
    @ColumnInfo(name = "Datum")
    public String Datum;

    @NonNull
    @ColumnInfo(name = "Start")
    public String startTime;

    @Nullable
    @ColumnInfo(name = "Ende")
    public String endTime;

    @Nullable
    @ColumnInfo(name = "Differenz")
    public Double Differenz;

    @Nullable
    @ColumnInfo(name = "MehrMinder_Stunden")
    public Double MehrMinder_Stunden;

    @Nullable
    @ColumnInfo(name = "Bemerkung")
    public String Bemerkung;
}