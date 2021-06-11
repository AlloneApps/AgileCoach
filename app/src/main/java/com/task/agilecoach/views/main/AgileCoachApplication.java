package com.task.agilecoach.views.main;

import android.app.Application;

import com.task.agilecoach.helpers.customFont.FontConstants;
import com.task.agilecoach.helpers.customFont.FontsOverride;
import com.task.agilecoach.helpers.dataUtils.DataUtils;

public class AgileCoachApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, FontConstants.DEFAULT, "fonts/Roboto-Medium.ttf");
        FontsOverride.setDefaultFont(this, FontConstants.DEFAULT_BOLD, "fonts/Roboto-Bold.ttf");
        FontsOverride.setDefaultFont(this, FontConstants.MONOSPACE, "fonts/Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, FontConstants.SERIF, "fonts/Roboto-Light.ttf");
        FontsOverride.setDefaultFont(this, FontConstants.SANS_SERIF, "fonts/Roboto-Thin.ttf");

        DataUtils.loadDefaultTaskStatus(AgileCoachApplication.this);
    }
}
