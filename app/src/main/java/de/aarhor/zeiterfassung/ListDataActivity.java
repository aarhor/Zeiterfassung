package de.aarhor.zeiterfassung;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.aarhor.zeiterfassung.adapter.WorkTimeDataAdapter;
import de.aarhor.zeiterfassung.db.WorkTime;

public class ListDataActivity extends AppCompatActivity {
    private WorkTimeDataAdapter _workTimeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        // Liste suchen
        RecyclerView list = findViewById(R.id.DataList);
        list.setLayoutManager(new LinearLayoutManager(this));

        // Adapter
        _workTimeAdapter = new WorkTimeDataAdapter(this);
        list.setAdapter(_workTimeAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getApp().getExecutors().diskIO().execute(() -> {
            List<WorkTime> data = getApp().getDb().workTimeDato().getAll();
            getApp().getExecutors().mainThread().execute(() -> _workTimeAdapter.swapData(data));
        });
    }

    private TimeTrackingApp getApp() {
        return (TimeTrackingApp) getApplication();
    }
}