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
        kamikaze=this.getName().contains("(1)");
        if(kamikaze){
            try {
                iniKamikaze();
            } catch (IOException ex) {
                Logger.getLogger(Kamikaze.class.getName()).log(Level.SEVERE, null, ex);
            }

            while(true){
                target_kamikaze();
            }
        }else{
            // El radar segueix el moviment del cano     
            setAdjustRadarForRobotTurn(false);
            // El cano segueix el moviment del robot
            setAdjustGunForRobotTurn(false);
            // El radar segueix el moviment del cano
            setAdjustRadarForGunTurn(false);
            // En primer lloc el robot s'orienta cap a la seva posició inicial (el corner que està mes a prop)
            try {
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
                camperState();
            }
        }
    }
    
    public void goToCorner() throws IOException{
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        width = getBattleFieldWidth();      // Obtenim les dimensions del camp de batalla (amplada)
        height = getBattleFieldHeight();    // Obtenim les dimensions del camp de batalla (alçada)
        heading = getHeading();             // Guardem la posició inicial del robot
        //gunHeading = getGunHeading();       // posició del canó
        //radarHeading = getRadarHeading();   // posició del radar
        //System.out.println("encara no tinc lider");
        //turnGunRight(360);
        setTurnRight(360);
           execute();
        while(nomLider==null)doNothing();//que doni voltes i dispari enemics
        //System.out.println("era broma si que tinc lider");
        //System.out.println(nomLider);fahead
        //turnGunRight(360);
        sendMessage(nomLider,new Missatge("A quin corner vaig capità?"/*,x,y*/));
        //System.out.println("Broadcast enviat");
        //turnGunRight(360);
        while(corner==4){
        sendMessage(nomLider,new Missatge("A quin corner vaig capità?"/*,x,y*/));
        //System.out.println("Broadcast enviat");
        doNothing();
        }//igual que dues linies aldamunt
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
        turnRight(270-heading);
        ahead(x-20);
        turnLeft(90);
        ahead(y-20);
        moveCorner0();
    }

    public void moveCorner1() throws IOException{
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        heading = getHeading();
        if(x==20 && y==(height-20))return;
        turnRight(270-heading);
        ahead(x-20);
        turnRight(90);
        ahead(height-y-20);
        moveCorner1();
    }

    public void moveCorner2() throws IOException{
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        heading = getHeading();
        if(x==width-20 && y==height-20)return;
        turnRight(90-heading);
        ahead(width-x-20);
        turnLeft(90);
        ahead(height-y-20);
        moveCorner2();
    }   
    
    public void moveCorner3() throws IOException{
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        heading = getHeading();
        if(x==width-20 && y==20)return;
        turnRight(90-heading);
        ahead(width-x-20);
        turnRight(90);
        ahead(y-20);
        moveCorner3();
    }
    public void camperState(){
        // Si el robot es troba a la posició 0 o 2, fa el moviment sentinella (gira a l'esquerra 180º, es mou 189 posicions, 
        // torna a girar a la dreta 180º i es mou 189 posicions) un cop realitzat el moviment sentinella, executa un gir de 180º per
        // disparar a tots els robots que es trobin al seu voltant

        if(corner == 0 || corner == 2){
            setAdjustRadarForGunTurn(false);
            ahead(189);
            turnGunRight(90);
            turnGunLeft(90);  
            back(189);
            turnGunRight(270);
            turnGunLeft(180);       
        }

        // Si el robot es troba a la posició 1 o 3, fa el moviment sentinella (gira a la dreta 180º, es mou 189 posicions, 
        // torna a girar a l'esquerra 180º i es mou 189 posicions) un cop realitzat el moviment sentinella, executa un gir de 180º per
        // disparar a tots els robots que es trobin al seu voltant
        else if(corner == 1 || corner == 3){
            setAdjustRadarForGunTurn(false);
            ahead(189);
            turnGunRight(180);
            turnGunLeft(180);  
            back(189);
            turnGunRight(180);
            turnGunLeft(180);  
        }
    }/*
    public void camperState(){
        // Si el robot es troba a la posició 0 o 2, fa el moviment sentinella (gira a l'esquerra 180º, es mou 189 posicions, 
        // torna a girar a la dreta 180º i es mou 189 posicions) un cop realitzat el moviment sentinella, executa un gir de 180º per
        // disparar a tots els robots que es trobin al seu voltant
        if(corner == 0 || corner == 2){
            turnLeft(180);
            ahead(189);
            turnRight(180);
            ahead(189);
            turnLeft(90);
            turnRight(90);       
        }

        // Si el robot es troba a la posició 1 o 3, fa el moviment sentinella (gira a la dreta 180º, es mou 189 posicions, 
        // torna a girar a l'esquerra 180º i es mou 189 posicions) un cop realitzat el moviment sentinella, executa un gir de 180º per
        // disparar a tots els robots que es trobin al seu voltant
        else if(corner == 1 || corner == 3){
            turnRight(180);
            ahead(189);
            turnLeft(180);
            ahead(189);
            turnRight(90);
            turnLeft(90);
        }
        doNothing();
    }*/

    public void onScannedRobot(ScannedRobotEvent e){
        if (isTeammate(e.getName())){
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
            }else{
                fire(3);
            }
        }
    }
    
    public void onMessageReceived(MessageEvent e){
        
            Missatge M = (Missatge) e.getMessage();
            String m=M.getText();
            switch(m){
                case "Ves al corner":
                    corner = M.getX();
                case "Soc el líder":
                    nomLider=e.getSender();
                case "A quin corner vaig capità?":
                        if(posicionsRebudes<4 && nouMembre(e.getSender())){
                            posicions[posicionsRebudes]=new Posicio(e.getSender(), M.getX(), M.getY(),(double)4);//obtener nombre y posicion y agregarlo al array posicions
                            ++posicionsRebudes;// incrementar posicionsRebudes
        //                System.out.println("posicions rebudes incrementat, he afegit a "+e.getSender());
                        }else{
                            try {
                                sendMessage(e.getSender(),new Missatge("Ves al corner",posicio(e.getSender()),(double)0));
                            } catch (IOException ex) {
                                Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("Envio a "+e.getSender()+" al corner "+posicio(e.getSender()));
                        }
                default:
            }
    }
    
    public Boolean nouMembre(String name){
        for(int i=0;i<4;++i){
            if(posicions[i]!=null && posicions[i].getName()==name)return false;
        }
        return true;
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
        posicions = new Posicio[4];
        for(int i=0;i<4;++i)posicions[i]=null;
        broadcastMessage(new Missatge("Soc el líder"));
        //ahead(200);///////////////////
        setTurnRight(360);
           execute();
        while(posicionsRebudes<4){
           //broadcastMessage(new Missatge("Soc el líder"));
           doNothing();
        //ahead(200);
           // 
        }//estatInicial()  fer voltes i disparar enemics sense moure's
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
        System.out.println("A "+posicions[posicio].getName()+" li dono el corner 0");
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
        System.out.println("A "+posicions[posicio].getName()+" li dono el corner 1");
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
        System.out.println("A "+posicions[posicio].getName()+" li dono el corner 2");
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
        System.out.println("A "+posicions[posicio].getName()+" li dono el corner 3");
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
    
    public void safeAhead(double l){
       // getx gety getheading
           x=l;
    }
    
    public void onHitRobot(HitRobotEvent e) {
        if(isTeammate(e.getName())){
            turnRight(90);
            safeAhead(30);
        }else{
            if (e.getBearing() > -10 && e.getBearing() < 10) {
                fire(3);
            }else{
                fire(1);
            }
        }
    }

}

