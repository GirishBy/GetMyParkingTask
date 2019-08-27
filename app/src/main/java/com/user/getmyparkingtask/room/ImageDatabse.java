package com.user.getmyparkingtask.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.user.getmyparkingtask.ImagePojo;

@Database(entities = {ImagePojo.class}, version = 1)
public abstract class ImageDatabse extends RoomDatabase {

    private static ImageDatabse instance;

    public abstract RoomDao roomDao();

    public static synchronized ImageDatabse getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ImageDatabse.class, "image_databse")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
