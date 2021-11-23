package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

public class Bomb extends Entity {
    private boolean exploded;
    private int countToExplode = 0;
    private final int intervalToExplode = 4;

    public Bomb(int x, int y, Image img) {
        super(x, y, img);
    }

    @Override
    public void update() {
        //Pause game
        if(BombermanGame.getBomberMan().isPause) return;

        if (deadEntity()) {
            exploded = true;
        } else {
            if(BombermanGame.getBomberMan().bomb > 0) {
                frame++;
                if (frame == 7) {
                    frame = 0;
                    indexAnim++;
                    if(indexAnim > 2) {
                        indexAnim = 0;
                        countToExplode++;
                    }
                    if (countToExplode >= 4) {
                        exploded = true;
                    }
                }
                img = Sprite.movingSprite(Sprite.bomb, Sprite.bomb_1,
                        Sprite.bomb_2, indexAnim, 3).getFxImage();
            }
        }

        if (exploded) {
            BombermanGame.getBomberMan().bomb--;
            int index = indexBomb();
            Entity bombExplosion = new BombExplosion(x / 32, y / 32, Sprite.bomb_exploded.getFxImage());
            if (index != -1) {
                BombermanGame.getBombObject().set(index, bombExplosion);
            }
            removeBrick();
        }
    }

    public void removeBrick() {
        int a = x / 32;
        int b = y / 32;

        int index;

        int y_top;
        int y_bottom;
        int x_right;
        int x_left;

        boolean pre_y_top = false;
        boolean pre_y_bottom = false;
        boolean pre_x_right = false;
        boolean pre_x_left = false;

        for (int i = 1; i <= BombermanGame.getBomberMan().bombRate; i++) {
            y_top = b - i;
            y_bottom = b + i;
            x_right = a + i;
            x_left = a - i;

            //Remove brick top
            if(!pre_y_top && isBrick(a, y_top)) {
                index = indexBrick(a, y_top);
                if (index != -1) {
                    BombermanGame.getBrickObject().get(index).isLive = false;
                }
                pre_y_top = true;
            } else {
            /*
            Nếu không phải Brick kiểm tra xem có phải là Wall không
            Nếu không phải thêm hiệu ứng tia lửa top
             */

                if (!pre_y_top && !isWall(a, y_top)) {
                    Entity topExplosion;
                    if (i < BombermanGame.getBomberMan().bombRate) {
                        topExplosion = new BombExplosion(a, y_top, Sprite.explosion_vertical.getFxImage());
                        topExplosion.down = true;
                    } else {
                        topExplosion = new BombExplosion(a, y_top, Sprite.explosion_vertical_top_last.getFxImage());
                    }
                    topExplosion.up = true;
                    BombermanGame.getBombObject().add(topExplosion);
                } else {
                    pre_y_top = true;
                }
            }

            //Remove brick bottom
            if(!pre_y_bottom && isBrick(a, y_bottom)) {
                index = indexBrick(a, y_bottom);
                if (index != -1) {
                    BombermanGame.getBrickObject().get(index).isLive = false;
                }
                pre_y_bottom = true;
            } else {

                if(!pre_y_bottom && !isWall(a, y_bottom)) {
                    Entity bottomExplosion;
                    if (i < BombermanGame.getBomberMan().bombRate) {
                        bottomExplosion = new BombExplosion(a, y_bottom, Sprite.explosion_vertical.getFxImage());
                        bottomExplosion.up = true;
                    } else {
                        bottomExplosion = new BombExplosion(a, y_bottom, Sprite.explosion_vertical_down_last.getFxImage());
                    }
                    bottomExplosion.down = true;
                    BombermanGame.getBombObject().add(bottomExplosion);
                } else {
                    pre_y_bottom = true;
                }
            }

            //Remove brick right
            if(!pre_x_right && isBrick(x_right, b)) {
                index = indexBrick(x_right, b);
                if (index != -1) {
                    BombermanGame.getBrickObject().get(index).isLive = false;
                }
                pre_x_right = true;
            } else {
                if(!pre_x_right && !isWall(x_right, b)) {
                    Entity rightExplosion;
                    if(i < BombermanGame.getBomberMan().bombRate) {
                        rightExplosion = new BombExplosion(x_right, b, Sprite.explosion_horizontal.getFxImage());
                        rightExplosion.left = true;
                    } else {
                        rightExplosion = new BombExplosion(x_right, b, Sprite.explosion_horizontal_right_last.getFxImage());
                    }
                    rightExplosion.right = true;
                    BombermanGame.getBombObject().add(rightExplosion);
                } else {
                    pre_x_right = true;
                }
            }

            //Remove brick left
            if(!pre_x_left && isBrick(x_left, b)) {
                index = indexBrick(x_left, b);
                if (index != -1) {
                    BombermanGame.getBrickObject().get(index).isLive = false;
                }
                pre_x_left = true;
            } else {
                if(!pre_x_left && !isWall(x_left, b)) {
                    Entity leftExplosion;
                    if(i < BombermanGame.getBomberMan().bombRate) {
                        leftExplosion = new BombExplosion(x_left, b, Sprite.explosion_horizontal.getFxImage());
                        leftExplosion.right = true;
                    } else {
                        leftExplosion = new BombExplosion(x_left, b, Sprite.explosion_horizontal_left_last.getFxImage());
                    }
                    leftExplosion.left = true;
                    BombermanGame.getBombObject().add(leftExplosion);
                } else {
                    pre_x_left = true;
                }
            }
        }
    }

    public int indexBomb() {
        for (int i = 0; i < BombermanGame.getBombObject().size(); i++) {
            if (BombermanGame.getBombObject().get(i) instanceof Bomb) {
                if(BombermanGame.getBombObject().get(i).getX() == x
                        && BombermanGame.getBombObject().get(i).getY() == y) {
                    return i;
                }
            }
        }

        return -1;
    }

    public int indexBrick(int a, int b) {
        for (int i = 0; i < BombermanGame.getBrickObject().size(); i++) {
            if (BombermanGame.getBrickObject().get(i) instanceof Brick) {
                if(BombermanGame.getBrickObject().get(i).getX() / 32 == a
                        && BombermanGame.getBrickObject().get(i).getY() / 32 == b) {
                    return i;
                }
            }
        }

        return -1;
    }

//    //Ở bên BombermanGame add Grass lúc nhấn SPACE
//    //Tìm index Grass trong bombObject để đổi thành Explosion
//    public int indexGrass(int a, int b) {
//        for (int i = 0; i < BombermanGame.getBombObject().size(); i++) {
//            if (BombermanGame.getBombObject().get(i) instanceof Grass) {
//               if (BombermanGame.getBombObject().get(i).getX() / 32 == a
//                        && BombermanGame.getBombObject().get(i).getY() / 32 == b) {
//                   return i;
//               }
//            }
//        }
//
//        return -1;
//    }
}
