package hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities;

import android.content.Context;

public class Utilities {

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp*density);
    }

}
