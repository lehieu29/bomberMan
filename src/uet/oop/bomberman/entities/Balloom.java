package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

public class Balloom extends Entity {
    //private int SPEED = 1;
    private int countMove_up_down; // Đếm thời gian cho chạy lên xuống
    private boolean isUpDown;

    public Balloom(int x, int y, Image img) {
        super(x, y, img);
    }

    @Override
    public void update() {
        //Pause game
        if(BombermanGame.getBomberMan().isPause) return;

        if(deadEntity()) {
            isLive = false;
        }

        if (isLive) {
            isMove = false;

            //Đếm thời gian rồi random đi lên trên hay xuống dưới
            if (countMove_up_down == 240) {
                int rand = (int) (Math.random() * 100);
                if (rand < 30) {
                    up = true;
                    down = false;
                    isUpDown = true;
                }
                if(rand >= 70) {
                    up = false;
                    down = true;
                    isUpDown = true;
                }

                countMove_up_down = 0;
            }
            else {
                countMove_up_down++;
            }

            //Đổi hướng nếu đi vào ngõ cụt :v
            if(isUpDown) {
                //Không thể đi xuống dưới và có thể đi lên trên
                if(!canMove(x, y + SPEED) && canMove(x, y - SPEED)) {
                    up = true;
                    down = false;
                }
                //Không thể đi lên trên và có thể đi xuống dưới
                if(!canMove(x, y - SPEED) && canMove(x, y + SPEED)) {
                    up = false;
                    down = true;
                }
            }


            //Không thể di chuyển sang trái
            if(!canMove(x - SPEED, y)) {
                right = true;
            }

            //Không thể di chuyển sang phải
            if(!canMove(x + SPEED, y)) {
                left = true;
            }

            //Di chuyển lên trên xuống dưới
            if(up && canMove(x, y - SPEED)) {
                y -= SPEED;
                isMove = true;
                if (canMove(x + SPEED, y) || canMove(x - SPEED, y)) {
                    up = false;
                    down = false;
                    isUpDown = false;
                }
            }
            if(down && canMove(x, y + SPEED)) {
                y += SPEED;
                isMove = true;
                if (canMove(x + SPEED, y) || canMove(x - SPEED, y)) {
                    up = false;
                    down = false;
                    isUpDown = false;
                }
            }

            //Di chuyển trái phải
            if(right && canMove(x + SPEED, y)) {
                x += SPEED;
                isMove = true;
                right = true;
                left = false;
            }
            if(left && canMove(x - SPEED, y)) {
                x -= SPEED;
                isMove = true;
                right = false;
                left = true;
            }

            //Animation thay đổi hình ảnh
            if(isMove) {
                frame++;
                if(frame > 10) {
                    frame = 0;
                    indexAnim++;
                    if(indexAnim > 2) {
                        indexAnim = 0;
                    }
                }
                if(right) {
                    img = Sprite.movingSprite(Sprite.balloom_right1, Sprite.balloom_right2,
                            Sprite.balloom_right3, indexAnim, 3).getFxImage();
                }
                if(left) {
                    img = Sprite.movingSprite(Sprite.balloom_left1, Sprite.balloom_left2,
                            Sprite.balloom_left3, indexAnim, 3).getFxImage();
                }
            }
        } else {
            img = Sprite.balloom_dead.getFxImage();
            frame++;
            if (frame > 40) {
                BombermanGame.playMedia("EnemyDead.wav");
                BombermanGame.getBomberMan().point += 100;
                BombermanGame.getEntities().remove(this);
                if(isDeadAllEnemy()) {
                    BombermanGame.playMedia("enemyDeadAll.wav");
                }
            }
        }
    }
}
