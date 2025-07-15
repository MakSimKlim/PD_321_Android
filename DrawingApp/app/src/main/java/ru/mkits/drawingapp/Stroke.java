package ru.mkits.drawingapp;

import android.graphics.Paint;
import android.graphics.Path;
public class Stroke {
    Path path;
    Paint paint;

    public Stroke(int color,float strokeWidth) {
        paint = new Paint();
        path = new Path();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }
}
