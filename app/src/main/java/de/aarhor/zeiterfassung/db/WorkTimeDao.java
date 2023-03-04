package de.aarhor.zeiterfassung.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WorkTimeDao {
    @Query("select * from time_data")
    List<WorkTime> getAll();

    @Query("select * from time_data where _id = :id")
    WorkTime getbyId(int id);

    @Insert
    void add(WorkTime workTime);

    @Query("select * from time_data " +
            "where IFNULL(Ende, '') = '' " +
            "order by _id DESC")
    WorkTime getOpened();

    @Update
    void update(WorkTime workTime);
}
