package com.example.ide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class WriterToFile extends Worker {
    public final String TAG = "MY_TAG";

    public WriterToFile(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.v(TAG, "Work is in progress");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Writing.path_to_file));) {
            writer.write(Writing.from_edtxt);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Work finished");
        return Result.success();
    }
}
