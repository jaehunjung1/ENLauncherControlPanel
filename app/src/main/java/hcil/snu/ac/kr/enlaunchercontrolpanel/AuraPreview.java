package hcil.snu.ac.kr.enlaunchercontrolpanel;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.Animations.ValueAnimatorFactory;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.AggregatedENAView;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.ENAView;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.IndependentENAView;
import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;

public class AuraPreview extends ConstraintLayout {
    private Context context;
    private ImageView appIconView;
    private ArrayList<ENAView> enavList;
    private int kNum = -1;

    /* *
     * TODO enavShape, enavColor 같은 auraView의 visual parameter들을 포괄하는 container가 필요할듯?
     * */


    /*
    * Overriding Necessary Functions and Constructor from ViewGroup
    */
    public AuraPreview(Context context) {
        super(context);
        this.context = context;
    }

    public AuraPreview(Context context, ImageView appIconView, ArrayList<ENAView> enavList, int kNum) {
        super(context);
        this.context = context;
        this.kNum = kNum;
        this.setEAAV(appIconView);
        this.setENAVList(enavList, kNum);
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
    public int getKNum() {
        return this.kNum;
    }

    public void setKNum(int k) {
        this.kNum = k;
        setENAVList(this.enavList, k);
    }

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

    public ArrayList<ENAView> getEnavList() {
        return this.enavList;
    }

    public void setENAVList(ArrayList<ENAView> enavList) {
        this.setENAVList(enavList, -1);
    }

    public void setENAVList(ArrayList<ENAView> enavList, int kNum) {
        this.clearENAVList();
        this.enavList = enavList;

        // startIndex: index of start of independent ENAVs in enavList
        int startIndex = kNum < 0? 0 : enavList.size() - kNum;

        // Drawing Aggregated Views
        if (startIndex != 0) {
            final ENAView enav = enavList.get(0);

            enav.setLayoutParams(new ConstraintLayout.LayoutParams(
                    Utilities.dpToPx(context, 80),
                    Utilities.dpToPx(context, 80)
            ));

            this.addView(enav);

            ConstraintSet set = new ConstraintSet();
            set.clone(this);
            set.connect(enav.getId(), ConstraintSet.LEFT, this.getId(), ConstraintSet.LEFT);
            set.connect(enav.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.TOP);
            set.connect(enav.getId(), ConstraintSet.RIGHT, this.getId(), ConstraintSet.RIGHT);
            set.connect(enav.getId(), ConstraintSet.BOTTOM, this.getId(), ConstraintSet.BOTTOM);

            set.applyTo(this);
        }

        // Drawing independent ENAVs
        for (int i = startIndex; i < enavList.size(); i++) {
            final ENAView enav = enavList.get(i);

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
        final ENAView lastENAV = enavList.get(enavList.size() - 1);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) lastENAV.getLayoutParams();
        ValueAnimator enavAnim = ValueAnimatorFactory.rotatePivotAnimator(
                lastENAV, 1500, lp.circleAngle, 333f
        );
        enavAnim.start();
    }

    public void changeENAVShapeAndColor(int shape, int color) {
        for (ENAView enav: enavList) {
            if (enav instanceof IndependentENAView) {
                ((IndependentENAView) enav).changeShapeAndColor(shape, color);
            } else if (enav instanceof AggregatedENAView) {
                ((AggregatedENAView) enav).changeColor(color);
            }
        }
    }

    private void clearENAVList() {
        this.removeAllViews();
        this.setEAAV(this.appIconView);
    }

}
