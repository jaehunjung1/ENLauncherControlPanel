package hcil.snu.ac.kr.enlaunchercontrolpanel;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import hcil.snu.ac.kr.enlaunchercontrolpanel.Animations.ValueAnimatorFactory;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.AggregatedENAView;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.ENAView;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.IndependentENAView;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.VisualParamContainer;
import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;

public class AuraPreview extends ConstraintLayout {
    private Context context;
    private ImageView appIconView;
    private ArrayList<ENAView> enavList;
    private VisualParamContainer visualParamContainer;

    private ArrayList<Integer> enavDataList;

    private int EAAVSIZE = 60; // EAAV Size
    private int ENAVSIZE = 10; // Independent ENAV Size
    private int DISTANCE = 1; // Closest Distance between EAAV and Independent ENAV

    /*
    * Overriding Necessary Functions and Constructor from ViewGroup
    */
    public AuraPreview(Context context) {
        super(context);
        this.context = context;
        EAAVSIZE = Utilities.dpToPx(context, EAAVSIZE);
        ENAVSIZE = Utilities.dpToPx(context, ENAVSIZE);
        DISTANCE = Utilities.dpToPx(context, DISTANCE);
    }

    public AuraPreview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        EAAVSIZE = Utilities.dpToPx(context, EAAVSIZE);
        ENAVSIZE = Utilities.dpToPx(context, ENAVSIZE);
        DISTANCE = Utilities.dpToPx(context, DISTANCE);
    }

    /*
    * Preview Drawing Logic
    * setEAAV => sets App Icon View (currently just imageView)
    * setENAVList => sets Notification Views (currently just imageView)
    */
    public ImageView getEAAV() {
        return this.appIconView;
    }

    public void setEAAV(ImageView appIconView) {
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                EAAVSIZE,
                EAAVSIZE
        );
        appIconView.setLayoutParams(lp);
        appIconView.setPadding(10, 10, 10, 10);
        this.addView(appIconView, 0);

        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        set.connect(appIconView.getId(), ConstraintSet.LEFT, this.getId(), ConstraintSet.LEFT);
        set.connect(appIconView.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.TOP);
        set.connect(appIconView.getId(), ConstraintSet.RIGHT, this.getId(), ConstraintSet.RIGHT);
        set.connect(appIconView.getId(), ConstraintSet.BOTTOM, this.getId(), ConstraintSet.BOTTOM);

        set.applyTo(this);

        this.appIconView = appIconView;
    }

    public ArrayList<ENAView> getEnavList() {
        return this.enavList;
    }

    public void setENAVList(ArrayList<Integer> dataList, VisualParamContainer container) {
        this.enavDataList = dataList;
        this.visualParamContainer = container;

        this.clearENAVList();
        this.enavList = new ArrayList<>();

        int enavListSize = dataList.size();

       switch (container.staticMode) {
           case SNAKE:
               drawSnakeMode(dataList, container, enavListSize);
               break;
           default:
               Log.e("setENAVList ERROR", "StaticMode not specified");
               break;
       }
    }

    public void changeENAVShapeAndColor(int shape, int color) {
        visualParamContainer.enavShape = shape;
        visualParamContainer.enavColor = color;
        for (ENAView enav: enavList) {
            if (enav instanceof IndependentENAView) {
                ((IndependentENAView) enav).setShapeAndColor(shape, color);
            } else if (enav instanceof AggregatedENAView) {
                ((AggregatedENAView) enav).setColor(color);
            }
        }
    }

    public void changeKNum(int k) {
        this.visualParamContainer.kNum = k;
        this.invalidateENAV();
    }

    private void clearENAVList() {
        this.removeAllViews();
        this.setEAAV(this.appIconView);
        this.enavList = null;
    }

    private void invalidateENAV() {
        this.setENAVList(this.enavDataList, this.visualParamContainer);
    }

    /*
    * SNAKE Mode Helper Function - called by setENAV
    */
    private void drawSnakeMode(ArrayList<Integer> dataList, VisualParamContainer container,
                               int enavListSize) {

        // startIndex: index of start of independent ENAVs in enavList
        int startIndex = container.kNum < 0? 0 : enavListSize - container.kNum;

        // Drawing Aggregated ENAV
        if (startIndex != 0) {
            final AggregatedENAView enav = new AggregatedENAView(getContext(), 0, startIndex);
            enav.setId(View.generateViewId());
            enav.setColor(container.enavColor);
            enav.setLayoutParams(new ConstraintLayout.LayoutParams(
                    // TODO change this according to size!!!
                    Utilities.dpToPx(context, 80),
                    Utilities.dpToPx(context, 80)
            ));
            enavList.add(enav);

            this.addView(enav);
            ConstraintSet set = new ConstraintSet();
            set.clone(this);
            set.connect(enav.getId(), ConstraintSet.LEFT, this.getId(), ConstraintSet.LEFT);
            set.connect(enav.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.TOP);
            set.connect(enav.getId(), ConstraintSet.RIGHT, this.getId(), ConstraintSet.RIGHT);
            set.connect(enav.getId(), ConstraintSet.BOTTOM, this.getId(), ConstraintSet.BOTTOM);

            set.applyTo(this);
        }

        // Drawing Independent ENAVs
        for (int i = startIndex; i < enavListSize; i++) {
            final IndependentENAView enav = new IndependentENAView(getContext());
            enav.setId(View.generateViewId());
            enav.setShapeAndColor(container.enavShape, container.enavColor);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                    ENAVSIZE,
                    ENAVSIZE
            );
            lp.circleConstraint = appIconView.getId();
            lp.circleAngle = 27 * i;
            lp.circleRadius = EAAVSIZE / 2 + ENAVSIZE / 2 + DISTANCE;
            enav.setLayoutParams(lp);
            enavList.add(enav);

            this.addView(enav);
        }

        // only for preview: last ENAV Animating
        final ENAView lastENAV = enavList.get(enavList.size() - 1);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) lastENAV.getLayoutParams();
        ValueAnimator enavAnim = ValueAnimatorFactory.rotatePivotAnimator(
                lastENAV, 1500, lp.circleAngle, 333f
        );
        enavAnim.start();
    }

}
