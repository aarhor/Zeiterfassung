package de.aarhor.zeiterfassung.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "time_data")
public class WorkTime {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id;

    @NonNull
    @ColumnInfo(name = "start_time")
    public String startTime;

    @Nullable
    @ColumnInfo(name = "end_time")
    public String endTime;

    public WorkTime() {
        startTime = null;
    }
}