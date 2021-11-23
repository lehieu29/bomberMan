package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

public class Brick extends Entity {
    private boolean exploded;

    public Brick(int x, int y, Image img) {
        super(x, y, img);
    }

    @Override
    public void update() {
        if(!isLive && !exploded) {
            frame++;
            if(frame > 8) {
                frame = 0;
                indexAnim++;
                if(indexAnim > 2) {
                    indexAnim = 0;
                    exploded = true;
                }
            }

            img = Sprite.movingSprite(Sprite.brick_exploded,
                    Sprite.brick_exploded1, Sprite.brick_exploded2, indexAnim, 3).getFxImage();
        }
        if(exploded) {
//            BombermanGame.playMedia("brick.wav");
            BombermanGame.getBrickObject().remove(this);
        }
    }
}
