package com.github.TwrpBuilder.filelister;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.github.TwrpBuilder.R;

import java.io.File;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.github.TwrpBuilder.util.Config.MIN_BACKUP_SIZE;

/**
 * A File Lister Dialog
 */

public class FileListerDialog {

    private final AlertDialog alertDialog;

    private FilesListerView filesListerView;

    private OnFileSelectedListener onFileSelectedListener;
    private Context ctx;

    private FileListerDialog(@NonNull Context context) {
        //super(context);
        alertDialog = new AlertDialog.Builder(context).create();
        init(context);
    }

    private FileListerDialog(@NonNull Context context, int themeResId) {
        //super(context, themeResId);
        alertDialog = new AlertDialog.Builder(context, themeResId).create();
        init(context);
    }

    /**
     * Creates a default instance of FileListerDialog
     *
     * @param context Context of the App
     * @return Instance of FileListerDialog
     */
    public static FileListerDialog createFileListerDialog(@NonNull Context context) {
        return new FileListerDialog(context);
    }

    private void init(final Context context) {
        this.ctx = context;
        filesListerView = new FilesListerView(context);
        alertDialog.setView(filesListerView);
        alertDialog.setButton(BUTTON_POSITIVE, context.getString(R.string.select), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialogInterface, int i) {
                if (new File(filesListerView.getSelected().getAbsolutePath()).length() > MIN_BACKUP_SIZE) {
                    dialogInterface.dismiss();
                    if (onFileSelectedListener != null)
                        onFileSelectedListener.onFileSelected(filesListerView.getSelected(), filesListerView.getSelected().getAbsolutePath());
                } else
                    Toast.makeText(context, R.string.recovery_too_small, Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.setButton(BUTTON_NEUTRAL, context.getString(R.string.root_dir), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //filesListerView.goToDefaultDir();
            }
        });
        alertDialog.setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    /**
     * Display the FileListerDialog
     */
    public void show() {
        alertDialog.setTitle(ctx.getString(R.string.select_stock_recovery));

        filesListerView.start();
        alertDialog.show();
        alertDialog.getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filesListerView.goToDefaultDir();
            }
        });
    }

    /**
     * Listener to know which file/directory is selected
     *
     * @param onFileSelectedListener Instance of the Listener
     */
    public void setOnFileSelectedListener(OnFileSelectedListener onFileSelectedListener) {
        this.onFileSelectedListener = onFileSelectedListener;
    }

}
