package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Random;

public class Controller {
    public Canvas canvas;
    public Text score_text;

    class Food{
        public double xPos;
        public double yPos;
        public Food(double _x, double _y){
            xPos = _x;
            yPos = _y;
        }
    }
    class Snake{
        private double xPos;
        private double yPos;

        public Snake(double _x, double _y){
            xPos = _x;
            yPos = _y;
        }

        public void setX(double xPos) {
            this.xPos = xPos;
        }

        public void setY(double yPos) {
            this.yPos = yPos;
        }

        public double getX() {
            return xPos;
        }

        public double getY() {
            return yPos;
        }
    }

    private float size = 20;

    private LinkedList<Snake> snake = new LinkedList<>();

    private boolean gameStarted;

    public enum KeyControl{
        STOP,RIGHT,LEFT,UP,DOWN
    }

    KeyControl keyControl;

    private Random rand = new Random();

    private Food food;

    @FXML
    void initialize(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Snake head = new Snake(canvas.getWidth()/2,canvas.getHeight()/2);
        keyControl = KeyControl.STOP;
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), actionEvent -> run(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        canvas.setFocusTraversable(true);
        canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    keyControl = KeyControl.UP; timeline.play(); break;
                    case DOWN:  keyControl = KeyControl.DOWN; timeline.play(); break;
                    case LEFT:  keyControl = KeyControl.LEFT; timeline.play(); break;
                    case RIGHT: keyControl = KeyControl.RIGHT; timeline.play(); break;
                    case P: timeline.pause(); break;
                }
            }
        });
        snake.add(head);
        spawn_food();
        timeline.play();
    }

    private double distance(Snake s1,Snake s2){
        return Math.sqrt((s1.getY()-s2.getY())*(s1.getY()-s2.getY())+(s1.getX()-s2.getX())*(s1.getX()-s2.getX()));
    }

    // add physics box2D in snake node.

    private void spawn_food(){
        food = new Food(rand.nextInt((int)canvas.getWidth()),rand.nextInt((int)canvas.getHeight()));
    }
    Snake prev = new Snake(0,0);
    private void run(GraphicsContext gc) {
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        Snake prev2 = new Snake(prev.getX(),prev.getY());
        for(int i = 1; i<snake.size();i++){
            prev2.setX(snake.get(i).getX());
            prev2.setY(snake.get(i).getY());
            snake.get(i).setY(prev.getY());
            snake.get(i).setX(prev.getX());
            prev.setX(prev2.getX());
            prev.setY(prev2.getY());
        }
        int index = 0;
        for(Snake s : snake){
            if(index == 0) gc.setFill(Color.LIGHTGREEN);
            else if(index > 0 && index % 2 == 0) gc.setFill(Color.ORANGE);
            else if(index > 0 && index % 2 == 1) gc.setFill(Color.YELLOW);
            gc.fillRect(s.getX(),s.getY(),size,size);
            index++;
        }

        //Spawn Food
        gc.setFill(Color.RED);
        gc.fillOval(food.xPos,food.yPos,size,size);

        // snake eat food
        if(snakeEat() <= size){
            snake.add(new Snake(snake.get(0).getX(),snake.get(0).getY()));
            spawn_food();
        }
        switch(keyControl){
            case UP: prev.setX(snake.getFirst().getX()); prev.setY(snake.getFirst().getY()); snake.get(0).setY(snake.get(0).getY()-size);  break;
            case DOWN: prev.setX(snake.getFirst().getX()); prev.setY(snake.getFirst().getY()); snake.get(0).setY(snake.get(0).getY()+size); break;
            case LEFT: prev.setX(snake.getFirst().getX()); prev.setY(snake.getFirst().getY()); snake.get(0).setX(snake.get(0).getX()-size); break;
            case RIGHT: prev.setX(snake.getFirst().getX()); prev.setY(snake.getFirst().getY()); snake.get(0).setX(snake.get(0).getX()+size); break;
            case STOP: break;
        }
        if(snake.getFirst().getX() < 0) snake.getFirst().setX(canvas.getWidth()-size);
        if(snake.getFirst().getX() > canvas.getWidth()) snake.getFirst().setX(size);
        if(snake.getFirst().getY() < 0) snake.getFirst().setY(canvas.getHeight()-size);
        if(snake.getFirst().getY() > canvas.getHeight()) snake.getFirst().setY(size);
        score_text.setText("Snake Size: "+snake.size());
    }

    private double snakeEat(){
        return Math.sqrt((snake.get(0).getY() - food.yPos) * (snake.get(0).getY() - food.yPos) + (snake.get(0).getX() - food.xPos) *(snake.get(0).getX() - food.xPos));
    }
}
