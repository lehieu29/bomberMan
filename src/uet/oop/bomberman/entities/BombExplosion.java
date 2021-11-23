package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;


public class BombExplosion extends Entity {
    private boolean exploded;
    public BombExplosion(int x, int y, Image img) {
        super(x, y, img);
    }

    @Override
    public void update() {
        //Pause Game
        if(BombermanGame.getBomberMan().isPause) return;

        if(!exploded) {
            frame++;
            if(frame > 3) {
                frame = 0;
                indexAnim++;
                if(indexAnim > 2) {
                    indexAnim = 0;
                    exploded = true;
                }
            }

            if(up && down) {
                img = Sprite.movingSprite(Sprite.explosion_vertical,
                        Sprite.explosion_vertical1, Sprite.explosion_vertical2, indexAnim, 3).getFxImage();
            } else if (right && left) {
                img = Sprite.movingSprite(Sprite.explosion_horizontal,
                        Sprite.explosion_horizontal1, Sprite.explosion_horizontal2, indexAnim, 3).getFxImage();
            } else if (up) {
                img = Sprite.movingSprite(Sprite.explosion_vertical_top_last,
                        Sprite.explosion_vertical_top_last1, Sprite.explosion_vertical_top_last2, indexAnim, 3).getFxImage();
            } else if (down) {
                img = Sprite.movingSprite(Sprite.explosion_vertical_down_last,
                        Sprite.explosion_vertical_down_last1, Sprite.explosion_vertical_down_last2, indexAnim, 3).getFxImage();
            } else if (right) {
                img = Sprite.movingSprite(Sprite.explosion_horizontal_right_last,
                        Sprite.explosion_horizontal_right_last1, Sprite.explosion_horizontal_right_last2, indexAnim, 3).getFxImage();
            } else if (left) {
                img = Sprite.movingSprite(Sprite.explosion_horizontal_left_last,
                        Sprite.explosion_horizontal_left_last1, Sprite.explosion_horizontal_left_last2, indexAnim, 3).getFxImage();
            } else {
                img = Sprite.movingSprite(Sprite.bomb_exploded,
                        Sprite.bomb_exploded1, Sprite.bomb_exploded2, indexAnim, 3).getFxImage();
            }
        }


        //System.out.println("Exploded: " + exploded);
        if(exploded) {
            BombermanGame.playMedia("Bomb_exploded.wav").setVolume(0.1);
            BombermanGame.getBombObject().remove(this);
        }
    }
}
