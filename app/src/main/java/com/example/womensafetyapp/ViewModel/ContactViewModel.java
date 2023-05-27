package com.example.womensafetyapp.ViewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.womensafetyapp.Model.Contact_Repository;
import com.example.womensafetyapp.Model.Contacts;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {

    Contact_Repository contact_repository;
    LiveData<List<Contacts>> contacts;
    public ContactViewModel(@NonNull Application application) {
        super(application);
        contact_repository= new Contact_Repository(application);
        contacts=contact_repository.get_all_contacts();
    }

    public LiveData<List<Contacts>> get_all_contacts(){
        return contacts;
    }
    public void update_contact(Contacts contact){
        contact_repository.update_contact(contact);
    }
    public void delete_contact(Contacts contact){
        contact_repository.delete_contact(contact);
    }
    public void add_contact(List<Contacts> contacts){
        contact_repository.add_contact(contacts);
    }
}
