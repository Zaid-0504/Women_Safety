package com.example.womensafetyapp.Model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Contacts.class},version = 1,exportSchema = false)
public abstract class ContactsDatabase extends RoomDatabase {

    public abstract ContactsDao contactsDao();
    private static  volatile ContactsDatabase instance;

    public static ExecutorService executorService= Executors.newFixedThreadPool(4);

    public static ContactsDatabase getInstance(final Context context) {
        if(instance == null){
            synchronized (ContactsDatabase.class){
                if(instance==null){
                    instance= Room.databaseBuilder(context.getApplicationContext(),
                            ContactsDatabase.class,"kavach_Database").build();
                }
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {


                    }
                });

            }

        }
        return instance;
    }

}
