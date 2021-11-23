package uet.oop.bomberman.entities;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

public abstract class Entity {
    //Tọa độ X tính từ góc trái trên trong Canvas
    protected int x;

    //Tọa độ Y tính từ góc trái trên trong Canvas
    protected int y;

    protected Image img;
    protected int SPEED = 1;

    public boolean up, down, right, left;
    public boolean isMove;

    //3 bien chay hieu ung Animation
    public int frame = 0;
    public int interval = 5;
    public int indexAnim = 0;

    //
    public boolean isNextLevel = false;
    //Số lượng bomb
    public int quantityBomb = 1;
    //So mang
    public int quantityLive = 3;
    public int point = 0; //Lưu Point
    public int bomb = 0;
    public int bombRate = 1;
    public boolean isLive;
    /*
    Type item = 1 thì là Flame Item
    Type item = 2 thì là Bomb Item
    Type item = 3 thì là Speed Item
    Type item = 4 thì là tăng mạng
    Type item = 5 thì là portal
     */
    public int typeItem;

    public boolean isPause;

    //Khởi tạo đối tượng, chuyển từ tọa độ đơn vị sang tọa độ trong canvas
    public Entity( int xUnit, int yUnit, Image img) {
        this.x = xUnit * Sprite.SCALED_SIZE;
        this.y = yUnit * Sprite.SCALED_SIZE;
        this.img = img;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(img, x, y);
    }
    public abstract void update();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Image getImg() {
        return img;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    //Toa do
    public boolean canMove(int a, int b) {
        int a1 = a / 32;
        int b1 = b / 32;

        int a2 = (a + 32 - 1) / 32;
        int b2 = b / 32;

        int a3 = a / 32;
        int b3 = (b + 32 - 1) / 32;

        int a4 = (a + 32 - 1) / 32;
        int b4 = (b + 32 - 1) / 32;

//        return !((isWall(a1, b1) || isBrick(a1, b1)) || (isWall(a2, b2) || isBrick(a2, b2))
//                || (isWall(a3, b3) || isBrick(a3, b3)) || (isWall(a4, b4) || isBrick(a4, b4)));

        return !((isWall(a1, b1) || isBrick(a1, b1) || isBomb(a1, b1))
                || (isWall(a2, b2) || isBrick(a2, b2) || isBomb(a2, b2))
                || (isWall(a3, b3) || isBrick(a3, b3) || isBomb(a3, b3))
                || (isWall(a4, b4) || isBrick(a4, b4) || isBomb(a4, b4)));
    }

    public boolean isBrick(int a, int b) {
        for (int i = 0; i < BombermanGame.getBrickObject().size(); i++) {
            if (BombermanGame.getBrickObject().get(i) instanceof Brick) {
                if ((BombermanGame.getBrickObject().get(i).getX() / 32) == a
                        && (BombermanGame.getBrickObject().get(i).getY() / 32) == b) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isWall(int a, int b) {
        int index = b * 31 + a;
        boolean result = BombermanGame.getStillObjects().get(index) instanceof Wall;

        return result;
    }

    public boolean isBomb(int a, int b) {
        for (int i = 0; i < BombermanGame.getBombObject().size(); i++) {
            if (BombermanGame.getBombObject().get(i) instanceof Bomb) {
                if (BombermanGame.getBombObject().get(i).getX() / 32 == a
                        && BombermanGame.getBombObject().get(i).getY() / 32 == b) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isExplosion(int a, int b) {
        for (int i = 0; i < BombermanGame.getBombObject().size(); i++) {
            if (BombermanGame.getBombObject().get(i) instanceof BombExplosion) {
                if (BombermanGame.getBombObject().get(i).getX() / 32 == a
                        && BombermanGame.getBombObject().get(i).getY() / 32 == b) {
                    return true;
                }
            }
        }

        return false;
    }

    public int isItem(int a, int b) {
        for (int i = 0; i < BombermanGame.getItemObject().size(); i++) {
            if(BombermanGame.getItemObject().get(i).getX() / 32 == a
                    && BombermanGame.getItemObject().get(i).getY() / 32 == b) {
                return i;
            }
        }
        return -1;
    }

    public boolean deadEntity() {
        int a1 = x / 32;
        int b1 = y / 32;

        int a2 = (x + 32 - 1) / 32;
        int b2 = y / 32;

        int a3 = x / 32;
        int b3 = (y + 32 - 1) / 32;

        int a4 = (x + 32 - 1) / 32;
        int b4 = (y + 32 - 1) / 32;

        return isExplosion(a1, b1) || isExplosion(a2, b2) || isExplosion(a3, b3) || isExplosion(a4, b4);
    }

    //Kiểm tra xem chết hết quái chưa
    public boolean isDeadAllEnemy() {
        for (int i = 0; i < BombermanGame.getEntities().size(); i++) {
            if(BombermanGame.getEntities().get(i) instanceof Balloom
                    || BombermanGame.getEntities().get(i) instanceof Oneal) {
                return false;
            }
        }
        return true;
    }
}
