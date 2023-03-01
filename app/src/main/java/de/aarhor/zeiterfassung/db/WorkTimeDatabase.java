package de.aarhor.zeiterfassung.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {WorkTime.class}, version = 1)
@TypeConverters({CalendarConverter.class})
public abstract class WorkTimeDatabase extends RoomDatabase {
    public abstract WorkTimeDao workTimeDato();
    private static WorkTimeDatabase _instance;

    public static WorkTimeDatabase getInstance(final Context context) {
        if (_instance == null) {
            synchronized (WorkTimeDatabase.class) {
                if (_instance == null) {
                    _instance = Room.databaseBuilder(
                            context.getApplicationContext(),    // Context
                            WorkTimeDatabase.class,             // Datenbank
                            "worktime_data.db"                  // Dateiname
                    ).build();
                }
            }
        }

        return _instance;
    }
}