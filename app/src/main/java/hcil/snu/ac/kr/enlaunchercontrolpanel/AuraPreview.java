package hcil.snu.ac.kr.enlaunchercontrolpanel;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import hcil.snu.ac.kr.enlaunchercontrolpanel.Animations.SelfRotationAnimation;

public class AuraPreview extends ConstraintLayout {


    /*
    * Overriding Necessary Functions and Constructor from ViewGroup
    */

    public AuraPreview(Context context) {
        super(context);
    }

    public AuraPreview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AuraPreview(Context context, ImageView appIconView) {
        super(context);
        this.setEAAV(appIconView);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthPixels = View.MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
//        int heightPixels = View.MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = View.MeasureSpec.getMode(widthMeasureSpec);
//
//        super.onMeasure(
//                View.MeasureSpec.makeMeasureSpec(widthPixels, MeasureSpec.EXACTLY),
//                View.MeasureSpec.makeMeasureSpec(heightPixels, MeasureSpec.EXACTLY)
//        );
//
//        for (int i = 0; i < this.getChildCount(); i++) {
//            final View child = this.getChildAt(i);
//            child.measure(
//                    View.MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(), MeasureSpec.EXACTLY),
//                    View.MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY)
//            );
//        }
//    }
//
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        int leftPadding = 0;
//        int rightPadding = 0;
//        int topPadding = 0;
//        int bottomPadding = 0;
//
//        for (int i = 0; i < this.getChildCount(); i++) {
//            final View child = this.getChildAt(i);
//            child.layout(
//                    leftPadding, topPadding, (r - l) - rightPadding, (b - t) - bottomPadding
//            );
//        }
//    }

    /*
    * Preview Drawing Logic
    * setEAAV => sets App Icon View (currently just imageView)
    * setENAVList => sets Notification Views
    */
    public void setEAAV(ImageView appIconView) {
        this.addView(appIconView, 0);

        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        set.connect(appIconView.getId(), ConstraintSet.LEFT, this.getId(), ConstraintSet.LEFT);
        set.connect(appIconView.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.TOP);
        set.connect(appIconView.getId(), ConstraintSet.RIGHT, this.getId(), ConstraintSet.RIGHT);
        set.connect(appIconView.getId(), ConstraintSet.BOTTOM, this.getId(), ConstraintSet.BOTTOM);
        set.applyTo(this);

        Animation appIconAnim = new SelfRotationAnimation(appIconView, 10);
        appIconAnim.setDuration(1000);
        appIconAnim.setRepeatCount(Animation.INFINITE);
        appIconAnim.setRepeatMode(Animation.RESTART);
        appIconView.startAnimation(appIconAnim);
    }


}
