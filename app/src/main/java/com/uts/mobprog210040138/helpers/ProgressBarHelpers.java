package com.uts.mobprog210040138.helpers;

import android.view.View;
import android.widget.ProgressBar;

public class ProgressBarHelpers {

    ProgressBar progressBar;

    public ProgressBarHelpers (ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void show () {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hide () {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
