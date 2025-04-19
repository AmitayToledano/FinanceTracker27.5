package com.example.financetracker.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.financetracker.models.Group;

import java.util.List;

@Dao
public interface GroupDao {

    @Query("SELECT * FROM group_")
    List<Group> getAll();

    @Query("SELECT * FROM group_ WHERE id = :id LIMIT 1")
    Group getGroupById(int id);

    @Insert
    void insert(Group group);

    @Update
    void update(Group group);

    @Delete
    void delete(Group group);

}
