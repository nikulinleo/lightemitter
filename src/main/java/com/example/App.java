package com.example;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
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

import com.example.Bezier.PointWithTan;

/**
 * JavaFX App
 */
public class App extends Application {
    Random rnd = new Random();
    ArrayList<double[]> clicks = new ArrayList<double[]>();
    int emitcnt = 0;
    double MaxX, MaxY;

    @Override
    public void start(Stage stage) throws IOException {
        //prepare window
        stage.setMaximized(true);
        MaxX = stage.getWidth();
        MaxY = stage.getHeight();
        stage.setTitle("LightEmitter v0.1");
        stage.getIcons().add(new Image("file:resources/lighticon4.png"));
        BorderPane pane = new BorderPane();
        pane.setBackground(new Background(new BackgroundFill(Color.valueOf("#232323"), null, null))); 
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        
        //prepare groups for objects
        Group curvesGroup = new Group(), lightersGroup = new Group(), fotonsGroup = new Group(), countersGroup = new Group();
        pane.getChildren().add(curvesGroup);
        pane.getChildren().add(lightersGroup);
        pane.getChildren().add(countersGroup);
        pane.getChildren().add(fotonsGroup);

        //prepare arrays for ojects
        ArrayList<Bezier> curves  = new ArrayList<Bezier>();
        ArrayList<Lighter> lighters  = new ArrayList<Lighter>();
        ArrayList<Counter> counters  = new ArrayList<Counter>();
        ArrayList<Foton> fotons  = new ArrayList<Foton>();


        //prepare toolbox
        VBox toolbox = new VBox();
        toolbox.setOpacity(1);
        toolbox.setBackground(new Background(new BackgroundFill(Color.valueOf("#535353"), null, null)));
        pane.setLeft(toolbox);;

        //prepare tools
        //bezier
        {
            Tool bezier = new Tool("bezier", new Image("file:resources/beziericon2.png"));
            Rebinder bezRebinder = new Rebinder(pane, bezier, clicks);
            bezier.setOnAction(bezRebinder);
            bezier.setTooltip(new Tooltip("Рисуйте кривую Безье\nподдерживается максимум 6 точек\nЛКМ - расстановка точек\nСКМ - построение кривой"));
            toolbox.getChildren().add(bezier);
                bezier.click = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getButton() == MouseButton.PRIMARY){
                            clicks.add(new double[]{event.getSceneX(),event.getSceneY()});
                        }
                        else if(event.getButton() == MouseButton.MIDDLE){
                            while(clicks.size() > 6) clicks.remove(0);
                            if(clicks.size() >1){
                                curves.add(new Bezier(clicks, curvesGroup));
                            }
                        }  
                        else if(event.getButton() == MouseButton.SECONDARY){
                            double x = event.getSceneX(), y = event.getSceneY();
                            Bezier toremove = null;
                            for(Bezier c : curves){
                                if((c.control_points.get(0).getCenterX() - x)*(c.control_points.get(0).getCenterX() - x) + (c.control_points.get(0).getCenterY() - y)*(c.control_points.get(0).getCenterY() - y) < 100 ||
                                (c.control_points.get(c.order - 1).getCenterX() - x)*(c.control_points.get(c.order - 1).getCenterX() - x) + (c.control_points.get(c.order - 1).getCenterY() - y)*(c.control_points.get(c.order - 1).getCenterY() - y) < 100){
                                    toremove = c;
                                }
                            }
                            if(toremove != null){
                                toremove.deinit();
                                curves.remove(toremove);
                            }
                        }
                    }
                };
                bezier.drag = null;
                bezier.move = null;
                bezier.press = null;
                bezier.release = null;
        }
            
        //light
        
            Tool light = new Tool("light", new Image("file:resources/lighticon4.png"));
            Rebinder lightRebinder = new Rebinder(pane, light, clicks);
            light.setOnAction(lightRebinder);
            light.setTooltip(new Tooltip("Расставляйте светильники\nЛКМ - поставить светильник\nПКМ - убрать светильник"));
            toolbox.getChildren().add(light);
                light.click = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getButton() == MouseButton.PRIMARY){
                            lighters.add(new Lighter(event.getSceneX(), event.getSceneY(), lightersGroup));
                        }
                        else if(event.getButton() == MouseButton.SECONDARY){
                            double x = event.getSceneX(), y = event.getSceneY();
                            Lighter toremove = null;
                            for(Lighter l: lighters){
                                if((l.x-x)*(l.x-x) + (l.y-y)*(l.y-y) < 36) toremove = l;
                            }
                            if(toremove != null){
                                toremove.deinit();
                                lighters.remove(toremove);
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
            counter.setTooltip(new Tooltip("Специальный инструмент\nдля подсчета фотонов"));
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
                            event.getSceneX(), event.getSceneY(), countersGroup));
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
                                if((i.start.getCenterX() - x)*(i.start.getCenterX() - x) + (i.start.getCenterY() - y)*(i.start.getCenterY() - y) < 10 ||
                                (i.end.getCenterX() - x)*(i.end.getCenterX() - x) + (i.end.getCenterY() - y)*(i.end.getCenterY() - y) < 10){
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
            nulltool.setTooltip(new Tooltip("Пустой инструмент, чтобы\nничего не испортить"));
            toolbox.getChildren().add(nulltool);
            

        //switch
        
            ToggleButton switchButton = new ToggleButton();
            switchButton.setGraphic(new ImageView(new Image("file:resources/switchicon.png")));
            toolbox.getChildren().add(switchButton);
            switchButton.setTooltip(new Tooltip("Включайте/выключайте генерацию фотонов"));
        

        //foton emitter
        ArrayList<Foton> toremove = new ArrayList<Foton>();
            Timeline mover = new Timeline(new KeyFrame(Duration.millis(50), ev -> {
                toremove.clear();
                for(Foton f : fotons){
                    if(f.x < 0 || f.x > MaxX || f.y  < 0|| f.y > MaxY){
                        toremove.add(f);
                    }
                    else{
                        PointWithTan nearest = null;
                        double nearestd = 1000, dist;
                        for(Bezier b : curves){
                            if(f.x < b.maxx && f.x > b.minx){
                                for(PointWithTan l: b.points){
                                    if(l.x < f.x - 1.5 || l.x > f.x + 1.5) continue;
                                    dist = (f.x-l.x)*(f.x-l.x) 
                                                + (f.y-l.y)*(f.y-l.y);
                                    if(dist < 1.5 && dist < nearestd){
                                        nearest = l;
                                        nearestd = dist;
                                    }
                                }
                            }
                        }
                        if(nearest != null){
                            double tx = nearest.tx, ty= nearest.ty;
                            double nx=-ty, ny=tx; //вектор нормали в точке касания
                            double tvx = f.vx, tvy = f.vy;
                            double constpart = (tvx*tx + tvy*ty);
                            double changepart = (tvx*nx + tvy*ny);
                            f.vx = tx*constpart - nx*changepart;
                            f.vy = ty*constpart - ny*changepart;
                            double vmod = Math.sqrt(f.vx*f.vx + f.vy*f.vy);
                            f.vx *= 2/vmod;
                            f.vy *= 2/vmod;
                        }
                        f.move();
                    }
                }

                for(Foton f: toremove){
                    f.deinit();
                    fotons.remove(f);
                }

                if(emitcnt == 0){
                    if(switchButton.isSelected()){
                        for(Lighter l : lighters){
                            for(int i = 0; i < l.bright; ++i){
                                double a = rnd.nextDouble() * 2 * Math.PI;
                                fotons.add(new Foton(l.x, l.y,
                                Math.cos(a)*2, Math.sin(a)*2, fotonsGroup));
                            }
                        }
                    }
                }
                emitcnt++;
                emitcnt%=100;
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
    Group group;

    Foton(double x, double y, double vx, double vy, Group group){
        super(x, y, 1, Color.valueOf("yellow"));
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.group = group;
        group.getChildren().add(this);
    }

    void move(){
        x+=vx;
        y+=vy;

        this.setCenterX(x);
        this.setCenterY(y);
    }

    void deinit(){
        group.getChildren().remove(this);
    };
}

class Lighter extends Circle{
    int bright;
    double x, y;
    Group group;

    Lighter(double x, double y, Group group){
        super(x, y, 5, Color.valueOf("yellow"));
        bright = 5000;
        this.x = x;
        this.y = y;
        this.group = group;

        group.getChildren().add(this);
    }

    void deinit(){
        group.getChildren().remove(this);
    }
}

class Bezier{
    ArrayList<Circle> control_points = new ArrayList<Circle>();
    ArrayList<PointWithTan> points = new ArrayList<PointWithTan>();
    double minx, maxx;
    int order;
    Group group;

    class PointWithTan extends Circle{
        double x, y, tx, ty;

        PointWithTan(double x, double y, double tx, double ty){
            super(x, y, 0.5, Color.BLUE);
            double tl = Math.sqrt(tx*tx + ty*ty);
            this.x = x;
            this.y = y;
            this.tx = tx / tl;
            this.ty = ty / tl;
        }
    }

    Bezier(ArrayList<double[]> control_points, Group group){
        order = control_points.size();
        for(double[] p : control_points){
            this.control_points.add(new Circle(p[0], p[1], 2, Color.BLUE));
        }
        this.group = group;

        //drawing bezier curve with points, distance < 1
        points.add(new PointWithTan(control_points.get(0)[0], control_points.get(0)[1], control_points.get(1)[0], control_points.get(1)[1]));
        double LeftP = 0, lastx = control_points.get(0)[0], lasty = control_points.get(0)[1];
        while(LeftP < 1){
            double k;
            for(k = 1; true; k = LeftP + (k - LeftP) / 2){
                double[] p = getPointWithTan(k, control_points);
                if((p[0]-lastx)*(p[0]-lastx) + (p[1]-lasty)*(p[1]-lasty) < 1) {
                    points.add(new PointWithTan(p[0], p[1], p[2]-p[0], p[3]-p[1]));
                    lastx = p[0]; 
                    lasty = p[1];
                    LeftP = k;       
                    break;
                }
            }
        }
        points.add(new PointWithTan(control_points.get(control_points.size()-1)[0], control_points.get(control_points.size()-1)[1], control_points.get(control_points.size()-2)[0], control_points.get(control_points.size()-2)[1]));

        points.sort((PointWithTan a, PointWithTan b)->(a.getCenterX() > b.getCenterX() ? 1: -1));
        
        minx = points.get(0).getCenterX();
        maxx = points.get(points.size()-1).getCenterX();

        control_points.clear();
        
        for(PointWithTan i : points){
            group.getChildren().add(i);
        }
        for(Circle i : this.control_points){
            group.getChildren().add(i);
        }
    }

    void deinit(){
        for(PointWithTan i : points){
            group.getChildren().remove(i);
        }
        for(Circle i : this.control_points){
            group.getChildren().remove(i);
        }
    }

    private double[] getPointWithTan(double k, ArrayList<double[]> points){
        ArrayList<double[]> tpoints = new ArrayList<double[]>();
        
        for(double[] i : points){
            tpoints.add(new double[]{i[0], i[1]});
        }
        
        for(int i = points.size()-1; i > 0; i--){
            for(int j = 0; j < i; j++){
                tpoints.get(j)[0] = tpoints.get(j)[0] * (1-k) + tpoints.get(j+1)[0] * k;
                tpoints.get(j)[1] = tpoints.get(j)[1] * (1-k) + tpoints.get(j+1)[1] * k;
            }
        }
        return new double[]{tpoints.get(0)[0], tpoints.get(0)[1], tpoints.get(1)[0], tpoints.get(1)[1]};
    }
}

class Counter extends Line{ 
    Circle start, end;
    int numpoints;
    int[] cntrs;
    Circle[] points;
    Group group;
    
    Counter(double x1, double y1, double x2, double y2, Group group){
        super(x1, y1, x2, y2);
        this.group = group;
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
            group.getChildren().add(points[i-1]);
        }
        group.getChildren().add(this);
        group.getChildren().add(start);
        group.getChildren().add(end);
    }

    void deinit(){
        for(int i = 0; i < numpoints; ++i){
            group.getChildren().remove(points[i]);
        }
        group.getChildren().remove(start);
        group.getChildren().remove(end);
        group.getChildren().remove(this);
    }
}