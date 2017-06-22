package com.example.saber.crashhandlertest;

import android.app.Application;

/**
 * Created by saber on 2017/6/19.
 */

public class MyApplicaiton extends Application {

    private static MyApplicaiton myApplicaiton;

    @Override
    public void onCreate() {
        super.onCreate();

        myApplicaiton = this;

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getsInstance();
        crashHandler.init(this);


    }

    public static MyApplicaiton getMyApplicaiton() {
        return myApplicaiton;
    }
}
