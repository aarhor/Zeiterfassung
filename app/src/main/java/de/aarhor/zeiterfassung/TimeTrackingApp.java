package de.aarhor.zeiterfassung;

import android.app.Application;

import de.aarhor.zeiterfassung.db.WorkTimeDatabase;

public class TimeTrackingApp extends Application {
    private AppExecutors _executors;

    @Override
    public void onCreate() {
        super.onCreate();
        _executors = new AppExecutors();
    }

    public AppExecutors getExecutors() {
        return _executors;
    }

    public WorkTimeDatabase getDb(){
        return WorkTimeDatabase.getInstance(
                this.getApplicationContext());
    }
}
