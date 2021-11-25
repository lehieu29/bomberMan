package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

public class Oneal extends Entity {
    //private int SPEED = 1;
    private int countMove_up_down; // Đếm thời gian cho chạy lên xuống
    private boolean isUpDown;
    private int count_change_speed;
    private boolean chaseBomber; //Kiểm tra xem đã đuổi Bomberman chưa để reset di chuyển
    private boolean isFind; //Kiếm tra xem đã tìm đường để đuổi Bomberman chưa?
    private boolean isPlayAttack = false;
    private int countToPlayMedia = 0;

    public Oneal(int x, int y, Image img) {
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
            if (count_change_speed >= 60) {
                SPEED = (int) (Math.random() * 2) + 1;
                count_change_speed = 0;
            } else {
                count_change_speed++;
            }

            if(!distanceBomber()) {
                if(chaseBomber) {
                    chaseBomber = false;
                    right = true;
                }

                countToPlayMedia = 0;
                isPlayAttack = false;

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
            } else {
                /*
                Đuổi Bomberman.
                 */
                if (SPEED == 1) {
                    SPEED = 2;
                }

                if (!isPlayAttack) {
                    BombermanGame.playMedia("attackwarning.wav");
                    isPlayAttack = true;
                }

                if (isPlayAttack) {
                    countToPlayMedia++;

                    if (countToPlayMedia >= 360) {
                        isPlayAttack = false;
                        countToPlayMedia = 0;
                    }
                }

//                if(SPEED == 2) {
//                    SPEED = 1;
//                }

                isFind = false;
                chaseBomber = true;

                //Cần Oneal đi sang phải để đuổi Bomberman
                if (!isFind && BombermanGame.getBomberMan().getX() / 32 > x / 32) {
                    //Nếu không thể sang phải thì đi lên hoặc xuống để tìm đường sang phải
                    if (!canMove(x + SPEED, y)) {
                        //Nếu không thể đi xuống thì đi lên, không thì đi xuống
                        if (!canMove(x, y + SPEED)) {
                            up = true;
                            down = false;
                        }
                        if (!canMove(x, y - SPEED)) {
                            up = false;
                            down = true;
                        }

                        //Nếu không thể sang phải, đi lên và đi xuống thì đi sang trái
                        if(!canMove(x, y + SPEED) && !canMove(x, y - SPEED)) {
                            right = false;
                            left = true;
                        }
                    } else {
                        right = true;
                        left = false;
                    }

                    isFind = true; //Đã tìm được hướng di chuyển thoát vòng if else của dòng 80 ra và di chuyển.
                }

                //Cần Oneal đi lên trên để đuổi Bomberman.
                if (!isFind && (BombermanGame.getBomberMan().getY() / 32) < y / 32) {
                    //Nếu không thể đi lên trên thì tìm đường sang trái hoặc sang phải
                    if (!canMove(x, y - SPEED)) {
                        //Nếu không thể đi sang trái
                        if (!canMove(x - SPEED, y)) {
                            right = true;
                            left = false;
                        }
                        //Nếu không thể đi sang phải thì đi sang trái
                        if (!canMove(x + SPEED, y)) {
                            right = false;
                            left = true;
                        }

                        //Nếu k thể lên trên, sang trái và sang phải thì đi xuống
                        if (!canMove(x - SPEED, y) && !canMove(x + SPEED, y)) {
                            up = false;
                            down = true;
                        }
                    } else {
                        up = true;
                        down = false;
                    }
                    isFind = true;
                }

                //Cần Oneal đi xuống dưới để đuổi Bomberman.
                if (!isFind && BombermanGame.getBomberMan().getY() / 32 > y / 32) {
                    //Nếu không thể đi xuống thì đi sang phải hoặc sang trái để tìm đường
                    if (!canMove(x, y + SPEED)) {
                        //Nếu không thể sang trái thì sang phải, không thì sang trái
                        if (!canMove(x - SPEED, y)) {
                            right = true;
                            left = false;
                        }
                        if (!canMove(x + SPEED, y)) {
                            right = false;
                            left = true;
                        }

                        //Nếu k thể xuống dưới, sang trái và sang phải thì đi xuống
                        if (!canMove(x - SPEED, y) && !canMove(x + SPEED, y)) {
                            up = true;
                            down = false;
                        }
                    } else {
                        up = false;
                        down = true;
                    }
                    isFind = true;
                }

                //Cần Oneal đi sang trái để đuổi Bomberman
                if (!isFind && BombermanGame.getBomberMan().getX() / 32 < x / 32) {
                    //Nếu không thể sang trái
                    if (!canMove(x - SPEED, y)) {
                        //Nếu không thể đi xuống thì đi lên, không thì đi xuống
                        if (!canMove(x, y + SPEED)) {
                            up = true;
                            down = false;
                        }
                        if (!canMove(x, y - SPEED)) {
                            up = false;
                            down = true;
                        }

                        //Nếu không thể sang trái, đi lên và đi xuống thì đi sang phải
                        if(!canMove(x, y + SPEED) && !canMove(x, y - SPEED)) {
                            right = true;
                            left = false;
                        }
                    } else {
                        right = false;
                        left = true;
                    }
                    isFind = true;
                }
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
                    img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2,
                            Sprite.oneal_right3, indexAnim, 3).getFxImage();
                }
                if(left) {
                    img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2,
                            Sprite.oneal_left3, indexAnim, 3).getFxImage();
                }
            }
        } else {
            img = Sprite.oneal_dead.getFxImage();
            frame++;
            if (frame > 40) {
                BombermanGame.playMedia("EnemyDead.wav").setVolume(0.3);
                BombermanGame.getBomberMan().point += 200;
                BombermanGame.getEntities().remove(this);
                if(isDeadAllEnemy()) {
                    BombermanGame.playMedia("enemyDeadAll.wav");
                }
            }
        }
    }

    public boolean distanceBomber() {
        //Toa do cua Bomberman
        int a1 = BombermanGame.getBomberMan().getX() / 32;
        int b1 = BombermanGame.getBomberMan().getY() / 32;

        int a2 = (BombermanGame.getBomberMan().getX() + 32 - 1) / 32;
        int b2 = BombermanGame.getBomberMan().getY() / 32;

        int a3 = BombermanGame.getBomberMan().getX() / 32;
        int b3 = (BombermanGame.getBomberMan().getY() + 32 - 1) / 32;

        int a4 = (BombermanGame.getBomberMan().getX() + 32 - 1) / 32;
        int b4 = (BombermanGame.getBomberMan().getY() + 32 - 1) / 32;

        int Oneal_x = x / 32;
        int Oneal_y = y / 32;

        return (Math.abs(Oneal_x - a1) <= 2 && Math.abs(Oneal_y - b1) <= 2)
                || (Math.abs(Oneal_x - a2) <= 2 && Math.abs(Oneal_y - b2) <= 2)
                || (Math.abs(Oneal_x - a3) <= 2 && Math.abs(Oneal_y - b3) <= 2)
                || (Math.abs(Oneal_x - a4) <= 2 && Math.abs(Oneal_y - b4) <= 2);
    }


//    public boolean distanceBomber(int a, int b, int i, String direction) {
//        //Top
//        if(!direction.equals("down")) {
//            int x_top = a;
//            int y_top = b + 32;
//            if (canMove(x_top, y_top)) {
//                if(isBomber(x_top, y_top) && i<= 5) {
//                    return true;
//                } else {
//                    i++;
//                    distanceBomber(x_top, y_top, i, "top");
//                }
//            }
//        }
//
//
//
//        //Right
//        int x_right = canMove(a + 32, b) ? a + 32 : a;
//        int y_right = b;
//
//        //Down
//        int x_down = a;
//        int y_down = canMove(a, b + 32) ? b + 32 : b;
//
//        //Left
//        int x_left = canMove(a - 32, b) ? a - 32 : a;
//        int y_left = b;
//
//
//    }
}
