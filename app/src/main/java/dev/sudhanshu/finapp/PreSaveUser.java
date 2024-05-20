package dev.sudhanshu.finapp;

import android.os.AsyncTask;


import dev.sudhanshu.finapp.models.User;
import io.realm.Realm;
import io.realm.RealmResults;

public class PreSaveUser {

    public static void preSaveUser() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> {
                    RealmResults<User> users = realm1.where(User.class).equalTo("username", "Fininfocom").findAll();
                    if (users.isEmpty()) {
                        User user = new User();
                        user.setUsername("Fininfocom");
                        user.setPassword("Fin@123");
                        realm1.insert(user);
                    }
                });
                realm.close();
                return null;
            }
        }.execute();
    }
}


