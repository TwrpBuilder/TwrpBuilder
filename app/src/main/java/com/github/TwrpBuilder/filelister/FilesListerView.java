package com.github.TwrpBuilder.filelister;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;

/**
 * Created by S.Yogesh on 14-02-2016.
 */
class FilesListerView extends RecyclerView {

    private FileListAdapter adapter;

    FilesListerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new FileListAdapter(this);
    }

    void start() {
        setAdapter(adapter);
        adapter.start();
    }

    private void setDefaultDir(File file) {
        adapter.setDefaultDir(file);
    }

    File getSelected() {
        return adapter.getSelected();
    }

    void goToDefaultDir() {
        adapter.goToDefault();
    }
}
