package com.anushka.androidtutz.contactmanager;


import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.widget.Toast;

import com.anushka.androidtutz.contactmanager.db.ContactsAppDatabase;
import com.anushka.androidtutz.contactmanager.db.entity.Contact;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class ContactRepository {
    private Application application;
    private ContactsAppDatabase contactsAppDatabase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MutableLiveData<List<Contact>> getContactsLiveData() {
        return contactsLiveData;
    }

    public void setContactsLiveData(MutableLiveData<List<Contact>> contactsLiveData) {
        this.contactsLiveData = contactsLiveData;
    }

    private MutableLiveData<List<Contact>> contactsLiveData = new MutableLiveData<>();
    private long rowIdInserted;

    public ContactRepository(Application mApplication) {
        application = mApplication;

        contactsAppDatabase = Room.databaseBuilder(application.getApplicationContext(), ContactsAppDatabase.class, "myDataBase").build();

        compositeDisposable.add(contactsAppDatabase.getContactsDao().getContacts().
                subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<List<Contact>>() {
                    @Override
                    public void accept(List<Contact> contacts) throws Exception {
                        contactsLiveData.postValue(contacts);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
    }

    public void createContact(final String name, final String email) {

        //long id = contactsAppDatabase.getContactsDao().addContact(new Contact(0, name, email));
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                rowIdInserted = contactsAppDatabase.getContactsDao().addContact(new Contact(0, name, email));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), "Contact Added Successfully " + rowIdInserted, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(application.getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }));


    }

    public void updateContact(final Contact contact) {

        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                contactsAppDatabase.getContactsDao().updateContact(contact);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), "Contact Updated Successfully " + rowIdInserted, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(application.getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void deleteContact(final Contact contact) {

        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                contactsAppDatabase.getContactsDao().deleteContact(contact);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), "Contact Deleted Successfully " + rowIdInserted, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(application.getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void clear() {
        compositeDisposable.clear();
    }

}
