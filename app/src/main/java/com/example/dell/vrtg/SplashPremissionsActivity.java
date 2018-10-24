package com.example.dell.vrtg;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * This class is used to get permissions need for the app. It also asks the user for permission on startup.
 * This class will check if the permissions are accepted every time the app is turned on.
 * If the user changes permissions in settings, he will be asked again for permissions when he starts the app.
 */

public class SplashPremissionsActivity extends AppCompatActivity {

    /**
     * The time that the splash screen will be on the screen in milliseconds.
     */
    private int timeoutMillis = 0;

    /** The time when this {@link Activity} was created. */
    private long startTimeMillis = 0;

    /** The code used when requesting permissions */
    private static final int PERMISSIONS_REQUEST = 1234;

    /** A random number generator for the background colors. */
    private static final Random random = new Random();

    /**
     * The TextView which is used to inform the user whether the permissions are
     * granted.
     */
    private TextView textView = null;
    private static final int  textViewID  = View.generateViewId();

    /*
     * ---------------------------------------------
     *
     * Getters
     *
     * ---------------------------------------------
     */
    /**
     * Get the time (in milliseconds) that the splash screen will be on the
     * screen before starting the {@link Activity} who's class is returned by
     * {@link #getNextActivityClass()}.
     */
    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    /** Get the {@link Activity} to start when the splash screen times out. */
    @SuppressWarnings("rawtypes")
    public Class getNextActivityClass() {
        return Main_POI.class;
    }

    /**
     * Get the list of required permissions by searching the manifest. If you
     * don't think the default behavior is working, then you could try
     * overriding this function to return something like:
     *
     * <pre>
     * <code>
     * return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
     * </code>
     * </pre>
     */
    public String[] getRequiredPermissions() {
        String[] permissions = null;
        try {
            permissions = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (permissions == null) {
            return new String[0];
        } else {
            return permissions.clone();
        }
    }

    /*
     * ---------------------------------------------
     *
     * Activity Methods
     *
     * ---------------------------------------------
     */
    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /** Default creation code. */
        super.onCreate(savedInstanceState);

        /** Create the layout that will hold the TextView. */
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        /** Add a TextView and set the initial text. */
        textView = new TextView(this);
        textView.setTextSize(50);
        textView.setId(textViewID);
        textView.setText("Waiting for permissions...");
        mainLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        /** Set the background color. */
        int off = 128;
        int rest = 256 - off;
        int color = Color.argb(255, off + random.nextInt(rest), off + random.nextInt(rest), off + random.nextInt(rest));
        mainLayout.setBackgroundColor(color);

        /** Set the mainLayout as the content view */
        setContentView(mainLayout);

        /**
         * Save the start time of this Activity, which will be used to determine
         * when the splash screen should timeout.
         */
        startTimeMillis = System.currentTimeMillis();

        /**
         * On a post-Android 6.0 devices, check if the required permissions have
         * been granted.
         */
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        } else {
            startNextActivity();
        }
    }

    /**
     * See if we now have all of the required dangerous permissions. Otherwise,
     * tell the user that they cannot continue without granting the permissions,
     * and then request the permissions again.
     */
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            checkPermissions();
        }
    }

    /*
     * ---------------------------------------------
     *
     * Other Methods
     *
     * ---------------------------------------------
     */
    /**
     * After the timeout, start the {@link Activity} as specified by
     * {@link #getNextActivityClass()}, and remove the splash screen from the
     * backstack. Also, we can change the message shown to the user to tell them
     * we now have the requisite permissions.
     */
    private void startNextActivity() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                textView.setText("Permissions granted...");
            }
        });
        long delayMillis = getTimeoutMillis() - (System.currentTimeMillis() - startTimeMillis);
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashPremissionsActivity.this, getNextActivityClass()));
                finish();
            }
        }, delayMillis);
    }

    /**
     * Check if the required permissions have been granted, and
     * {@link #startNextActivity()} if they have. Otherwise
     * {@link #requestPermissions(String[], int)}.
     */
    private void checkPermissions() {
        String[] ungrantedPermissions = requiredPermissionsStillNeeded();
        if (ungrantedPermissions.length == 0) {
            startNextActivity();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(ungrantedPermissions, PERMISSIONS_REQUEST);
            }
        }
    }

    /**
     * Convert the array of required permissions to a {@link Set} to remove
     * redundant elements. Then remove already granted permissions, and return
     * an array of ungranted permissions.
     */
    @TargetApi(23)
    private String[] requiredPermissionsStillNeeded() {

        Set<String> permissions = new HashSet<String>();
        for (String permission : getRequiredPermissions()) {
            permissions.add(permission);
        }
        for (Iterator<String> i = permissions.iterator(); i.hasNext();) {
            String permission = i.next();
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(SplashPremissionsActivity.class.getSimpleName(),
                        "Permission: " + permission + " already granted.");
                i.remove();
            } else {
                Log.d(SplashPremissionsActivity.class.getSimpleName(),
                        "Permission: " + permission + " not yet granted.");
            }
        }
        return permissions.toArray(new String[permissions.size()]);
    }
}
