package com.example.ide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.ide.GitDB.GitLinks;
import com.example.ide.GitDB.GitLinksDao;
import com.example.ide.GitDB.GitLinksDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReaderFromFile extends Worker {
    public final String TAG = "MY_TAG";

    public ReaderFromFile(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.v(TAG, "Work is in progress");
        String path_to_file = getInputData().getString("path_to_file");
        StringBuilder readed = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path_to_file));) {
            while (reader.ready())
                Writing.readed += reader.readLine() + "\n";
            //readed.append(reader.readLine()).append("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Data output = new Data.Builder()
                .putString("readed", "completed")
                .build();
        Log.v(TAG, "Work finished");
        return Result.success(output);
    }
}
