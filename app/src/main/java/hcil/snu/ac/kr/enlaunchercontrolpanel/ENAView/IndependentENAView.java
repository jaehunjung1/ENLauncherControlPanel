package hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;

import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;

public class IndependentENAView extends ENAView {
    public IndependentENAView(Context context) {
        super(context);
    }

    public void changeShapeAndColor(int shape, int color) {
        String drawableName;
        switch (shape) {
            case 0:
                drawableName = "enav_circle_shape";
                break;
            case 1:
                drawableName = "enav_square_shape";
                break;
            default:
                drawableName = "enav_circle_shape";
                break;
        }
        Drawable enavDrawable = Utilities.getDrawableFromString(
                getContext(), drawableName
        );
        enavDrawable.setColorFilter(new PorterDuffColorFilter(
                color, PorterDuff.Mode.MULTIPLY
        ));

        this.setImageDrawable(enavDrawable);
    }
}
