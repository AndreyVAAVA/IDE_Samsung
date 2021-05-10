package com.example.ide;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.eclipsesource.v8.V8;
import com.example.ide.GitDB.GitLinks;
import com.example.ide.GitDB.GitLinksDao;
import com.example.ide.GitDB.GitLinksDatabase;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Writing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Writing extends Fragment {

    // the fragment initialization parameters
    private static final String DECISION = "param1";

    // TODO: Rename and change types of parameters
    private String todo;
    public static String readed = "";
    public static String from_edtxt;
    public static String path_to_file = "";
    private ItemViewModel viewModel;

    BottomSheetBehavior bottomSheetBehavior;
    @BindView(R.id.sheet)
    View layout;
    @BindView(R.id.code)
    EditText code;
    public Writing() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Writing.
     */
    // TODO: Rename and change types and number of parameters
    public static Writing newInstance(String param1) {
        Writing fragment = new Writing();
        Bundle args = new Bundle();
        args.putString(DECISION, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            todo = getArguments().getString(DECISION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_writing, container, false);
        ButterKnife.bind(this, view);
        //viewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);
        /*items.setOnClickListener(item -> {
            // Set a new item
            viewModel.selectItem(path_to_file);
        });*/
        if (todo != null){
            if (!(todo.equalsIgnoreCase("Returned"))){
                Snackbar.make(view, todo, Snackbar.LENGTH_SHORT).show();
                readed = "";
                code.setText(readed);
                path_to_file = todo;
                Data myData = new Data.Builder().putString("path_to_file", todo).build();
                OneTimeWorkRequest read = new OneTimeWorkRequest.Builder(ReaderFromFile.class).setInputData(myData).build();
                WorkManager.getInstance(getContext()).enqueue(read);
                WorkManager.getInstance(getContext()).getWorkInfoByIdLiveData(read.getId()).observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()){
                        //String readed = workInfo.getOutputData().getString("readed");
                        code.setText(readed);
                    }
                });
            }
        }
        bottomSheetBehavior = BottomSheetBehavior.from(layout);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        todo = null;
        layout.setVisibility(View.INVISIBLE);
    }
    @OnTextChanged(value = R.id.code, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onTextChanged(CharSequence text){
        from_edtxt = text.toString();
    }
}