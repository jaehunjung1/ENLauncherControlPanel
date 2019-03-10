package hcil.snu.ac.kr.enlaunchercontrolpanel;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.Animations.RotatingAnimation;
import hcil.snu.ac.kr.enlaunchercontrolpanel.Animations.ValueAnimatorFactory;
import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;

public class AuraPreview extends ConstraintLayout {
    private Context context;
    private ImageView appIconView;
    private ArrayList<ImageView> enavList;

    /*
    * Overriding Necessary Functions and Constructor from ViewGroup
    */
    public AuraPreview(Context context) {
        super(context);
        this.context = context;
    }

    public AuraPreview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    public AuraPreview(Context context, ImageView appIconView) {
        super(context);
        this.context = context;
        this.setEAAV(appIconView);
    }

    /*
    * Preview Drawing Logic
    * setEAAV => sets App Icon View (currently just imageView)
    * setENAVList => sets Notification Views (currently just imageView)
    */

    public ImageView getEAAV() {
        return this.appIconView;
    }

    /**
    * @param appIconView - app icon by ImageView
    */
    public void setEAAV(ImageView appIconView) {
        appIconView.setLayoutParams(new ConstraintLayout.LayoutParams(
                Utilities.dpToPx(context, 60),
                Utilities.dpToPx(context, 60)
        ));
        this.addView(appIconView, 0);

        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        set.connect(appIconView.getId(), ConstraintSet.LEFT, this.getId(), ConstraintSet.LEFT);
        set.connect(appIconView.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.TOP);
        set.connect(appIconView.getId(), ConstraintSet.RIGHT, this.getId(), ConstraintSet.RIGHT);
        set.connect(appIconView.getId(), ConstraintSet.BOTTOM, this.getId(), ConstraintSet.BOTTOM);

        set.applyTo(this);

        this.appIconView = appIconView;

        // Animating EAAV itself just for testing...
//        Animation appIconAnim = new RotatingAnimation(appIconView, 10);
//        appIconAnim.setDuration(1000);
//        appIconAnim.setRepeatCount(Animation.INFINITE);
//        appIconAnim.setRepeatMode(Animation.RESTART);
//        appIconView.startAnimation(appIconAnim);
    }

    public ArrayList<ImageView> getEnavList() {
        return this.enavList;
    }

    public void setENAVList(ArrayList<ImageView> enavList) {
        this.enavList = enavList;

        for (int i = 0; i < enavList.size(); i++) {
            final ImageView enav = enavList.get(i);

            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                    Utilities.dpToPx(context, 10),
                    Utilities.dpToPx(context, 10)
            );
            lp.circleConstraint = appIconView.getId();
            lp.circleAngle = 27 * i;
            lp.circleRadius = Utilities.dpToPx(context, 36);
            enav.setLayoutParams(lp);

            this.addView(enav);

        }

        // last ENAV Animating
        final ImageView lastENAV = enavList.get(enavList.size() - 1);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) lastENAV.getLayoutParams();
        ValueAnimator enavAnim = ValueAnimatorFactory.rotatePivotAnimator(
                lastENAV, 1500, lp.circleAngle, 333f
        );
        enavAnim.start();
    }


}
