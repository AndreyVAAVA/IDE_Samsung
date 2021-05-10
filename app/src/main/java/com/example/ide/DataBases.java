package com.example.ide;

import android.app.Application;

import androidx.room.Room;

import com.example.ide.GitDB.GitLinks;
import com.example.ide.GitDB.GitLinksDatabase;


public class DataBases extends Application {

    public static DataBases instance;

    private GitLinksDatabase gitLinksDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        gitLinksDatabase = Room.databaseBuilder(this, GitLinksDatabase.class, "links")
                .build();
    }

    public static DataBases getInstance() {
        return instance;
    }

    public GitLinksDatabase getGitLinksDatabase() {
        return gitLinksDatabase;
    }

}