package uet.oop.bomberman.levels;

public abstract class Level {
    protected int width;
    protected int height;
    protected int level;
    protected String[] map;

    public String[] getMap() {
        return map;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLevel() {
        return level;
    }
}
