/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.prop.robocode;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.*;


/**
 *
 * @author speed
 */
public class RoboCorner extends TeamRobot{
    
    // Creem les variables privades que necessitem
    private double x;               // Coordenada x del robot
    private double y;               // Coordenada y del robot
    private double xi;               // Coordenada inicial x del robot
    private double yi;               // Coordenada inicial y del robot
    private double width;           // Amplada del camp de batalla
    private double height;          // Alçada del camp de batalla
    private double heading;         // Array de booleans que ens dirà si una posició està ocupada o no
    private double corner=4;       // Posició del robot en el camp de batalla | S'inicialitza a quatre però el kamikaze li comunicarà a quin corner ha d'anar {0.1.2.3}
    private double gunHeading;     // Direcció del canó del robot
    private double radarHeading;   // Direcció del radar del robot
    private String nomLider=null;
    private Boolean kamikaze=false;
    /////////////variables del kamikaze exclusivament
    private Posicio[] posicions;
    private Integer posicionsRebudes = 0;
    private Boolean[] ocupat;       // Array de booleans que ens dirà si una posició està ocupada o no
    private Integer position;       // Posició del robot en el camp de batalla
    private trigonometry t=new trigonometry();
    private RobotStatus robotStatus;
    
    public void run(){
        kamikaze=kamikaze();
        if(kamikaze){
            try {
                iniKamikaze();
            } catch (IOException ex) {
                Logger.getLogger(Kamikaze.class.getName()).log(Level.SEVERE, null, ex);
            }

            while(true){
                // El kamikaze comença a seguir al robot que està més a prop
                target_kamikaze();
            }
        }else{
            // El radar segueix el moviment del cano     
            setAdjustRadarForRobotTurn(false);
            // El cano segueix el moviment del robot
            setAdjustGunForRobotTurn(false);
            // El radar segueix el moviment del cano
            setAdjustRadarForGunTurn(false);
            
            try {
                // En primer lloc el robot s'orienta cap a la seva posició inicial (el corner que està mes a prop)
                goToCorner();
            } catch (IOException ex) {
                Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
            }
            // El radar no segueix el moviment del robot
            setAdjustRadarForRobotTurn(true);
            // El canó no segueix el moviment del robot
            setAdjustGunForRobotTurn(true);
            // El radar segueix el moviment del canó
            setAdjustRadarForGunTurn(false);
            // El radar segueix el moviment del robot
            setAdjustRadarForRobotTurn(false);
            // El canó segueix el moviment del robot
            setAdjustGunForRobotTurn(false);
            // El radar no segueix el moviment del canó
            setAdjustRadarForGunTurn(true);

            while(true){
                try {
                    // El robot es queda a la cantonada i dispara a tots els robots que es trobin a prop (realitzant un moviment sentinella)
                    camperState();
                } catch (IOException ex) {
                    Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public Boolean kamikaze(){
        setTurnRight(360); 
        execute();
        xi = getX();    // Obtenim coordenada x del robot
        yi = getY();    // Obtenim coordenada y del robot
        posicions = new Posicio[4]; // Inicialitzem l'array de posicions
        for(int i=0;i<4;++i)posicions[i]=null;
        doNothing();
        try {
            broadcastMessage(new Missatge("Estic aquí",xi,yi));
        } catch (IOException ex) {
            Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
        }
        doNothing();
        
        while(posicionsRebudes<4){
            try {
                broadcastMessage(new Missatge("Necessito dades"));
            } catch (IOException ex) {
                Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
            }
           doNothing();
        }
        return socKamikaze();
    }
    
    public Boolean socKamikaze(){
        double distance=t.distancia(xi,yi, 500, 400);
        nomLider=getName();
        for(int i=0;i<4;++i){
            double dist=t.distancia(posicions[i].getX(), posicions[i].getY(), 500, 400);
            if(distance>dist){
                nomLider=posicions[i].getName();
                distance=dist;
            }else
                if((dist==distance) && 0<(nomLider.compareTo(posicions[i].getName())))nomLider=posicions[i].getName();
        }
        write();

        return nomLider==getName();
        
    }
    
    public void write(){
        System.out.println("Tinc les posicions de:");
        for(int i=0;i<4;++i){
            System.out.println("    "+posicions[i].getName()+" amb coordenades ("+posicions[i].getX()+", "+posicions[i].getY()+")");
        }
        System.out.println("dels quals el meu lider és "+nomLider);
    }
    
    public void goToCorner() throws IOException{
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        width = getBattleFieldWidth();      // Obtenim les dimensions del camp de batalla (amplada)
        height = getBattleFieldHeight();    // Obtenim les dimensions del camp de batalla (alçada)
        heading = getHeading();             // Guardem la posició inicial del robot

        setTurnRight(360);
           execute();
        while(nomLider==null)doNothing();//que doni voltes i dispari enemics

        sendMessage(nomLider,new Missatge("A quin corner vaig capità?"/*,x,y*/));

        while(corner==4){
            sendMessage(nomLider,new Missatge("A quin corner vaig capità?"/*,x,y*/));
            doNothing();
        }

        // Es mou abaix a l'esquerra
        if (corner==0){
            moveCorner0();
        }
        
        // Es mou adalt a l'esquerra
        else if (corner==1){
            moveCorner1();
        }

        // Es mou adalt a la dreta
        else if (corner==2){
            moveCorner2();
        }
        
        // Es mou abaix a la dreta
        else if (corner==3){
            moveCorner3();
        }
    }
    
    public void moveCorner0() throws IOException{
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        heading = getHeading();
        if(x==20 && y==20)return;
        goTo(20,20,true);
        turnRight(360-getHeading());
    }

    public void moveCorner1() throws IOException{
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        heading = getHeading();
        if(x==20 && y==(height-20))return;
        goTo(20,getBattleFieldHeight()-20,false);
        turnRight(180-getHeading());
    }

    public void moveCorner2() throws IOException{
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        heading = getHeading();
        if(x==width-20 && y==height-20)return;
        goTo(getBattleFieldWidth()-20,getBattleFieldHeight()-20,false);
        turnRight(180-getHeading());
    }   
    
    public void goTo(double X, double Y,Boolean abaix){
        double mvx=X-getX(),mvy=Y-getY();
        double distance = Math.sqrt(Math.pow(mvx,2)+Math.pow(mvy,2));
        double headingg = Math.toDegrees(Math.atan(mvx/mvy));
        if (abaix)headingg+=180;
        turnRight(headingg-getHeading());
        ahead(distance);
    }
    
    public void moveCorner3() throws IOException{
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        if(x==width-20 && y==20)return;
        goTo(getBattleFieldWidth()-20,20,true);
        turnRight(360-getHeading());
    }
    public void camperState() throws IOException{
        // Si el robot es troba a la posició 0 o 2, fa el moviment sentinella (gira a l'esquerra 180º, es mou 189 posicions, 
        // torna a girar a la dreta 180º i es mou 189 posicions) un cop realitzat el moviment sentinella, executa un gir de 180º per
        // disparar a tots els robots que es trobin al seu voltant

        if(corner == 0 || corner == 2){
            setAdjustRadarForGunTurn(false);
            // Obtenim el heading del robot i calculam el heading que ha de tenir per mirar cap a la posició 1 o 3
            turnGunRight(90);
            turnGunLeft(90);  
            ahead(189);
            turnGunRight(180);
            turnGunLeft(180); 
            //back(189);
            if (corner == 0){
                moveCorner0();
            }
            else moveCorner2();
        }

        // Si el robot es troba a la posició 1 o 3, fa el moviment sentinella (gira a la dreta 180º, es mou 189 posicions, 
        // torna a girar a l'esquerra 180º i es mou 189 posicions) un cop realitzat el moviment sentinella, executa un gir de 180º per
        // disparar a tots els robots que es trobin al seu voltant
        else if(corner == 1 || corner == 3){
            setAdjustRadarForGunTurn(false);
            turnGunLeft(90);
            turnGunRight(90); 
            ahead(189);
            turnGunLeft(180);
            turnGunRight(180);  
            if (corner == 1){
                moveCorner1();
            } 
            else moveCorner3();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e){
        x = getX();
        y = getY();
        if (isTeammate(e.getName())){
            // Obtenim posició del robot aliat
           double posicio_x = x + e.getDistance() * Math.sin(Math.toRadians(getHeading() + e.getBearing()));
           double posicio_y = y + e.getDistance() * Math.cos(Math.toRadians(getHeading() + e.getBearing()));
           if(posicio_x-x <= 40 && posicio_x-x >= -40 && posicio_y-y <= 40 && posicio_y-y >= -40){
                // Si es troba aprop del corner 2
                if(width-x >= width-80 || height-y >= height-80){
                    turnRight(90);
                    ahead(80);
                }
                // Si es troba aprop del corner 0
                if(x >= 80 || y >= 80){
                    turnRight(90);
                    ahead(80);
                }
                // Si es troba aprop del corner 1
                if(x >= 80 || height-y >= height-80){
                    turnLeft(90);
                    ahead(80);
                }
                // Si es troba aprop del corner 3
                if(width-x >= width-80 || y >= 80){
                    turnRight(90);
                    ahead(80);
                }
           }
           return;
 
        }else{
            if(kamikaze){
                // Si el robot escanejat és un enemic, el seguim
                if (e.getDistance() < 0){
                    // Si l'enemic està a una distància inferior a 100, el seguim
                    // getBearing() ens retorna la direcció del robot escanejat respecte el nostre robot (en graus)
                    setTurnRight(e.getBearing());
                    setAhead(e.getDistance());
                    fire(3);
                }
                else{
                    // Si l'enemic està a una distància superior a 100, el seguim
                    setTurnRight(e.getBearing());
                    setAhead(e.getDistance());
                    fire(3);
                }
            }
            else{
                fire(3);
            }
        }
    }
    
    public void onMessageReceived(MessageEvent e){
        
        try {
            Missatge M = (Missatge) e.getMessage();
            String m=M.getText();
            switch(m){
                case "Ves al corner":
                    corner = M.getX();
                    break;
                case "Estic aquí":
                    nouMembre(e.getSender(),M.getX(),M.getY());
                    break;
                case "Necessito dades":
                    sendMessage(e.getSender(),new Missatge("Toma dades",xi,yi));
                    System.out.println("envio la meva posi"+xi+" "+yi);
                    doNothing();
                    break;
                case "Toma dades":
                    nouMembre(e.getSender(),M.getX(),M.getY());
                    doNothing();
                    break;
                case "A quin corner vaig capità?":
                    if(posicionsRebudes<4) nouMembre(e.getSender(),M.getX(),M.getY());  // Incrementar posicionsRebudes
                    else{
                        sendMessage(e.getSender(),new Missatge("Ves al corner",posicio(e.getSender()),(double)0));
                    }
                    break;
                default:
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void nouMembre(String name,double xx,double yy){
        for(int i=0;i<4;++i){
            if(posicions[i]!=null && posicions[i].getName()==name)return;
        }
        posicions[posicionsRebudes]=new Posicio(name, xx, yy,(double)4);    // Obtener nombre y posicion y agregarlo al array posicions
        ++posicionsRebudes;
    }
    
    public double posicio(String name){
        for(int i=0;i<4;++i){
            if(posicions[i]!=null && posicions[i].getName()==name)return posicions[i].getZ();
        }
        return 4;
    }
    
    
     public void onStatus(StatusEvent e) {
        this.robotStatus = e.getStatus();
    } 
    
    public void iniKamikaze() throws IOException{
        enviaPosicions();
    }
    
    public void enviaPosicions() throws IOException{
        double distancia,distanciaMin=2000000;
        Integer posicio = 7;
        for(int i = 0;i<4;++i){
            if(posicions[i].getZ()==4){
                distancia=t.distancia(posicions[i].getX(),posicions[i].getY(),(double)0,(double)0);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves al corner",(double)0,(double)0));
        posicions[posicio].setZ(0);
        distanciaMin=2000000;
        for(int i = 0;i<4;++i){
            if(posicions[i].getZ()==4){
                distancia=t.distancia(posicions[i].getX(),posicions[i].getY(),(double)0,(double)800);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves al corner",(double)1,(double)0));
        posicions[posicio].setZ(1);
        distanciaMin=2000000;
        for(int i = 0;i<4;++i){
            if(posicions[i].getZ()==4){
                distancia=t.distancia(posicions[i].getX(),posicions[i].getY(),(double)1000,(double)800);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves al corner",(double)2,(double)0));
        posicions[posicio].setZ(2);
        distanciaMin=2000000;
        for(int i = 0;i<4;++i){
            if(posicions[i].getZ()==4){
                distancia=t.distancia(posicions[i].getX(),posicions[i].getY(),(double)1000,(double)0);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves al corner",(double)3,(double)0));
        posicions[posicio].setZ(3);
    }
    
    public void target_kamikaze(){
        // En aquesta funció el radar gira 360º i quan troba un enemic, el segueix mitjançant la funció onScannedRobot        
        // El radar segueix el moviment del cano
        setAdjustRadarForGunTurn(false);
        turnGunRight(360);
        doNothing();
    }
    
    public Boolean robotllegit(String n){
        for(int i=0;i<4;++i){
        if(posicions[i]!=null && posicions[i].getName()==n)return true;
        }
        return false;
    }
        
    public void onHitRobot(HitRobotEvent e) {
        if(isTeammate(e.getName())){
            turnRight(90);
            ahead(30);
        }else{
            if (e.getBearing() > -10 && e.getBearing() < 10) {
                fire(3);
            }else{
                fire(1);
            }
        }
    }

}

