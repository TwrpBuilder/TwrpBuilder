package github.grace5921.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.zip.CheckedOutputStream;

import eu.chainfire.libsuperuser.Shell;
import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.app.Activity;
import github.grace5921.TwrpBuilder.config.Config;
import github.grace5921.TwrpBuilder.util.ShellExecuter;
import github.grace5921.TwrpBuilder.util.ShellUtils;

/**
 * Created by Sumit on 19.10.2016.
 */

public class BackupFragment extends Fragment
{
    private ShellUtils mShell;
    private Button mBackupButton;
    private TextView ShowOutput;
    private Button mUploadBackup;
    private String store_RecoveryPartitonPath_output;
    private String[] parts;
    private String[] recovery_output_last_value;
    private String recovery_output_path;
    private List<String> RecoveryPartitonPath;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_backup, container, false);
        /*this.mShell = ((Activity) getActivity()).getShellSession();*/
        mBackupButton=(Button)view.findViewById(R.id.BackupRecovery);
        ShowOutput=(TextView)view.findViewById(R.id.show_output);
        mUploadBackup=(Button)view.findViewById(R.id.UploadBackup);
        if(Config.checkBackup()) {
            mBackupButton.setEnabled(false);
            mUploadBackup.setEnabled(true);
        }
        else {
            mBackupButton.setEnabled(true);
            mUploadBackup.setEnabled(false);
        }

        mBackupButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackupButton.setEnabled(false);
                        Shell.SU.run("mkdir -p /sdcard/TwrpBuilder && dd if="+recovery_output_path+" of=/sdcard/TwrpBuilder/Recovery.img");
                        Shell.SU.run("tar -c /sdcard/TwrpBuilder/Recovery.img > /sdcard/TwrpBuilder/TwrpBuilderRecoveryBackup.tar");
                        ShowOutput.setText("Backed up recovery "+recovery_output_path);
                        Snackbar.make(view, "Made Recovery Backup. ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mUploadBackup.setEnabled(true);
                    }
                }
        );

        mUploadBackup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUploadBackup.setEnabled(false);
                        Snackbar.make(view, "Uploading Please Wait. ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
        );
        try {
            RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep RECOVERY");
            store_RecoveryPartitonPath_output=String.valueOf(RecoveryPartitonPath);
            parts = store_RecoveryPartitonPath_output.split("\\s+");
            recovery_output_last_value = parts[7].split("\\]");
            recovery_output_path=recovery_output_last_value[0];
        }catch (Exception e){
            RecoveryPartitonPath = Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep recovery");
            store_RecoveryPartitonPath_output=String.valueOf(RecoveryPartitonPath);
            parts = store_RecoveryPartitonPath_output.split("\\s+");
            recovery_output_last_value = parts[7].split("\\]");
            recovery_output_path=recovery_output_last_value[0];
        }

        return view;
    }




    @Override
    public void onResume() {
        super.onResume();


    }
}
