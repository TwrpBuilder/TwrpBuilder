package github.grace5921.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

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
    ShellUtils mShell;
    private Button mBackupButton;
    private TextView ShowOutput;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_backup, container, false);
        /*this.mShell = ((Activity) getActivity()).getShellSession();*/
        mBackupButton=(Button)view.findViewById(R.id.BackupButton);
        ShowOutput=(TextView)view.findViewById(R.id.show_output);

        if(Config.checkBackup()) {
            mBackupButton.setEnabled(false);
        }

        mBackupButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackupButton.setEnabled(false);
                        ShowOutput.setText(recovery_output);
                    }
                }
        );

        return view;
    }

    private static final List<String> BackupTwrp= Shell.SU.run("ls -la `find /dev/block/platform/ -type d -name \"by-name\"` | grep RECOVERY");
    private String store_dev_output=String.valueOf(BackupTwrp);
    private String[] parts = store_dev_output.split("\\s+");
    private String[] recovery_output_last = parts[7].split("\\]");
    private String recovery_output=recovery_output_last[0];

    @Override
    public void onResume() {
        super.onResume();


    }
}
