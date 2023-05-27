package com.example.womensafetyapp.Model;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Contact_Repository {

    private ContactsDatabase db;
    private ContactsDao contactsDao;
    private LiveData<List<Contacts>> all_contacts;

    public Contact_Repository(Application application) {
         db= ContactsDatabase.getInstance(application);
         contactsDao= db.contactsDao();
         all_contacts=contactsDao.get_all_contacts();
    }


    public LiveData<List<Contacts>> get_all_contacts(){
         return all_contacts;
    }
    public void update_contact(Contacts contact){
        ContactsDatabase.executorService.execute(new Runnable() {
            @Override
            public void run() {
                contactsDao.Update_contact(contact);
            }
        });

    }
    public void delete_contact(Contacts contact){
        ContactsDatabase.executorService.execute(new Runnable() {
            @Override
            public void run() {
                contactsDao.Delete_contact(contact);
            }
        });
    }
    public void add_contact(List<Contacts> contacts){
        ContactsDatabase.executorService.execute(new Runnable() {
            @Override
            public void run() {
                contactsDao.Insert_contacts(contacts);
            }
        });
    }
}
