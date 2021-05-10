package com.example.ide.GitDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.ide.GitDB.GitLinks;

@Database(entities = {GitLinks.class}, version = 1)
public abstract class GitLinksDatabase extends RoomDatabase {
    public abstract GitLinksDao gitLinksDao();
}