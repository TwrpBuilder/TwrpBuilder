package com.github.TwrpBuilder.filelister;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.io.File;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A File Lister Dialog
 */

public class FileListerDialog {

    private AlertDialog alertDialog;

    private FilesListerView filesListerView;

    private OnFileSelectedListener onFileSelectedListener;

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

    /**
     * Creates an instance of FileListerDialog with the specified Theme
     *
     * @param context Context of the App
     * @param themeId Theme Id for the dialog
     * @return Instance of FileListerDialog
     */
    public static FileListerDialog createFileListerDialog(@NonNull Context context, int themeId) {
        return new FileListerDialog(context, themeId);
    }

    private void init(Context context) {
        filesListerView = new FilesListerView(context);
        alertDialog.setView(filesListerView);
        alertDialog.setButton(BUTTON_POSITIVE, "Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (onFileSelectedListener != null)
                    onFileSelectedListener.onFileSelected(filesListerView.getSelected(), filesListerView.getSelected().getAbsolutePath());
            }
        });
        alertDialog.setButton(BUTTON_NEUTRAL, "Default Dir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //filesListerView.goToDefaultDir();
            }
        });
        alertDialog.setButton(BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    /**
     * Display the FileListerDialog
     */
    public void show() {
        alertDialog.setTitle("Select a file");

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
