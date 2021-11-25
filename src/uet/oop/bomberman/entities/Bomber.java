package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

public class Bomber extends Entity {
    //public static int SPEED = 1;

    //Số mạng
    //public static int quantityLive = 3;
    //Biến đếm để hồi sinh lại
    public static int count_to_live = 0;
    public static boolean wait_revival;
    //Animation hồi sinh
    public static int ani_wait_revival = 0;
    public static boolean portal;

    public Bomber(int x, int y, Image img) {
        super( x, y, img);
    }

    @Override
    public void update() {
        if(isPause) return;

        if(wait_revival) {
            x = 32;
            y = 32;
            count_to_live++;
            if(count_to_live > 20) {
                ani_wait_revival++;
                count_to_live = 0;
                if(ani_wait_revival >= 2) {
                    ani_wait_revival = 0;
                    isLive = true;
                    wait_revival = false;
                    img = Sprite.player_down.getFxImage();
                }
            }
            img = Sprite.movingSprite(Sprite.player_down, Sprite.grass, ani_wait_revival, 2).getFxImage();
            return;
        }

        if (deadEntity()) {
            isLive = false;
        }

        if (isLive) {
            isMove = false;

            if (indexItem() != -1) {
                if (!itemBrick(indexItem())) {
                    switch (BombermanGame.getItemObject().get(indexItem()).typeItem) {
                        case 1: {
                            bombRate++;
                            System.out.println("Bomb Rate: " + bombRate);
                            break;
                        }
                        case 2: {
                            quantityBomb++;
                            System.out.println("Số Bomb: " + quantityBomb);
                            break;
                        }
                        case 3: {
                            SPEED++;
                            System.out.println("SPEED: " + SPEED);
                            break;
                        }
                        case 4: {
                            quantityLive++;
                            System.out.println("Số mạng: " + quantityLive);
                            break;
                        }
                    }
                    if (BombermanGame.getItemObject().get(indexItem()).typeItem != 5) {
                        BombermanGame.playMedia("Item.wav");
                        BombermanGame.getItemObject().remove(indexItem());
                    } else {
                        if (isDeadAllEnemy()) {
                            BombermanGame.getItemObject().remove(indexItem());
                            isNextLevel = true;
                        }
                    }

                }
            }

            if(up && canMove(x, y - SPEED)) {
                y += -SPEED;
                isMove = true;
            }
            if(down && canMove(x, y + SPEED)) {
                y += SPEED;
                isMove = true;
            }
            if(right && canMove(x + SPEED, y)) {
                x += SPEED;
                isMove = true;
            }
            if(left && canMove(x - SPEED, y)) {
                x += -SPEED;
                isMove = true;
            }

            if(isMove) {
                frame++;
                if(frame > interval) {
                    frame = 0;
                    indexAnim++;
                    if(indexAnim > 2) {
                        indexAnim = 0;
                    }
                }

                if(up) {
                    img = Sprite.movingSprite(Sprite.player_up, Sprite.player_up_1,
                            Sprite.player_up_2, indexAnim, 3).getFxImage();
                }
                if(down) {
                    img = Sprite.movingSprite(Sprite.player_down, Sprite.player_down_1,
                            Sprite.player_down_2, indexAnim, 3).getFxImage();
                }
                if(right) {
                    img = Sprite.movingSprite(Sprite.player_right, Sprite.player_right_1,
                            Sprite.player_right_2, indexAnim, 3).getFxImage();
                }
                if(left) {
                    img = Sprite.movingSprite(Sprite.player_left, Sprite.player_left_1,
                            Sprite.player_left_2, indexAnim, 3).getFxImage();
                }
            } else {
                img = Sprite.player_down.getFxImage();
            }
        } else {
            BombermanGame.playMedia("bomber_die.wav").setVolume(0.01);
            frame++;
            if(frame > 20) {
                frame = 0;
                indexAnim++;
            }
            if (indexAnim > 2) {
                if (quantityLive == 1) {
                    BombermanGame.getEntities().remove(this);
                }
                quantityLive--;
                wait_revival = true;
                indexAnim = 0;

            } else {
                img = Sprite.movingSprite(Sprite.player_dead1,
                        Sprite.player_dead2, Sprite.player_dead3, indexAnim, 3).getFxImage();
            }
        }

    }

    @Override
    public boolean canMove(int a, int b) {
        int a1 = a / 32;
        int b1 = b / 32;

        int a2 = (a + 32 - 4) / 32;
        int b2 = b / 32;

        int a3 = a / 32;
        int b3 = (b + 32 - 2) / 32;

        int a4 = (a + 32 - 4) / 32;
        int b4 = (b + 32 - 2) / 32;

        return !((isWall(a1, b1) || isBrick(a1, b1) || isBomb(a1, b1))
                || (isWall(a2, b2) || isBrick(a2, b2) || isBomb(a2, b2))
                || (isWall(a3, b3) || isBrick(a3, b3) || isBomb(a3, b3))
                || (isWall(a4, b4) || isBrick(a4, b4) || isBomb(a4, b4)));
    }

    @Override
    public boolean isBomb(int a, int b) {
        for (int i = 0; i < BombermanGame.getBombObject().size(); i++) {
            if (BombermanGame.getBombObject().get(i) instanceof Bomb
                    && BombermanGame.getBombObject().get(i).motionless) {
                if (BombermanGame.getBombObject().get(i).getX() / 32 == a
                        && BombermanGame.getBombObject().get(i).getY() / 32 == b) {
                    return true;
                }
            }
        }

        return false;
    }

    public int indexItem() {
        int a1 = x / 32;
        int b1 = y / 32;

        int a2 = (x + 32 - 10) / 32;
        int b2 = y / 32;

        int a3 = x / 32;
        int b3 = (y + 32 - 10) / 32;

        int a4 = (x + 32 - 10) / 32;
        int b4 = (y + 32 - 10) / 32;

        if (isItem(a1, b1) != -1) {
            return isItem(a1, b1);
        }
        if (isItem(a2, b2) != -1) {
            return isItem(a2, b2);
        }
        if (isItem(a3, b3) != -1) {
            return isItem(a3, b3);
        }
        if (isItem(a4, b4) != -1) {
            return isItem(a4, b4);
        }
        return -1;
    }

    //Kiểm tra xem trên item có còn brick k, truyền vào index là vị trí của item
    public boolean itemBrick(int index) {
        int item_x = BombermanGame.getItemObject().get(index).getX();
        int item_y = BombermanGame.getItemObject().get(index).getY();

        for (int i = 0; i < BombermanGame.getBrickObject().size(); i++) {
            if(BombermanGame.getBrickObject().get(i).getX() == item_x
                    && BombermanGame.getBrickObject().get(i).getY() == item_y) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deadEntity() {
        int a1 = x / 32;
        int b1 = y / 32;

        int a2 = (x + 32 - 1) / 32;
        int b2 = y / 32;

        int a3 = x / 32;
        int b3 = (y + 32 - 1) / 32;

        int a4 = (x + 32 - 1) / 32;
        int b4 = (y + 32 - 1) / 32;

        return (isExplosion(a1, b1) || isEnemy(a1, b1))
                || (isExplosion(a2, b2) || isEnemy(a2, b2))
                || (isExplosion(a3, b3) || isEnemy(a3, b3))
                || (isExplosion(a4, b4) || isEnemy(a4, b4));
    }

    public boolean isEnemy(int a, int b) {
        for (int i = 0; i < BombermanGame.getEntities().size(); i++) {
            if (BombermanGame.getEntities().get(i) instanceof Balloom
                    || BombermanGame.getEntities().get(i) instanceof Oneal) {
                if ((BombermanGame.getEntities().get(i).getX() + 16) / 32 == a
                        && (BombermanGame.getEntities().get(i).getY() + 16) / 32 == b) {
                    return true;
                }
            }
        }

        return false;
    }
}
