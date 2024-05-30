package com.example;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * JavaFX App
 */
public class App extends Application {
    int emitcnt = 0;
    Random rnd = new Random();
    ArrayList<double[]> clicks = new ArrayList<double[]>();
    ArrayList<Lighter> lights = new ArrayList<Lighter>();
    ArrayList<Foton> fotons = new ArrayList<Foton>();
    ArrayList<Counter> counters = new ArrayList<Counter>();
    ArrayList<Bezier> curves = new ArrayList<Bezier>();

    @Override
    public void start(Stage stage) throws IOException {



        //prepare window
        stage.setMaximized(true);
        stage.setTitle("LightEmitter v0.1");
        stage.getIcons().add(new Image("file:resources/lighticon4.png"));

        //prepare toolbox
        BorderPane pane = new BorderPane();
        pane.setBackground(new Background(new BackgroundFill(Color.valueOf("#232323"), null, null))); 
        VBox toolbox = new VBox();
        toolbox.setOpacity(1);
        toolbox.setBackground(new Background(new BackgroundFill(Color.valueOf("#535353"), null, null)));
        pane.setLeft(toolbox);;

        //prepare tools
            //bezier
            Tool bezier = new Tool("bezier", new Image("file:resources/beziericon2.png"));
            Rebinder bezRebinder = new Rebinder(pane, bezier, clicks);
            bezier.setOnAction(bezRebinder);
            toolbox.getChildren().add(bezier);
                bezier.click = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getButton() == MouseButton.PRIMARY){
                            clicks.add(new double[]{event.getSceneX(),event.getSceneY()});
                        }
                        else if(event.getButton() == MouseButton.MIDDLE){
                            while(clicks.size() > 6) clicks.remove(0);
                            curves.add(new Bezier(clicks, pane));
                        }              
                    }
                };
            
            //light
            Tool light = new Tool("light", new Image("file:resources/lighticon4.png"));
            Rebinder lightRebinder = new Rebinder(pane, light, clicks);
            light.setOnAction(lightRebinder);
            toolbox.getChildren().add(light);
                light.click = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getButton() == MouseButton.PRIMARY){
                            lights.add(new Lighter(event.getSceneX(), event.getSceneY()));
                            pane.getChildren().add(lights.get(lights.size() - 1));
                        }
                        else if(event.getButton() == MouseButton.SECONDARY){
                            double x = event.getSceneX(), y = event.getSceneY();
                            Lighter toremove = null;
                            for(Lighter l: lights){
                                if((l.x-x)*(l.x-x) + (l.y-y)*(l.y-y) < 6) toremove = l;
                            }
                            if(toremove != null){
                                pane.getChildren().remove(toremove);
                                lights.remove(toremove);
                            }
                        }
                    }
                };
                light.drag = null;
                light.move = null;
                light.press = null;
                light.release = null;
            
                //counter
            Tool counter = new Tool("counter", new Image("file:resources/countericon.png"));
            Rebinder cntRebinder = new Rebinder(pane, counter, clicks);
            counter.setOnAction(cntRebinder);
            toolbox.getChildren().add(counter);
                counter.press = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getButton() == MouseButton.PRIMARY){
                            clicks.add(new double[]{event.getSceneX(),event.getSceneY()});;
                        }
                    }
                };
                counter.release = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getButton() == MouseButton.PRIMARY){
                            counters.add(new Counter(clicks.get(0)[0], clicks.get(0)[1],
                            event.getSceneX(), event.getSceneY(), pane));
                        }
                        clicks.clear();
                    }
                };
                counter.click = new  EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getButton() == MouseButton.SECONDARY){
                            double x = event.getSceneX(), y = event.getSceneY();
                            Counter toremove = null;
                            for(Counter i : counters){
                                if((i.start.getCenterX() - x)*(i.start.getCenterX() - x) + (i.start.getCenterY() - y)*(i.start.getCenterY() - y) < 3 ||
                                (i.end.getCenterX() - x)*(i.end.getCenterX() - x) + (i.end.getCenterY() - y)*(i.end.getCenterY() - y) < 3){
                                    toremove = i;
                                }
                            }
                            if(toremove != null){
                                toremove.deinit();
                                counters.remove(toremove);
                            }
                        }
                    }
                };

            //nulltool
            Tool nulltool = new Tool("nulltool", new Image("file:resources/nulltoolicon.png"));
            Rebinder nullRebinder = new Rebinder(pane, nulltool, clicks);
            nulltool.setOnAction(nullRebinder);
            toolbox.getChildren().add(nulltool);

            //switch
            ToggleButton switchButton = new ToggleButton();
            switchButton.setGraphic(new ImageView(new Image("file:resources/switchicon.png")));
            toolbox.getChildren().add(switchButton);

            //foton emitter
            Timeline mover = new Timeline(new KeyFrame(Duration.millis(50), ev -> {
                ArrayList<Foton> toremove = new ArrayList<Foton>();
                for(Foton f : fotons){
                    f.move();
                    if(f.x < 0 || f.x > stage.getWidth() || f.y  < 0|| f.y > stage.getHeight()){
                        toremove.add(f);
                    }
                }
                for(Foton f: toremove){
                    fotons.remove(f);
                    pane.getChildren().remove(f);
                }

                if(emitcnt == 0){
                    if(switchButton.isSelected()){
                        for(Lighter l : lights){
                            for(int i = 0; i < l.bright; ++i){
                                double a = rnd.nextDouble()* 2 * Math.PI;
                                fotons.add(new Foton(l.x, l.y,
                                Math.cos(a)*2, Math.sin(a)*2));
                                pane.getChildren().add(fotons.get(fotons.size()-1));
                            }
                        }
                    }
                }
                emitcnt++;
                emitcnt%=5;
            }));
            mover.setCycleCount(Animation.INDEFINITE);
            mover.play();

        //bind actions due to tool
        pane.setOnMouseClicked(null);
        pane.setOnMousePressed(null);
        pane.setOnMouseMoved(null);
        pane.setOnMouseDragged(null);
        pane.setOnMouseReleased(null);

        //show everything
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}

class Tool extends Button{
    String name;

    EventHandler<MouseEvent> click, press, drag, move, release;
    public Tool(String name, Image icon){
        super();
        this.name = name;
        super.setGraphic(new ImageView(icon));
    }
}

class Rebinder implements EventHandler<ActionEvent>{
    Tool tool;
    Pane pane;
    ArrayList<double[]> clicks;

    Rebinder(Pane pane, Tool tool, ArrayList<double[]> clicks){
        this.pane = pane;
        this.tool = tool;
        this.clicks = clicks;
    }
    
    @Override
    public void handle(ActionEvent event){
        pane.setOnMouseClicked(tool.click);
        pane.setOnMousePressed(tool.press);
        pane.setOnMouseDragged(tool.drag);
        pane.setOnMouseMoved(tool.move);
        pane.setOnMouseReleased(tool.release);
        clicks.clear();
    }
}

class Foton extends Circle{
    double x, y, vx, vy;
    Foton(double x, double y, double vx, double vy){
        super(x, y, 1, Color.valueOf("yellow"));
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    void move(){
        x+=vx;
        y+=vy;

        this.setCenterX(x);
        this.setCenterY(y);
    }
}

class Lighter extends Circle{
    int bright;
    double x, y;
    Lighter(double x, double y){
        super(x, y, 5, Color.valueOf("yellow"));
        bright = 10;
        this.x = x;
        this.y = y;
    }
}

class Bezier{
    Circle[] points = new Circle[]{null, null, null, null, null, null};
    Pane pane;

    Bezier(ArrayList<double[]> points, Pane pane){
        for(int i=0; i < points.size(); ++i){
            this.points[i] = new Circle(points.get(i)[0], points.get(i)[1], 3, Color.BLUE);
            pane.getChildren().add(this.points[i]);
            this.pane = pane;
        }
        points.clear();   
    }

    void deinit(){
        for(int i=0; i < 6; ++i){
            if(points[i] != null) pane.getChildren().remove(this.points[i]);
        }
    }
}

class Counter extends Line{
    Circle start, end;
    int numpoints;
    int[] cntrs;
    Circle[] points;
    Pane pane;
    
    Counter(double x1, double y1, double x2, double y2, Pane pane){
        super(x1, y1, x2, y2);
        this.pane = pane;
        super.setStroke(Color.RED);
        start = new Circle(x1, y1, 3, Color.RED);
        end = new Circle(x2, y2, 3, Color.RED);
        numpoints = 8;
        points = new Circle[numpoints];
        for(int i = 1; i <= numpoints; ++i){
            double
            tx = (x1 * i + x2*(numpoints+1-i)) / (numpoints+1),
            ty = (y1 * i + y2*(numpoints+1-i)) / (numpoints+1); 
            points[i-1] = new Circle(tx, ty, 1.5, Color.RED);
            pane.getChildren().add(points[i-1]);
        }
        pane.getChildren().add(this);
        pane.getChildren().add(start);
        pane.getChildren().add(end);
    }

    void deinit(){
        for(int i = 0; i < numpoints; ++i){
            pane.getChildren().remove(points[i]);
        }
        pane.getChildren().remove(start);
        pane.getChildren().remove(end);
        pane.getChildren().remove(this);
    }
}