package com.mahmoud.popularmovies.Control;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

/**
 * Created by Mahmoud on 10/28/2016.
 */

public class Utils {

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static void getWindowDimentions(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
    }
}
