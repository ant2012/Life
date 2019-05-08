package ru.ant.life;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.view.Display;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class LifeSurfaceDrawableView extends View {
    private final static int SURFACE_MARGIN_MIN = 5;
    private final static int CELL_MARGIN = 1;
    private final static int LOGICAL_WIDTH_MIN = 30;
    private final LifeManager lifeManager;
    private int refreshInterval = 500;

    private final List<ShapeDrawable> cells;
    private final Handler lifeCycleHandler;
    private final Runnable lifeCycleRunner;
    private ShapeDrawable mainRect;

    public LifeSurfaceDrawableView(Context context, int initialCount, int dieCount, int splitCount) {
        super(context);
        setBackgroundColor(0xff000000);
        this.setSystemUiVisibility(SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        Point size = getViewportSize(context);

        int leastAxisLength = min(size.x, size.y);
        int leastAxisCellMargins = CELL_MARGIN*(LOGICAL_WIDTH_MIN + 1);
        int cellSize = (leastAxisLength - leastAxisCellMargins) / LOGICAL_WIDTH_MIN;

        int xCellsCount = (size.x - CELL_MARGIN - 2*SURFACE_MARGIN_MIN) / (cellSize + CELL_MARGIN);
        int yCellsCount = (size.y - CELL_MARGIN - 2*SURFACE_MARGIN_MIN) / (cellSize + CELL_MARGIN);
        int xCellsLength = xCellsCount * (cellSize + CELL_MARGIN) + CELL_MARGIN;
        int yCellsLength = yCellsCount * (cellSize + CELL_MARGIN) + CELL_MARGIN;
        int xMargin = (size.x - xCellsLength) / 2;
        int yMargin = (size.y - yCellsLength) / 2;

        if(initialCount > xCellsCount * yCellsCount)
            initialCount = xCellsCount * yCellsCount;

        mainRect = new ShapeDrawable(new RectShape());
        mainRect.getPaint().setColor(ContextCompat.getColor(context, R.color.colorSurfaceBackground));
        mainRect.setBounds(xMargin, yMargin, xMargin +xCellsLength, yMargin +yCellsLength);


        cells = new ArrayList<>();
        for (int i = 0; i< xCellsCount; i++){
            for (int j = 0; j < yCellsCount; j++) {
                ShapeDrawable cell = new ShapeDrawable(new RectShape());
//                if(i==0 || j==0 || i==xCellsCount-1 || j==yCellsCount-1)
//                    cell.getPaint().setColor(ContextCompat.getColor(context, R.color.colorAccent));
//                else
                    cell.getPaint().setColor(ContextCompat.getColor(context, R.color.colorCellBackground));

                int x = xMargin + (cellSize + CELL_MARGIN) * i + CELL_MARGIN;
                int y = yMargin + (cellSize + CELL_MARGIN) * j + CELL_MARGIN;
                cell.setBounds(x, y, x+cellSize, y+cellSize);
                cells.add(cell);
            }
        }

        lifeManager = new LifeManager(context, initialCount, dieCount, splitCount, xCellsCount, yCellsCount, xMargin, yMargin, CELL_MARGIN, cellSize);

        lifeCycleHandler = new Handler();
        lifeCycleRunner = new Runnable() {
            @Override
            public void run() {
                lifeManager.step();
                LifeSurfaceDrawableView.this.invalidate();
                lifeCycleHandler.postDelayed(lifeCycleRunner, refreshInterval);
            }
        };
        lifeCycleHandler.postDelayed(lifeCycleRunner, refreshInterval);
    }

    private Point getViewportSize(Context context) {
        Display display = ((LifeSurfaceFullscreenActivity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new Point(size.x, size.y);
    }

    protected void onDraw(Canvas canvas) {
        mainRect.draw(canvas);
        for (ShapeDrawable cell : cells) {
            cell.draw(canvas);
        }
        lifeManager.draw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        lifeCycleHandler.removeCallbacks(lifeCycleRunner);
    }
}