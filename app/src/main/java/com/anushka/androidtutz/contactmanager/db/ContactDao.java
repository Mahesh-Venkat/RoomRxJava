package com.anushka.androidtutz.contactmanager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.anushka.androidtutz.contactmanager.db.entity.Contact;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ContactDao {

    @Insert
    public long addContact(Contact contact);

    @Update
    public void updateContact(Contact contact);

    @Delete
    public void deleteContact(Contact contact);

//    @Query("select * from contacts")
//    public List<Contact> getContacts();

    @Query("select * from contacts")
    Flowable<List<Contact>> getContacts();

    @Query("select * from contacts where contact_id == :contactID ")
    public Contact getASpecificContact(long contactID);
}
