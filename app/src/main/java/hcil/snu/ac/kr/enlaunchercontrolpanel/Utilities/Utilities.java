package hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;

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

    public static Drawable getDrawableFromString(Context c, String imageName) {
        return ContextCompat.getDrawable(
                c, c.getResources().getIdentifier(imageName, "drawable", c.getPackageName())
        );
    }

    /*
    * @Param type : "palette", "shape" ...
    */
    public static void getStaticResourceName(
            Context context, ArrayList<String> resourceArr, int indexArrID) {

        TypedArray indexArr = context.getResources().obtainTypedArray(indexArrID);
        for (int i = 0; i < indexArr.length(); i++) {

            if (indexArrID == R.array.shape_name) {
                resourceArr.add(indexArr.getString(i));
            } else {
                int id = indexArr.getResourceId(i, -1);
                if (id > 0) {
                    resourceArr.add(context.getResources().getResourceEntryName(id));
                }
            }
        }

    }

}
