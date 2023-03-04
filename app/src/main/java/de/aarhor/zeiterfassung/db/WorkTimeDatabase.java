package de.aarhor.zeiterfassung.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
                            )/*.addMigrations()*/                     // Migrationen
                            .build();
                }
            }
        }

        return _instance;
    }

    private final static Migration _MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE time_data ADD COLUMN pause INTEGER NOT NULL DEFAULT 0");
        }
    };
}