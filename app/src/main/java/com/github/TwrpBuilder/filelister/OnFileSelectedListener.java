package com.github.TwrpBuilder.filelister;

import java.io.File;

/**
 * Created by root on 9/7/17.
 */

public interface OnFileSelectedListener {
    void onFileSelected(File file, String path);
}
