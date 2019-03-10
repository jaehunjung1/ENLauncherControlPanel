package hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.util.Locale;

public class Utilities {

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp*density);
    }

    public static String colorHex(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "#%02X%02X%02X", r, g, b);
    }

    public static Drawable getDrawableFromString(Context c, String ImageName) {
        return ContextCompat.getDrawable(
                c, c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName())
        );
    }
}
