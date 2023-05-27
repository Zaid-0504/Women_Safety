package com.example.womensafetyapp.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactsDao {

    @Query("Select * from Contact_Detail")
    LiveData<List<Contacts>> get_all_contacts();

    @Insert
    void Insert_contacts(List<Contacts> Contacts);

    @Delete
    void Delete_contact(Contacts contact);

    @Update
    void Update_contact(Contacts contact);
}
