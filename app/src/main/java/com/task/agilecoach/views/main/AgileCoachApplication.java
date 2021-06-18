package com.task.agilecoach.views.main;

import android.app.Application;
import android.content.Context;

import com.task.agilecoach.helpers.customFont.FontConstants;
import com.task.agilecoach.helpers.customFont.FontsOverride;
import com.task.agilecoach.helpers.dataUtils.DataUtils;

public class AgileCoachApplication extends Application {

    private static Context mainContext;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mainContext = AgileCoachApplication.this;

            FontsOverride.setDefaultFont(this, FontConstants.DEFAULT, "fonts/Roboto-Medium.ttf");
            FontsOverride.setDefaultFont(this, FontConstants.DEFAULT_BOLD, "fonts/Roboto-Bold.ttf");
            FontsOverride.setDefaultFont(this, FontConstants.MONOSPACE, "fonts/Roboto-Regular.ttf");
            FontsOverride.setDefaultFont(this, FontConstants.SERIF, "fonts/Roboto-Light.ttf");
            FontsOverride.setDefaultFont(this, FontConstants.SANS_SERIF, "fonts/Roboto-Thin.ttf");

            DataUtils.loadDefaultTaskStatus(AgileCoachApplication.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Context getApplicationContextMain() {
        return mainContext;
    }
}
