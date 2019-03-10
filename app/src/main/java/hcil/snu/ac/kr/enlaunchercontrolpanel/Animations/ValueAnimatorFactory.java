package hcil.snu.ac.kr.enlaunchercontrolpanel.Animations;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

//TODO updatelistener와 addlistener를 서로 다른 valueanimator들이 서로 공유할 수 있도록 따로 빼기
public class ValueAnimatorFactory {
    private Context context;


    /* *
    *  app Icon의 cx, cy를 pivot으로 현재 angle -> 마지막 angle까지 원호를 그리며 이동하는 Animation
    * */
    public static ValueAnimator rotatePivotAnimator(
            final ImageView enav, long duration, float startAngle, float endAngle) {
        final ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) enav.getLayoutParams();
        final ValueAnimator anim = ValueAnimator.ofFloat(startAngle, endAngle);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final float updateAngle = (float) valueAnimator.getAnimatedValue();
                lp.circleAngle = updateAngle;
                enav.setLayoutParams(lp);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                anim.pause();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        anim.resume();
                    }
                }, 300);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                anim.setStartDelay(300);
                anim.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator());
//        anim.setRepeatMode(ValueAnimator.RESTART);

        return anim;
    }
}
