package com.github.TwrpBuilder.app;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.filelister.FileListerDialog;
import com.github.TwrpBuilder.filelister.OnFileSelectedListener;
import com.github.TwrpBuilder.util.FWriter;
import com.github.TwrpBuilder.util.ShellExecuter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.github.TwrpBuilder.MainActivity.Cache;
import static com.github.TwrpBuilder.util.Config.Sdcard;
import static com.github.TwrpBuilder.util.Config.TwrpBackFName;
import static com.github.TwrpBuilder.util.Config.buildProp;

/**
 * Created by androidlover5842 on 24/1/18.
 */

public class CustomBackupActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private ProgressBar progressBar;
    public static boolean FromCB;
    public static boolean resultOfB;
    private static boolean running;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_backup);
        Toolbar toolbar = findViewById(R.id.action_bar_tool);
        toolbar.setTitle(R.string.backup_recovery);
        button = findViewById(R.id.bt_generate_backup);
        editText = findViewById(R.id.ed_select_recovery);
        progressBar = findViewById(R.id.pb_gen_backup);
        final FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(this);

        fileListerDialog.setOnFileSelectedListener(new OnFileSelectedListener() {
            @Override
            public void onFileSelected(@NonNull File file, String path) {
                editText.setText(file.toString());
            }
        });


        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileListerDialog.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString()) || editText.getText().toString().trim().length() == 0) {
                    Snackbar.make(findViewById(R.id.ll_custom_backup), getResources().getString(R.string.select_file), Snackbar.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    button.setEnabled(false);
                    running = true;
                    new GenerateBackup().execute();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FromCB = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FromCB = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FromCB = true;
    }

    @Override
    public void onBackPressed() {
        if (!running) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), R.string.you_cant_exit_until_backup, Toast.LENGTH_SHORT).show();
        }
    }

    class GenerateBackup extends AsyncTask<Void, Void, Void> {

        @Nullable
        @Override
        protected Void doInBackground(Void... voids) {
            ShellExecuter.mkdir("TwrpBuilder");
            try {
                new FWriter(Cache + "build.prop", buildProp());

                ShellExecuter.cp(editText.getText().toString(), Cache + "recovery.img");
                String[] file = new String[]{Cache + "build.prop", Cache + "recovery.img"};
                zip(file, Cache + "TwrpBuilderRecoveryBackup.zip");
                ShellExecuter.cp(Cache + "TwrpBuilderRecoveryBackup.zip", Sdcard + "TwrpBuilder/" + TwrpBackFName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            running = false;
            resultOfB = true;
            finish();
        }

        void zip(@NonNull String[] files, @NonNull String zipFile) throws IOException {
            BufferedInputStream origin;
            try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {
                byte data[] = new byte[1024];

                for (String file : files) {
                    FileInputStream fi = new FileInputStream(file);
                    origin = new BufferedInputStream(fi, 1024);
                    try {
                        ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, 1024)) != -1) {
                            out.write(data, 0, count);
                        }
                    } finally {
                        origin.close();
                    }
                }
            }
        }
    }

}
