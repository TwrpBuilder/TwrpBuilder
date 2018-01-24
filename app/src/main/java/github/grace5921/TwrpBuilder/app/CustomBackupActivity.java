package github.grace5921.TwrpBuilder.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import easyfilepickerdialog.kingfisher.com.library.model.DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;
import eu.chainfire.libsuperuser.Shell;
import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.util.Config;
import github.grace5921.TwrpBuilder.util.ShellExecuter;

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
    protected void onDestroy() {
        super.onDestroy();
        FromCB=true;
    }

    class GenrateBackup extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            ShellExecuter.mkdir("TwrpBuilder");
            try {
                ShellExecuter.cp("/system/build.prop","/sdcard/TwrpBuilder/build.prop");
                ShellExecuter.cp(editText.getText().toString(),"/sdcard/TwrpBuilder/recovery.img");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` >  /sdcard/TwrpBuilder/mounts ; cd /sdcard/TwrpBuilder && tar -c recovery.img build.prop mounts > /sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar");
            compressGzipFile("/sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar","/sdcard/TwrpBuilder/"+ Config.TwrpBackFName);
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
