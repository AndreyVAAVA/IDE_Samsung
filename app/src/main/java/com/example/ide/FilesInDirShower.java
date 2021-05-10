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

import java.io.File;

public class FilesInDirShower extends Worker {
    public final String TAG = "MY_TAG";

    public FilesInDirShower(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.v(TAG, "Work is in progress");
        String repo_name = getInputData().getString("repo_name");
        GitLinksDatabase db = DataBases.getInstance().getGitLinksDatabase();
        GitLinksDao gitLinksDao = db.gitLinksDao();
        GitLinks gitLink = gitLinksDao.getGitLink(repo_name);
        String path_to_dir = gitLink.getPath();
        String[] pathnames;

        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File f = new File(gitLink.getPath());
        // Populates the array with names of files and directories
        pathnames = f.list();
        Data output = new Data.Builder()
                .putString("path_to_dir", path_to_dir)
                .putStringArray("files", pathnames)
                .build();
        Log.v(TAG, "Work finished");
        return Result.success(output);
    }
}
