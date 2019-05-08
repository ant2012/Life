package ru.ant.life;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import androidx.core.content.ContextCompat;

import static java.lang.StrictMath.abs;

public class Organism {
    private final int x;
    private final int y;
    private final ShapeDrawable drawable;
    private boolean toSplit;
    private boolean toDie;

    public Organism(Context context, int x, int y, int xMargin, int yMargin, int cellMargin, int cellSize) {
        this.x = x;
        this.y = y;
        drawable = new ShapeDrawable(new RectShape());
        drawable.getPaint().setColor(ContextCompat.getColor(context, R.color.colorOrganism));

        int left = xMargin + x*(cellSize + cellMargin) + cellMargin;
        int top =  yMargin + y*(cellSize + cellMargin) + cellMargin;
        drawable.setBounds(left, top, left + cellSize, top + cellSize);
    }

    public void draw(Canvas canvas) {
        drawable.draw(canvas);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isToSplit() {
        return toSplit;
    }

    public boolean isToDie() {
        return toDie;
    }

    public boolean isNear(Organism o) {
        return abs(this.x - o.x) < 2 && abs(this.y - o.y) < 2;
    }

    public void markToSplit() {
        toSplit = true;
    }

    public void markToDie() {
        toDie = true;
    }

    public void markSplitted() {
        toSplit = false;
    }
}
