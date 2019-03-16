package hcil.snu.ac.kr.enlaunchercontrolpanel;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import hcil.snu.ac.kr.enlaunchercontrolpanel.Animations.ValueAnimatorFactory;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.AggregatedENAView;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.ENAView;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.IndependentENAView;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView.VisualParamContainer;
import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.StaticMode;

public class AuraPreview extends ConstraintLayout {
    private Context context;
    private ImageView appIconView;
    private ArrayList<ENAView> enavList;
    private VisualParamContainer visualParamContainer;

    private ArrayList<Integer> enavDataList;

    private int EAAVSIZE = 60; // EAAV Size
    private int IENAVSIZE = 10; // Independent ENAV Size
    private int AENAVSIZE = 80; // Aggregated ENAV Size   // TODO change this according to size!!!
    private int DISTANCE = 1; // Closest Distance between EAAV and Independent ENAV

    /*
    * Overriding Necessary Functions and Constructor from ViewGroup
    */
    public AuraPreview(Context context) {
        super(context);
        this.context = context;
        EAAVSIZE = Utilities.dpToPx(context, EAAVSIZE);
        IENAVSIZE = Utilities.dpToPx(context, IENAVSIZE);
        AENAVSIZE = Utilities.dpToPx(context, AENAVSIZE);
        DISTANCE = Utilities.dpToPx(context, DISTANCE);
    }

    public AuraPreview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        EAAVSIZE = Utilities.dpToPx(context, EAAVSIZE);
        IENAVSIZE = Utilities.dpToPx(context, IENAVSIZE);
        AENAVSIZE = Utilities.dpToPx(context, AENAVSIZE);
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
           case PIZZA:
               drawPizzaMode(dataList, container, 5);
               break;
           default:
               Log.e("setENAVList ERROR", "StaticMode not specified");
               break;
       }
    }

    public void changeENAVShapeAndColor(int shape, String color) {
        visualParamContainer.enavShape = shape;
        visualParamContainer.enavColor = color;

        ArrayList<Integer> colorList = getENAVColorList(color, enavList.size());

        switch (visualParamContainer.staticMode) {
            case SNAKE:
            case PIZZA:
                for (int i = 0; i < enavList.size(); i++) {
                    ENAView enav = enavList.get(i);
                    if (enav instanceof IndependentENAView) {
                        ((IndependentENAView)enav).setShapeAndColor(shape, colorList.get(i));
                    } else if (enav instanceof AggregatedENAView) {
                        ((AggregatedENAView)enav).setColor(colorList.get(i));
                    }
                }
                break;
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

    private ArrayList<Integer> getENAVColorList(String enavColor, int enavNum) {
        ArrayList<Integer> enavColorList = new ArrayList<>();
        try {
            // enavColor가 hex code 일 경우
            int color = Integer.parseInt(enavColor);
            for (int i = 0; i < enavNum; i++) {
                enavColorList.add(color);
            }
        } catch (NumberFormatException e) {
            // e.g. enavColor == "phaedra"
            int arrayId = context.getResources()
                    .getIdentifier(enavColor, "array", context.getPackageName());
            TypedArray ta = context.getResources().obtainTypedArray(arrayId);
            for (int i = 0; i < enavNum; i++) {
                enavColorList.add(ta.getColor(i % ta.length(), 0));
            }
        }
        return enavColorList;
    }

    /*
    * SNAKE Mode Helper Function - called by setENAV
    */
    private void drawSnakeMode(ArrayList<Integer> dataList, VisualParamContainer container,
                               int enavListSize) {

        // startIndex: index of start of independent ENAVs in enavList
        int startIndex = container.kNum < 0? 0 : enavListSize - container.kNum;

        ArrayList<Integer> colorList = getENAVColorList(container.enavColor, enavListSize);

        // Drawing Aggregated ENAV
        if (startIndex != 0) {
            final AggregatedENAView enav = new AggregatedENAView(
                    getContext(), 0, startIndex, StaticMode.SNAKE, dataList.get(0)
                    );
            enav.setId(View.generateViewId());
            enav.setColor(colorList.get(0));
            enav.setLayoutParams(new ConstraintLayout.LayoutParams(
                    AENAVSIZE,
                    AENAVSIZE
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
            enav.setShapeAndColor(container.enavShape, colorList.get(i));
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                    IENAVSIZE,
                    IENAVSIZE
            );
            lp.circleConstraint = appIconView.getId();
            lp.circleAngle = 27 * i;
            lp.circleRadius = EAAVSIZE / 2 + IENAVSIZE / 2 + DISTANCE;
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

    /*
     * PIZZA Mode Helper Function - called by setENAV
     */
    private void drawPizzaMode(ArrayList<Integer> dataList, VisualParamContainer container,
                               int pizzaNum) {

        /*
        * TODO data에서 받아오도록 변경
        * pizzaNum : 독립적인 pizza slice의 개수
        * enavSizeList : 각 enav의 size
        * enavColorList : 각 enav의 color code
        */

        // 현재는 각 pizza slice의 size를 random하게 넣고 있음.
        final ArrayList<Integer> enavSizeList = new ArrayList<>();
        for (int i = 0; i < pizzaNum; i++) {
            enavSizeList.add(EAAVSIZE + Utilities.dpToPx(context, new Random().nextInt(9)));
        }

        ArrayList<Integer> colorList = getENAVColorList(container.enavColor, pizzaNum);


        for (int i = 0; i < pizzaNum; i++) {
            final AggregatedENAView enav = new AggregatedENAView(
                    getContext(), i, pizzaNum, StaticMode.PIZZA, dataList.get(i)
            );
            enav.setId(View.generateViewId());
            enav.setColor(colorList.get(i));
            enav.setLayoutParams(new ConstraintLayout.LayoutParams(
                    enavSizeList.get(i),
                    enavSizeList.get(i)
            ));
            enavList.add(enav);

            this.addView(enav, 0);
            ConstraintSet set = new ConstraintSet();

            set.clone(this);
            set.connect(enav.getId(), ConstraintSet.LEFT, this.getId(), ConstraintSet.LEFT);
            set.connect(enav.getId(), ConstraintSet.TOP, this.getId(), ConstraintSet.TOP);
            set.connect(enav.getId(), ConstraintSet.RIGHT, this.getId(), ConstraintSet.RIGHT);
            set.connect(enav.getId(), ConstraintSet.BOTTOM, this.getId(), ConstraintSet.BOTTOM);

            set.applyTo(this);
        }

        // only for preview: last ENAV Animating
        final ENAView lastENAV = enavList.get(enavList.size() - 1);
        ValueAnimator enavAnim = ValueAnimatorFactory.pizzaSizeAnimator(
                lastENAV, 2500, 1.0f, 1.15f
        );
        enavAnim.start();

    }


}
