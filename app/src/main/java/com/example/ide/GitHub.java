package com.example.ide;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.ide.GitDB.GitLinks;
import com.example.ide.GitDB.GitLinksDao;
import com.example.ide.GitDB.GitLinksDatabase;
import com.example.ide.GitDB.Recycle.GitLinksRecycleAdapter;
import com.example.ide.GitDB.Recycle.GitLinksRecycleViewShow;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GitHub#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GitHub extends Fragment implements GitLinksRecycleAdapter.OnRecyclerViewItemListener{

    // the fragment initialization parameters
    private static final String FILES_DIR = "path";

    private static String repo_name_for_man;
    private String path;
    private String full_path;
    private CompositeDisposable disposables;
    private Fragment fragment;
    private String link;
    private String dir_name;
    private String branch_name;
    private String password;
    private String account;
    private String repo_name;
    private boolean sucs;
    GitLinksDatabase db;
    ArrayList<GitLinksRecycleViewShow> recycleViewShowList;

    private Unbinder unbinder;
    @BindView(R.id.link)
    TextInputLayout download;
    @BindView(R.id.repo_list)
    RecyclerView recyclerView;


    public GitHub() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param path path to directory where to save
     * @return A new instance of fragment GitHub.
     */
    public static GitHub newInstance(String path) {
        GitHub fragment = new GitHub();
        Bundle args = new Bundle();
        args.putString(FILES_DIR, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString(FILES_DIR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_git_hub, container, false);
        unbinder = ButterKnife.bind(this, view);
        disposables = new CompositeDisposable();
        download.setEndIconOnClickListener(v -> {
            MaterialAlertDialogBuilder dialog_dir = getDialog("Enter your directory name");
            TextInputLayout textInputLayout_dir = getTextInputLayout(dialog_dir, getString(R.string.directory_name));
            dialog_dir
                    .setView(textInputLayout_dir)
                    .setPositiveButton("Next", (dialog, which) -> {
                        dir_name = textInputLayout_dir.getEditText().getText().toString();
                        MaterialAlertDialogBuilder dialog_branch = getDialog("Enter name of branch to clone");
                        TextInputLayout textInputLayout_branch = getTextInputLayout(dialog_branch, getString(R.string.branch_name));
                        dialog_branch
                                .setView(textInputLayout_branch)
                                .setPositiveButton("Next", (dialog_clone, which_clone) -> {
                                    dialog.dismiss();
                                    MaterialAlertDialogBuilder dialog_account = getDialog("Enter account name");
                                    TextInputLayout textInputLayout_account = getTextInputLayout(dialog_account, getString(R.string.account_name));
                                    branch_name = textInputLayout_branch.getEditText().getText().toString();
                                    dialog_account
                                            .setView(textInputLayout_account)
                                            .setPositiveButton("Next", ((dialog_name, which_name) -> {
                                                dialog_clone.dismiss();
                                                MaterialAlertDialogBuilder dialog_password = getDialog("Enter account password");
                                                TextInputLayout textInputLayout_password = getTextInputLayout(dialog_password, getString(R.string.password));
                                                account = textInputLayout_account.getEditText().getText().toString();
                                                dialog_password
                                                    .setView(textInputLayout_password)
                                                    .setPositiveButton("Next", (dialog_pass, which_pass) -> {
                                                        dialog_name.dismiss();
                                                        password = textInputLayout_password.getEditText().getText().toString();
                                                        /*List<OneTimeWorkRequest> list = new ArrayList<>(2);
                                                        OneTimeWorkRequest gitWorkRequest = new OneTimeWorkRequest.Builder(GitWorker.class).build();
                                                        OneTimeWorkRequest roomWorkRequest = new OneTimeWorkRequest.Builder(WritingWorker.class).build();
                                                        list.add(gitWorkRequest);
                                                        list.add(roomWorkRequest);
                                                        WorkManager.getInstance(this.getContext()).beginWith(gitWorkRequest).then(roomWorkRequest).enqueue();*/
                                                        Observable<View> observable_ready = Observable.create(e -> {
                                                            e.onNext(v);
                                                        });
                                                        disposables.add(observable_ready
                                                                .observeOn(Schedulers.io())
                                                                .doOnNext(x -> {
                                                                    link = download.getEditText().getText().toString();
                                                                    full_path = path + dir_name;
                                                                    File f = new File(full_path);
                                                                    if (!f.isFile()) {
                                                                        if (!(f.isDirectory())) {
                                                                            sucs = f.mkdir();
                                                                            Git.cloneRepository()
                                                                                    .setURI(link)
                                                                                    .setBranch(branch_name)
                                                                                    .setDirectory(new File(full_path))
                                                                                    .call();
                                                                        }
                                                                    }
                                                                    Log.d(TAG, "doOnNextCloning: " + sucs);
                                                                })
                                                                .doOnNext(x -> {
                                                                    if (sucs) {
                                                                        GitLinksDao gitLinksDao = db.gitLinksDao();
                                                                        gitLinksDao.insert(createNewLink(link));
                                                                        Log.d(TAG, "doOnNext: " + download.getEditText().getText().toString());
                                                                    }
                                                                })
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(vw -> { Snackbar.make(vw, "Data was saved", Snackbar.LENGTH_SHORT).show(); dialog_pass.dismiss();},
                                                                        err -> Snackbar.make(view, "Error, please report it, name of error: " + err.getMessage(), Snackbar.LENGTH_SHORT).show()));
                                                    }).show();
                                            })).show();
                                }).show();
                    }).show();
        });
        db = DataBases.getInstance().getGitLinksDatabase();
        recycleViewShowList = new ArrayList<>();
        disposables.add(db.gitLinksDao().getAllStocksRxWay()
                .doOnNext(links -> {
                    recycleViewShowList.clear();
                    links.forEach(x -> recycleViewShowList.add(new GitLinksRecycleViewShow(x.getRepo_name())));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(employees -> {
                    GitLinksRecycleAdapter adapter = new GitLinksRecycleAdapter(getLayoutInflater(), recycleViewShowList, this);
                    recyclerView.setAdapter(adapter);
                }));
        return view;
    }

    @OnClick(R.id.return_back)
    void onSaveClick() {
        gotoWritingFragment("Returned");
    }

    @Override
    public void onItemClick(int position) {
        Data myData = new Data.Builder()
                .putString("repo_name", recycleViewShowList.get(position).getRepo_name())
                .build();
        OneTimeWorkRequest work =
                new OneTimeWorkRequest.Builder(FilesInDirShower.class)
                        .setInputData(myData)
                        .build();
        WorkManager.getInstance(getContext()).enqueue(work);
        getDialog("Do you want to open this directory?").setPositiveButton("Yes", (dialog, which) -> {
            WorkManager.getInstance(getContext()).getWorkInfoByIdLiveData(work.getId())
                    .observe(this, workInfo -> {
                        if (workInfo != null && workInfo.getState().isFinished()){
                            String path_to_dir = workInfo.getOutputData().getString("path_to_dir");
                            String[] result = workInfo.getOutputData().getStringArray("files");
                            List<String> result_log = Arrays.asList(result);
                            result_log.forEach(x -> Log.d(TAG, "onItemClick: " + x));
                            AtomicReference<String> path_to_file = new AtomicReference<>();
                            new MaterialAlertDialogBuilder(getContext(), R.style.Theme_IDE_Dialog)
                                    .setTitle("What file do you want to open?")
                                    .setItems(result, (dialog_item, which_item) -> {
                                        path_to_file.set(path_to_dir + "/" + result[which_item]);
                                        Log.d(TAG, "onItemClick: " + path_to_file.toString());
                                        gotoWritingFragment(path_to_file.toString());
                                        dialog.dismiss();
                                    }).show();
                        }
                    });
        }).show();
        Log.d(TAG, "onItemClick: " + recycleViewShowList.get(position).getRepo_name());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
        path = null;
        full_path = null;
        disposables = null;
        fragment = null;
        link = null;
        dir_name = null;
        branch_name = null;
        password = null;
        account = null;
        repo_name = null;
        db = null;
        recycleViewShowList = null;
        Log.d(TAG, "onDestroy: Disposable worked");
    }

    /**
     * Creates Writing fragment.
     *
     * @param message This message will be sent to Writing fragment.
     */
    private void gotoWritingFragment(String message) {
        fragment = Writing.newInstance(message);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit();
    }

    /**
     * Creates dialog with cancel button and special theme.
     *
     * @param title that will be showed in Dialog
     * @return Dialog, that was created
     */
    private MaterialAlertDialogBuilder getDialog(String title) {
        return new MaterialAlertDialogBuilder(getContext(), R.style.Theme_IDE_Dialog)
                .setTitle(title)
                .setNegativeButton(getString(R.string.cancel), (dialog_cncl, which) -> dialog_cncl.dismiss());
    }

    /**
     * Creates GitLinks object, that could be written to DB
     *
     * @param link that will be added to our object
     * @return Object that could be written to DB
     */
    private GitLinks createNewLink(String link){
        GitLinks gitLinks = new GitLinks();
        gitLinks.setLink(link);
        gitLinks.setRepo_name(dir_name);
        gitLinks.setUserName(account);
        gitLinks.setPassword(password);
        gitLinks.setPath(full_path);
        return gitLinks;
    }

    /**
     * Creates layout for dialog.
     *
     * @param dialog it will be showed there
     * @param hint Text hint
     * @return TextInputLayout for MaterialAlertDialogBuilder
     */
    private TextInputLayout getTextInputLayout(MaterialAlertDialogBuilder dialog, String hint){
        TextInputLayout textInputLayout = new TextInputLayout(dialog.getContext());// Pass it an Activity or Context
        TextInputEditText editText = new TextInputEditText(textInputLayout.getContext());
        textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        textInputLayout.addView(editText, editTextParams);
        textInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        textInputLayout.setHint(hint);
        textInputLayout.setBoxStrokeColor(getResources().getColor(R.color.white));
        return textInputLayout;
    }


    /*public class GitWorker extends Worker {
        public GitWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            full_path = path + dir_name;
            File f = new File(full_path);
            if (!f.isFile()) {
                if (!(f.isDirectory())) {
                    sucs = f.mkdir();
                    try {
                        Git.cloneRepository()
                                .setURI(link)
                                .setBranch(branch_name)
                                .setDirectory(new File(full_path))
                                .call();
                    } catch (GitAPIException e) {
                        e.printStackTrace();
                    }
                }
            }
            return Result.success();
        }
    }

    public class WritingWorker extends Worker {

        public WritingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            GitLinksDao gitLinksDao = db.gitLinksDao();
            gitLinksDao.insert(createNewLink(link));
            Log.d(TAG, "doOnNext: " + download.getEditText().getText().toString());
            return Result.success();
        }
    }*/
}
