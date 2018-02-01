package com.github.TwrpBuilder.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import easyfilepickerdialog.kingfisher.com.library.model.DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;
import eu.chainfire.libsuperuser.Shell;
import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.util.Config;
import com.github.TwrpBuilder.util.ShellExecuter;

import static com.github.TwrpBuilder.util.Config.Sdcard;

/**
 * Created by androidlover5842 on 24/1/18.
 */

public class CustomBackupActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private DialogConfig dialogConfig;
    private FilePickerDialogFragment.Builder  builder;
    private ProgressBar progressBar;
    public static boolean FromCB,resultOfB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_backup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        button=findViewById(R.id.bt_generate_backup);
        editText=findViewById(R.id.ed_select_recovery);
        progressBar=findViewById(R.id.pb_gen_backup);
        dialogConfig = new DialogConfig.Builder()
                .enableMultipleSelect(true) // default is false
                .enableFolderSelect(true) // default is false
                .initialDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator)
                .supportFiles(new SupportFile(".img",0))
                .enableFolderSelect(false)
                .enableMultipleSelect(false)
                .build();

        builder=new FilePickerDialogFragment.Builder();

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        builder
                        .configs(dialogConfig)
                        .onFilesSelected(new FilePickerDialogFragment.OnFilesSelectedListener() {
                            @Override
                            public void onFileSelected(List<File> list) {
                                for (File file : list) {
                                    editText.setText(file.toString());
                                }
                            }
                        })
                        .build()
                        .show(getSupportFragmentManager(), null);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString()))
                {
                    Snackbar.make(getCurrentFocus(),"Please select a file",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    button.setEnabled(false);
                    new GenrateBackup().execute();

                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FromCB=true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FromCB=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FromCB=true;
    }

    class GenrateBackup extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            ShellExecuter.mkdir("TwrpBuilder");
            try {
                ShellExecuter.cp("/system/build.prop",Sdcard+"TwrpBuilder/build.prop");
                ShellExecuter.cp(editText.getText().toString(),Sdcard+"/TwrpBuilder/recovery.img");
                Shell.SH.run("cd "+Sdcard+"/TwrpBuilder/ && tar -c build.prop recovery.img > TwrpBuilderRecoveryBackup.tar");
                compressGzipFile(Sdcard+"TwrpBuilder/TwrpBuilderRecoveryBackup.tar",Sdcard+"TwrpBuilder/"+ Config.TwrpBackFName);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            resultOfB=true;
            finish();
        }

        private void compressGzipFile(String file, String gzipFile) {
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(gzipFile);
                GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
                byte[] buffer = new byte[1024];
                int len;
                while((len=fis.read(buffer)) != -1){
                    gzipOS.write(buffer, 0, len);
                }
                //close resources
                gzipOS.close();
                fos.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
