package hcil.snu.ac.kr.enlaunchercontrolpanel.utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;

import hcil.snu.ac.kr.enlaunchercontrolpanel.R;

public class ContToContUI extends ConstraintLayout {
    ConstraintSet set;
    VerticalRangeSeekBar leftSeekBar, rightSeekBar;
    ContMappingView contMappingView;

    boolean isLeftInverted, isRightInverted;

    public ContToContUI(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.cont_to_cont_ui, this);
        leftSeekBar = findViewById(R.id.sb_left_start);
        rightSeekBar = findViewById(R.id.sb_right_start);
        leftSeekBar.setProgress(20, 80);
        rightSeekBar.setProgress(20, 80);
        
        leftSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (leftValue == rightValue)
                    isLeftInverted = !isLeftInverted;
                contMappingView.invalidateSize(getThumbPos());
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });
        
        rightSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (leftValue == rightValue)
                    isRightInverted = !isRightInverted;
                contMappingView.invalidateSize(getThumbPos());
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });
        
        isLeftInverted = false;
        isRightInverted = false;

        leftSeekBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                float paddingLeft = dpToPx(getContext(), 7);
                float paddingRight = dpToPx(getContext(), 9);
                leftSeekBar.getViewTreeObserver().removeOnPreDrawListener(this);
                contMappingView = new ContMappingView(getContext(),
                        leftSeekBar.getLeft()+paddingLeft, rightSeekBar.getLeft()+paddingRight);
                Log.i("duh", String.valueOf(leftSeekBar.getX()));
                contMappingView.setId(View.generateViewId());
                contMappingView.setLayoutParams(new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                addView(contMappingView, 0);

                set = new ConstraintSet();
                set.clone(ContToContUI.this);
                set.connect(contMappingView.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
                set.connect(contMappingView.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
                set.connect(contMappingView.getId(), ConstraintSet.LEFT, getId(), ConstraintSet.LEFT);
                set.connect(contMappingView.getId(), ConstraintSet.RIGHT, getId(), ConstraintSet.RIGHT);
                set.applyTo(ContToContUI.this);

                contMappingView.invalidateSize(getThumbPos());

                return true;
            }
        });
    }

    private float[] getThumbPos() {
        float thumbHeight = dpToPx(getContext(), 15);
        float padding = dpToPx(getContext(), 10);
        float height = leftSeekBar.getHeight() - 2 * padding;

        float leftFirstVal = isLeftInverted? leftSeekBar.getRightSeekBar().getProgress() : leftSeekBar.getLeftSeekBar().getProgress();
        float leftSecondVal = isLeftInverted? leftSeekBar.getLeftSeekBar().getProgress() : leftSeekBar.getRightSeekBar().getProgress();
        float rightFirstVal = isRightInverted? rightSeekBar.getRightSeekBar().getProgress() : rightSeekBar.getLeftSeekBar().getProgress();
        float rightSecondVal = isRightInverted? rightSeekBar.getLeftSeekBar().getProgress() : rightSeekBar.getRightSeekBar().getProgress();


        float maxProgress = leftSeekBar.getMaxProgress();

        float leftStart = padding + thumbHeight / 2 + height * leftFirstVal / maxProgress;
        float leftEnd = padding + thumbHeight / 2 + height * leftSecondVal / maxProgress;
        float rightStart = padding + thumbHeight / 2 + height * rightFirstVal / maxProgress;
        float rightEnd = padding + thumbHeight / 2 + height * rightSecondVal / maxProgress;

        return new float[] {leftStart, rightStart, rightEnd, leftEnd};
    }


    public int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp*density);
    }
}
