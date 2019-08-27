package com.user.getmyparkingtask.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.user.getmyparkingtask.ImagePojo;

import java.util.List;

@Dao
public interface RoomDao {

    @Insert
    void insert(ImagePojo searchItem);

    @Query("SELECT * FROM image_table")
    List<ImagePojo> getAllNotes();

    @Delete
    void delete(ImagePojo searchItem);

}
