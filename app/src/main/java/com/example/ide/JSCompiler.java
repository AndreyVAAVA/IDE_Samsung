package com.example.ide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.eclipsesource.v8.V8;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JSCompiler extends Worker {
    public final String TAG = "MY_TAG";

    public JSCompiler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.v(TAG, "Work is in progress");
        StringBuilder readed = new StringBuilder();
        /*try (BufferedReader reader = new BufferedReader(new FileReader(Writing.path_to_file));) {
            while (reader.ready())
                readed.append(reader.readLine()).append("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        V8 runtime = V8.createV8Runtime();
        Object result = runtime.executeScript(Writing.from_edtxt);
        runtime.release();
        String total_str;
        Integer total_int;
        Double total_dbl;
        Boolean total_bln;
        String end = "";
        if (result instanceof String) {
            total_str = (String) result;
            end = total_str;
        } else if (result instanceof Integer) {
            total_int = (Integer) result;
            end = total_int.toString();
        } else if (result instanceof Double) {
            total_dbl = (Double) result;
            end = total_dbl.toString();
        } else if (result instanceof Boolean) {
            total_bln = (Boolean) result;
            end = total_bln.toString();
        }
        Data output = new Data.Builder()
                .putString("compiled", end)
                .build();
        Log.v(TAG, "Work finished");
        return Result.success(output);
    }
}
