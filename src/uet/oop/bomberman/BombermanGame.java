package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.levels.Levels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BombermanGame extends Application {
    
    public static final int WIDTH = 31;
    public static final int HEIGHT = 13;

    private static Entity bomberman;
    private static Entity bomb;

    private GraphicsContext gc;
    private GraphicsContext gcHeader; //Lưu các biến count_time, Point, Live

    private Canvas canvas;
    private Canvas canvas_Var;
    private static List<Entity> entities = new ArrayList<>();
    private static List<Entity> stillObjects = new ArrayList<>();

    //Media Player
    private MediaPlayer mediaPlayer;

    private int TIME = 20;
    private int LIVE = 3;
    private int POINT = 0;
    private int count_time = 0;
    private int count_newGame = 0;
    private int count_toLevel = 0;
    private boolean nextLevel = false;

    private boolean end = true;
    private boolean start = true;

    //Lưu bom
    private static List<Entity> bombObject = new ArrayList<>();
    //Lưu tường
    private static List<Entity> brickObject = new ArrayList<>();
    //Lưu item
    private static List<Entity> itemObject = new ArrayList<>();

    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) {
        // Tao Canvas
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        canvas_Var = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE);

        canvas.setTranslateY(32);
        gc = canvas.getGraphicsContext2D();
        gcHeader = canvas_Var.getGraphicsContext2D();

        //render header
        renderHeader();

        // Tao root container
        Group root = new Group();
        root.getChildren().add(canvas);
        root.getChildren().add(canvas_Var);

        //Nhac nen
        String path = "res/media/nhacnen.wav";
        Media media = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.1);
        //mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });

        // Tao scene
        Scene scene = new Scene(root);

        // Them scene vao stage
        stage.setScene(scene);
        stage.getIcons().add(new Image("sprites/background.png"));
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (nextLevel) {
                    end = true;
                    count_toLevel++;
                    //Hien 1s level 2
                    if (count_toLevel >= 10 && count_toLevel < 70) {
                        renderText("Level 2");
                    }
                    if (count_toLevel >= 70) {
                        count_toLevel = 0;
                        nextLevel = false;
                        end = false;
                        nextLevel();
                    }
                }

                if (!end) {
                    render();
                    update();
                }

                if (end && !start && !nextLevel) {
                    count_newGame++;
                    //2s hien new game
                    if (count_newGame >= 120) {
                        count_newGame = 0;
                        start = true;
                    }
                }

                if(start) {
                    //2s hien new game
                    if (count_newGame < 120) {
                        renderText("NEW GAME");
                    }
                    //1s hien level 1
                    if (count_newGame >= 120 && count_newGame < 180) {
                        renderText("Level 1");
                    }
                    if (count_newGame >= 180) {
                        start = false;
                        count_newGame = 0;
                        newGame();
                    }
                    count_newGame++;
                }
            }
        };

        timer.start();

        createMap(1);

        for (int i = 0; i < entities.size(); i++) {
            if(entities.get(i) instanceof Balloom) {
                System.out.println("Balloom: " + i);
            }
            if(entities.get(i) instanceof Oneal) {
                System.out.println("Oneal: " + i);
            }
            if(entities.get(i) instanceof Bomber) {
                System.out.println("Bomber: " + i);
            }
        }

        bomberman = new Bomber(1, 1, Sprite.player_right.getFxImage());
        bomberman.isLive = true;
        entities.add(bomberman);

        scene.setOnKeyPressed(keyEvent -> {
            String input = keyEvent.getText();
            switch (input) {
                case "d": {
                    bomberman.right = true;
                    break;
                }
                case "w": {
                    bomberman.up = true;
                    break;
                }
                case "s": {
                    bomberman.down = true;
                    break;
                }
                case "a": {
                    bomberman.left = true;
                    break;
                }
                case " ": {
                    if (bomberman.bomb < bomberman.quantityBomb) {
                        try {
                            playMedia("Bomb.wav");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int bomb_x = bomberman.getX() / 32;
                        int bomb_y = (bomberman.getY() + 16) / 32;
                        bomb = new Bomb(bomb_x, bomb_y, Sprite.bomb.getFxImage());
                        bomberman.bomb++;

                        //Add object Bomb
                        bombObject.add(bomb);
                    } else {
                        System.out.println("Khong the dat them bom!");
                    }
                    break;
                }
                case "p": {
                    if (!bomberman.isPause) {
                        bomberman.isPause = true;
                    } else {
                        bomberman.isPause = false;
                    }
                    System.out.println("Is Pause: " + bomberman.isPause);
                }
            }
        });

        scene.setOnKeyReleased(keyEvent -> {
            String input = keyEvent.getText();
            switch (input) {
                case "d": {
                    bomberman.right = false;
                    break;
                }
                case "w": {
                    bomberman.up = false;
                    break;
                }
                case "s": {
                    bomberman.down = false;
                    break;
                }
                case "a": {
                    bomberman.left = false;
                    break;
                }
            }
        });
    }

    public void createMap(int level) {
        Levels levels = new Levels();
        levels.addLevelToLevelList();
        levels.loadLevelFromFile(levels.levelList.get(level - 1));
        String[] map = levels.getMap();

        for (int i = 0; i < levels.getHeight(); i++) {
            for (int j = 0; j < levels.getWidth(); j++) {
                Entity object;
                char entity = map[i].charAt(j);
                switch (entity) {
                    case '#': {
                        object = new Wall(j, i, Sprite.wall.getFxImage());
                        break;
                    }
                    case '*': {
                        Entity brick = new Brick(j, i, Sprite.brick.getFxImage());
                        brick.isLive = true;
                        brickObject.add(brick);
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                    case '1': {
                        Entity balloom = new  Balloom(j, i, Sprite.balloom_left1.getFxImage());
                        balloom.right = true;
                        balloom.isLive = true;
                        entities.add(balloom);
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                    case '2': {
                        Entity oneal = new Oneal(j, i, Sprite.balloom_left1.getFxImage());
                        oneal.right = true;
                        oneal.isLive = true;
                        entities.add(oneal);
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                    case '3': {
                        Entity doll = new Doll(j, i, Sprite.balloom_left1.getFxImage());
                        doll.right = true;
                        doll.isLive = true;
                        entities.add(doll);
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                    case 'f': {
                        Entity item = new Items(j, i, Sprite.powerup_flames.getFxImage());
                        item.typeItem = 1;
                        itemObject.add(item);

                        Entity brick = new Brick(j, i, Sprite.brick.getFxImage());
                        brick.isLive = true;
                        brickObject.add(brick);
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                    case 'b': {
                        Entity item = new Items(j, i, Sprite.powerup_bombs.getFxImage());
                        item.typeItem = 2;
                        itemObject.add(item);

                        Entity brick = new Brick(j, i, Sprite.brick.getFxImage());
                        brick.isLive = true;
                        brickObject.add(brick);
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                    case 's': {
                        Entity item = new Items(j, i, Sprite.powerup_speed.getFxImage());
                        item.typeItem = 3;
                        itemObject.add(item);

                        Entity brick = new Brick(j, i, Sprite.brick.getFxImage());
                        brick.isLive = true;
                        brickObject.add(brick);
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                    case 'l': {
                        Entity item = new Items(j, i, Sprite.powerup_detonator.getFxImage());
                        item.typeItem = 4;
                        itemObject.add(item);

                        Entity brick = new Brick(j, i, Sprite.brick.getFxImage());
                        brick.isLive = true;
                        brickObject.add(brick);
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                    case 'x': {
                        Entity item = new Items(j, i, Sprite.portal.getFxImage());
                        item.typeItem = 5;
                        itemObject.add(item);

                        Entity brick = new Brick(j, i, Sprite.brick.getFxImage());
                        brick.isLive = true;
                        brickObject.add(brick);
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                    default: {
                        object = new Grass(j, i, Sprite.grass.getFxImage());
                        break;
                    }
                }
                stillObjects.add(object);
            }
        }
    }

    public void update() {
//        entities.forEach(Entity::update);
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).update();
        }

        for (int i = 0; i < bombObject.size(); i++) {
            bombObject.get(i).update();
        }
//        bombExplosion.forEach(Entity::update);
        for (int i = 0; i < brickObject.size(); i++) {
            brickObject.get(i).update();
        }

        count_time++;
        if(count_time >= 60) {
            TIME--;
            count_time = 0;
        }
        POINT = bomberman.point;
        LIVE = bomberman.quantityLive;

        if (!end) {
            endGame();
        }

        if(bomberman.isDeadAllEnemy()) {
            nextLevel = bomberman.isNextLevel;
        }
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        stillObjects.forEach(g -> g.render(gc));
        bombObject.forEach(g -> g.render(gc));
        itemObject.forEach(g -> g.render(gc));
        brickObject.forEach(g -> g.render(gc));
        entities.forEach(g -> g.render(gc));

        //gcHeader
        renderHeader();
    }

    /*
    Getter
     */
    public static List<Entity> getStillObjects() {
        return stillObjects;
    }

    public static List<Entity> getEntities() {
        return entities;
    }

    public static List<Entity> getBombObject() {
        return bombObject;
    }

    public static List<Entity> getBrickObject() {
        return brickObject;
    }

    public static List<Entity> getItemObject() {
        return itemObject;
    }

    public static Bomber getBomberMan() {
        return (Bomber) bomberman;
    }

    /*
    Sound Effect
     */

    public static MediaPlayer playMedia(String song) {
        String path = "res/media/" + song;
        Media media = new Media(new File(path).toURI().toString());
        MediaPlayer mp = new MediaPlayer(media);
        mp.setVolume(0.2);
        mp.setAutoPlay(true);

        return mp;
    }

    /*
    New Game, End Game
     */
    public void newGame() {
        clearMap();
        TIME = 300;

        bomberman = new Bomber(1, 1, Sprite.player_down.getFxImage());
        bomberman.isLive = true;
        entities.add(bomberman);
        createMap(1);
        end = false;
    }

    public void endGame() {
        if (bomberman.quantityLive == 0 || TIME == 0) {
            renderText("GAME OVER");
            end = true;
        }
    }

    public void clearMap() {
        if (!stillObjects.isEmpty()) {
            stillObjects.clear();
        }

        if (!entities.isEmpty()) {
            entities.clear();
        }

        if (!brickObject.isEmpty()) {
            brickObject.clear();
        }

        if(!bombObject.isEmpty()) {
            bombObject.clear();
        }

        if(!itemObject.isEmpty()) {
            itemObject.clear();
        }
    }

    public void renderHeader() {
        gcHeader.clearRect(0, 0, canvas_Var.getWidth(), canvas_Var.getHeight());
        //set background
        gcHeader.setFill(Color.LIGHTGREY);
        gcHeader.fillRect(0, 0, Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE);

        gcHeader.setFont(new Font(20));
        gcHeader.setFill(Color.BLACK);
        gcHeader.fillText("TIME: ", 15, 24);
        gcHeader.fillText("POINT: ", 420, 24);
        gcHeader.fillText("LIVE: ", 900, 24);

        gcHeader.setFont(new Font(20));
        gcHeader.setFill(Color.BLACK);
        gcHeader.fillText(String.valueOf(TIME), 70, 24);
        gcHeader.strokeText(String.valueOf(TIME), 70, 24);

        gcHeader.fillText(String.valueOf(POINT), 490, 24);
        gcHeader.strokeText(String.valueOf(POINT), 490, 24);

        gcHeader.fillText(String.valueOf(LIVE), 950, 24);
        gcHeader.strokeText(String.valueOf(LIVE), 950, 24);
    }

    public void renderText(String text) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gcHeader.clearRect(0, 0, canvas_Var.getWidth(), canvas_Var.getHeight());

        gcHeader.setFill(Color.BLACK);
        gcHeader.fillRect(0, 0, Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE);

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font(40));
        gc.fillText(text, 400, 185);
    }

    public void nextLevel() {
        clearMap();
        bomberman.setX(32);
        bomberman.setY(32);
        entities.add(bomberman);
        TIME = 300;
        createMap(2);
    }
}
