package hcil.snu.ac.kr.enlaunchercontrolpanel.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class ContMappingView extends View {
    Paint wallpaint;
    Path wallpath;

    float[] coordX;
    float[] coordY;
    public ContMappingView(Context context, float leftX, float rightX) {
        super(context);

        wallpaint = new Paint();
        wallpaint.setAntiAlias(true);
        wallpaint.setColor(Color.RED);
        wallpaint.setAlpha(150);
        wallpaint.setStyle(Paint.Style.FILL);

        wallpath = new Path();

        coordX = new float[] {leftX, rightX, rightX, leftX};
        coordY = new float[] {45, 640, 45, 640};

    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        wallpath.reset(); // only needed when reusing this path for a new build
        wallpath.moveTo(coordX[0], coordY[0]); // used for first point
        wallpath.lineTo(coordX[1], coordY[1]);
        wallpath.lineTo(coordX[2], coordY[2]);
        wallpath.lineTo(coordX[3], coordY[3]);
        wallpath.close();

        canvas.drawPath(wallpath, wallpaint);
    }

    public void invalidateSize(float[] thumbPos) {
        coordY = thumbPos;
        this.invalidate();
    }
}
