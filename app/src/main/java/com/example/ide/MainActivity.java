package com.example.ide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Supplier;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import com.eclipsesource.v8.V8;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import net.openhft.compiler.CompilerUtils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.helpers.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private ItemViewModel viewModel;

    @BindView(R.id.topAppBar)
    MaterialToolbar materialToolbar;

    Fragment fragment;
    OneTimeWorkRequest write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        materialToolbar.setNavigationOnClickListener(v -> {
            fragment = GitHub.newInstance(this.getFilesDir().toString());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .commit();
        });
        View view = findViewById(android.R.id.content).getRootView();
        materialToolbar.setOnMenuItemClickListener(m -> {
            switch (m.getItemId()){
                case R.id.launch:
//                    Snackbar.make(view, "Launch", Snackbar.LENGTH_SHORT).show();
                    //Snackbar.make(view, RunCommand("d8 --version"), Snackbar.LENGTH_SHORT).show();

                    /*File f = new File(this.getFilesDir() + "test.java");
                    Snackbar.make(view, this.getFilesDir() + "test.java", Snackbar.LENGTH_SHORT).show();
                    if(!f.exists()) { //check if the file already exists
                        {
                            Snackbar.make(view, this.getFilesDir().toString(), Snackbar.LENGTH_SHORT).show();
                            try {
                                f.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Supplier<String> supplier = Reflect.compile(
                            "com.example.HelloWorld",
                            "package com.example;\n" +
                                    "class HelloWorld implements java.util.function.Supplier<String> {\n" +
                                    "    public String get() {\n" +
                                    "        return \"Hello World!\";\n" +
                                    "    }\n" +
                                    "}\n").create().get();*/

                    // Prints "Hello World!"
                    //Log.d(TAG, "onCreate: " + supplier.get());
                    //System.out.println(supplier.get());
                    //RunCommand("javac -source 1.8 -target 1.8 \\" + " -cp \"$ANDROID_HOME\"/platforms/android-30/android.jar "+ this.getFilesDir() + "test.java");
                    //RunCommand("./android-sdk-linux/build-tools/23.0.2/dx --output=test.jar --dex ./bin lib/commons-cli-1.3.1.jar");
                    write = new OneTimeWorkRequest.Builder(WriterToFile.class).build();
                    WorkManager.getInstance(this.getApplicationContext()).enqueue(write);
                    OneTimeWorkRequest compile = new OneTimeWorkRequest.Builder(JSCompiler.class).build();
                    WorkManager.getInstance(this).enqueue(compile);
                    WorkManager.getInstance(this).getWorkInfoByIdLiveData(compile.getId()).observe(this, workInfo -> {
                        if (workInfo != null && workInfo.getState().isFinished()){
                            String readed = workInfo.getOutputData().getString("compiled");
                            Snackbar.make(view, readed, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    //V8 runtime = V8.createV8Runtime();
                    /*Integer result = (Integer) runtime.executeScript(""
                            + "var hello = 'hello, ';\n"
                            + "var world = 'world!';\n"
                            + "hello.concat(world).length;\n");*/
                    /*String result = (String) runtime.executeScript(""
                            + "\"hello\" + \"world\"");
                    Log.d(TAG, "onCreate: " + result);
                    runtime.release();*/
                    /*V8 runtime = V8.createV8Runtime();
                    int result = runtime.executeIntegerScript(""
                            + "var hello = 'hello, ';\n"
                            + "var world = 'world!';\n"
                            + "hello.concat(world).length;\n");*/
                    break;
                case R.id.build:
                    Snackbar.make(view, "Build", Snackbar.LENGTH_SHORT).show();
                    break;
                case R.id.stop:
                    Snackbar.make(view, "Stop", Snackbar.LENGTH_SHORT).show();
                    break;
                case R.id.save:
                    /*viewModel = new ViewModelProvider(this.getApplicationContext()).get(ItemViewModel.class);
                    viewModel.getSelectedItem().observe(this, item -> {
                        // Perform an action with the latest item data
                    });*/
                    write = new OneTimeWorkRequest.Builder(WriterToFile.class).build();
                    WorkManager.getInstance(this.getApplicationContext()).enqueue(write);
                    Snackbar.make(view, "Saved", Snackbar.LENGTH_SHORT).show();
                    break;
                case R.id.commit:
                    Snackbar.make(view, "Commit", Snackbar.LENGTH_SHORT).show();
                    break;
                default:
                    Snackbar.make(view, "No idea", Snackbar.LENGTH_SHORT).show();
                    break;
            }
            return false;
        });
    }
    String RunCommand(String cmd) {
        StringBuffer cmdOut = new StringBuffer();
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
            InputStreamReader r = new InputStreamReader(process.getInputStream());
            BufferedReader bufReader = new BufferedReader(r);
            char[] buf = new char[4096];
            int nRead = 0;
            while ((nRead = bufReader.read(buf)) > 0) {
                cmdOut.append(buf, 0, nRead);
            }
            bufReader.close();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cmdOut.toString();
    }
}
