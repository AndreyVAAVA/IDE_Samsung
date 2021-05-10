package com.example.ide.GitDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ide.GitDB.GitLinks;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface GitLinksDao {

    @Insert
    void insert(GitLinks link);

    @Insert
    void insertAll(GitLinks... links);

    @Update
    void update(GitLinks link);

    @Delete
    void delete(GitLinks links);

    @Query("SELECT * FROM gitlinks")
    List<GitLinks> getAllStocks();

    @Query("SELECT * FROM gitlinks")
    Flowable<List<GitLinks>> getAllStocksRxWay();

    /*@Query("SELECT * FROM gitlinks WHERE repo_name LIKE :state")
    List<GitLinks> getAllNamedLinks(String state);*/

    @Query("SELECT * FROM gitlinks WHERE repo_name LIKE :repo_name")
    GitLinks getGitLink(String repo_name);

    @Query("SELECT * FROM gitlinks WHERE userName LIKE :userName")
    GitLinks getCompanyStocksByTicker(String userName);
}
