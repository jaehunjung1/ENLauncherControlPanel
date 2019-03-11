package hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;

public class AggregatedENAView extends ENAView {
    private Paint paint;
    private RectF arcRect = new RectF();
    private int strokeWidth;

    private float aggregateStartAngle;
    private float aggregateSweepAngle;


    public AggregatedENAView(Context context) {
        super(context);
    }

    public AggregatedENAView(Context context, int index, int spanSize) {
        super(context);

        strokeWidth = Utilities.dpToPx(context, 7);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.TRANSPARENT);

        //Initialize Aggregate Angle
        aggregateStartAngle = (270 + index * 24) % 360;
        aggregateSweepAngle = 24 * spanSize - 15;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(arcRect, aggregateStartAngle, aggregateSweepAngle, false, paint);
    }

    // for now, just change color of aggregated ENAV
    public void setColor(int color) {
        paint.setColor(color);
        this.invalidate();
    }

    public float getAggregateSweepAngle() {
        return aggregateSweepAngle;
    }

    public void setAggregateSweepAngle(float angle) {
        this.aggregateSweepAngle = angle;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int radius = Utilities.dpToPx(getContext(), 35);
        arcRect.set(width / 2f - radius, height / 2f - radius,
                width / 2f + radius, height / 2f + radius);
    }
}
