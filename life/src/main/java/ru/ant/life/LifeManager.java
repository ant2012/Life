package ru.ant.life;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LifeManager {
    private int dieCount;
    private int splitCount;
    private Context context;
    private final int xCellCount;
    private final int yCellCount;
    private final int xMargin;
    private final int yMargin;
    private int cellMargin;
    private int cellSize;

    private List<Organism> organisms;
    private Random random;

    public LifeManager(Context context, int initialCount, int dieCount, int splitCount, int xCellCount, int yCellCount, int xMargin, int yMargin, int cellMargin, int cellSize) {
        this.context = context;
        this.dieCount = dieCount;
        this.splitCount = splitCount;
        this.xCellCount = xCellCount;
        this.yCellCount = yCellCount;
        this.xMargin = xMargin;
        this.yMargin = yMargin;

        this.cellMargin = cellMargin;
        this.cellSize = cellSize;

        organisms = new ArrayList<>();
        random = new Random(System.currentTimeMillis());
        for (int i = 0; i < initialCount; i++) {
            int x = random.nextInt(xCellCount);
            int y = random.nextInt(yCellCount);
            while (isOrganismExists(x, y)){
                x = random.nextInt(xCellCount);
                y = random.nextInt(yCellCount);
            }
            organisms.add(new Organism(context, x, y, xMargin, yMargin, cellMargin, cellSize));
        }
    }


    private boolean isOrganismExists(int x, int y) {
        return organisms.stream().anyMatch(o -> o.getX() == x && o.getY() == y);
    }

    public void draw(Canvas canvas) {
        for (Organism organism : organisms) {
            organism.draw(canvas);
        }
    }

    public void step() {
        for (Organism organism : organisms) {
            List<Organism> neighbours = organisms.stream().filter(o -> o.isNear(organism)).collect(Collectors.toList());
            if(neighbours.size()>= dieCount)
                organism.markToDie();
            if(neighbours.size()<= splitCount)
                organism.markToSplit();
        }
        List<Organism> toDie = organisms.stream().filter(Organism::isToDie).collect(Collectors.toList());
        organisms.removeAll(toDie);

        List<Organism> toSplit = organisms.stream().filter(Organism::isToSplit).collect(Collectors.toList());
        for (Organism organism : toSplit) {
            for (int i = 0; i < 10; i++) {
                int direction = random.nextInt(8);
                Organism child = findSplitPlaceByDirection(organism, direction);
                if(isOrganismExists(child)) continue;
                if(!isInSurface(child)) continue;
                organisms.add(child);
                break;
            }
            organism.markSplitted();
        }
    }

    private boolean isInSurface(Organism o) {
        return o.getX()>=0 && o.getX()<xCellCount&& o.getY() >= 0 && o.getY()<yCellCount;
    }

    private Organism findSplitPlaceByDirection(Organism o, int direction) {
        switch (direction){
            case 0: return new Organism(context, o.getX()-1, o.getY()-1, xMargin, yMargin, cellMargin, cellSize);
            case 1: return new Organism(context, o.getX(), o.getY()-1, xMargin, yMargin, cellMargin, cellSize);
            case 2: return new Organism(context, o.getX()+1, o.getY()-1, xMargin, yMargin, cellMargin, cellSize);
            case 3: return new Organism(context, o.getX()-1, o.getY(), xMargin, yMargin, cellMargin, cellSize);
            case 4: return new Organism(context, o.getX()+1, o.getY(), xMargin, yMargin, cellMargin, cellSize);
            case 5: return new Organism(context, o.getX()-1, o.getY()+1, xMargin, yMargin, cellMargin, cellSize);
            case 6: return new Organism(context, o.getX(), o.getY()+1, xMargin, yMargin, cellMargin, cellSize);
            case 7: return new Organism(context, o.getX()+1, o.getY()+1, xMargin, yMargin, cellMargin, cellSize);
            default: throw new RuntimeException(String.format("Wrong Direction [%1$s]", direction));
        }
    }

    private boolean isOrganismExists(Organism o) {
        return isOrganismExists(o.getX(), o.getY());
    }

}
