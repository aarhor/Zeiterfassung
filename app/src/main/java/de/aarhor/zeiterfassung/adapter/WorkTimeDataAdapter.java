package de.aarhor.zeiterfassung.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.aarhor.zeiterfassung.R;
import de.aarhor.zeiterfassung.db.WorkTime;

public class WorkTimeDataAdapter extends RecyclerView.Adapter<WorkTimeDataAdapter.WorkTimeViewHolder> {
    private Context _context;
    private List<WorkTime> _data;

    public WorkTimeDataAdapter(Context context) {
        _context = context;
    }

    @NonNull
    @Override
    public WorkTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(_context);
        View view = inflater.inflate(R.layout.item_time_data, parent, false);
        return new WorkTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkTimeViewHolder holder, int position) {
        // Keine Daten vorhanden
        if (_data == null) {
            return;
        }

        // Keine Daten für die angegebene Position
        if (position >= _data.size()) {
            return;
        }

        WorkTime currentData = _data.get(position);
        holder.StartTimeView.setText(currentData.startTime);
        if (currentData.endTime == null) {
            holder.EndTimeView.setText("---");
        } else {
            holder.EndTimeView.setText(currentData.endTime);
        }
    }

    @Override
    public int getItemCount() {
        if (_data == null) {
            return 0;
        }

        return _data.size();
    }

    public void swapData(List<WorkTime> data) {
        _data = data;
        notifyDataSetChanged();
    }

    static class WorkTimeViewHolder extends RecyclerView.ViewHolder {
        final TextView StartTimeView;
        final TextView EndTimeView;

        public WorkTimeViewHolder(@NonNull View itemView) {
            super(itemView);

            StartTimeView = itemView.findViewById(R.id.StartTimeValue);
            EndTimeView = itemView.findViewById(R.id.EndTimeValue);
        }
    }
}
