package de.aarhor.zeiterfassung.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "time_data")
public class WorkTime {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id;

    @NonNull
    @ColumnInfo(name = "start_time")
    public Calendar startTime = Calendar.getInstance();

    @Nullable
    @ColumnInfo(name = "end_time")
    public Calendar endTime;

    public WorkTime() {
        startTime = null;
    }
}