package hcil.snu.ac.kr.enlaunchercontrolpanel.ENAView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import hcil.snu.ac.kr.enlaunchercontrolpanel.Utilities.Utilities;
import hcil.snu.ac.kr.enlaunchercontrolpanel.ViewModel.StaticMode;

public class AggregatedENAView extends ENAView {
    private int data; // TODO change this to real notification data
    private StaticMode staticMode;

    private Paint paint;
    private RectF arcRect = new RectF();
    private int strokeWidth;

    private float aggregateStartAngle;
    private float aggregateSweepAngle;


    public AggregatedENAView(Context context) {
        super(context);
    }

    public AggregatedENAView(Context context, int index, int spanSize, StaticMode staticMode,
                             int data) {
        super(context);

        this.data = data;
        this.staticMode = staticMode;
        this.strokeWidth = Utilities.dpToPx(context, 7);
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        switch (staticMode) {
            case SNAKE:
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeWidth(strokeWidth);
                paint.setColor(Color.TRANSPARENT);

                aggregateStartAngle = (270 + index * 21) % 360;
                aggregateSweepAngle = 21 * spanSize - 15;
                break;
            case PIZZA:
            case PROGRESS:
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeCap(Paint.Cap.SQUARE);
                paint.setColor(Color.TRANSPARENT);

                // for pizza, spanSize : pizza slice의 총 개수
                int sliceAngle = Math.round(360f / spanSize);
                aggregateStartAngle = (270 + index * sliceAngle) % 360;
                aggregateSweepAngle = sliceAngle;
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (staticMode) {
            case SNAKE:
                canvas.drawArc(arcRect, aggregateStartAngle, aggregateSweepAngle, false, paint);
                break;
            case PIZZA:
            case PROGRESS:
                canvas.drawArc(arcRect, aggregateStartAngle, aggregateSweepAngle, true, paint);
                break;
        }
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
        switch (staticMode) {
            case SNAKE:
                int radius = Utilities.dpToPx(getContext(), 35);
                arcRect.set(width / 2f - radius, height / 2f - radius,
                        width / 2f + radius, height / 2f + radius);
                break;
            case PIZZA:
            case PROGRESS:
                arcRect.set(0, 0, width, height);
                break;
        }

    }
}
