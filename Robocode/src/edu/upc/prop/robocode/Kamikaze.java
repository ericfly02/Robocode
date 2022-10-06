/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.prop.robocode;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.*;


/**
 *
 * @author speed
 */
public class Kamikaze extends TeamRobot{
    
    // Creem les variables privades que necessitem
    private Posicio[] posicions;
    private Integer posicionsRebudes = 0;
    private double x;               // Coordenada x del robot
    private double y;               // Coordenada y del robot
    private double width;           // Amplada del camp de batalla
    private double height;          // Alçada del camp de batalla
    private double heading;         // Direcció del robot
    private Boolean[] ocupat;       // Array de booleans que ens dirà si una posició està ocupada o no
    private Integer position;       // Posició del robot en el camp de batalla
    private double gunHeading;     // Direcció del canó del robot
    private double radarHeading;   // Direcció del radar del robot
    private trigonometry t=new trigonometry();
    private RobotStatus robotStatus;
         
    public void run(){
        try {
            ini();
        } catch (IOException ex) {
            Logger.getLogger(Kamikaze.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while(true){
            target_kamikaze();
        }
    }
    
    public void ini() throws IOException{
        broadcastMessage(new Missatge("Soc el líder"));
        //ahead(200);///////////////////
        posicions = new Posicio[4];
        while(posicionsRebudes<4){
           //broadcastMessage(new Missatge("Soc el líder"));
           setTurnRight(360);
           execute();
        //ahead(200);
           // 
        }//estatInicial()  fer voltes i disparar enemics sense moure's
        enviaPosicions();
    }
    
    public void enviaPosicions() throws IOException{
        double distancia,distanciaMin=2000000;
        Integer posicio = 7;
        for(int i = 0;i<4;++i){
            if(posicions[i]!=null){
                distancia=t.distancia(posicions[i].getX(),posicions[i].getY(),(double)0,(double)0);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves al corner",(double)0,(double)0));
        posicions[posicio]=null;
        distanciaMin=2000000;
        for(int i = 0;i<4;++i){
            if(posicions[i]!=null){
                distancia=t.distancia(posicions[i].getX(),posicions[i].getY(),(double)0,(double)800);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves al corner",(double)1,(double)0));
        posicions[posicio]=null;
        distanciaMin=2000000;
        for(int i = 0;i<4;++i){
            if(posicions[i]!=null){
                distancia=t.distancia(posicions[i].getX(),posicions[i].getY(),(double)1000,(double)800);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves al corner",(double)2,(double)0));
        posicions[posicio]=null;
        distanciaMin=2000000;
        for(int i = 0;i<4;++i){
            if(posicions[i]!=null){
                distancia=t.distancia(posicions[i].getX(),posicions[i].getY(),(double)1000,(double)0);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves al corner",(double)3,(double)0));
        posicions[posicio]=null;
    }
    
    public void goToCorner() throws IOException{
        
        x = getX();                         // Obtenim coordenada x del robot
        y = getY();                         // Obtenim coordenada y del robot
        width = getBattleFieldWidth();      // Obtenim les dimensions del camp de batalla (amplada)
        height = getBattleFieldHeight();    // Obtenim les dimensions del camp de batalla (alçada)
        heading = getHeading();             // Guardem la posició inicial del robot
        gunHeading = getGunHeading();       // posició del canó
        radarHeading = getRadarHeading();   // posició del radar
        ocupat = new Boolean[4];
        Arrays.fill(ocupat, Boolean.FALSE);

        // Amb aquesta funció establim que la direccció del radar i la istola serà la mateixa que la direcció del robot
        

        // Es mou abaix a l'esquerra
        if (x < width/2 && y < height/2){
            moveCorner0();
        }
        
        // Es mou adalt a l'esquerra
        else if (x < width/2 && y > height/2){
            moveCorner1();
        }

        // Es mou adalt a la dreta
        else if (x > width/2 && y > height/2){
            moveCorner2();
        }
        
        // Es mou abaix a la dreta
        else if (x > width/2 && y < height/2){
            moveCorner3();
        }
    }
    
    public void moveCorner0() throws IOException{
        if(!ocupat[0]){
            broadcastMessage(new Missatge("C0_ocupat")); 
            //ocupat[0]=true;  
            ocupat[0]=true;  
            position=0;     
            turnRight(270-heading);
            //turnGunRight(270-gunHeading);
            //turnRadarRight(270-radarHeading);
            setAdjustGunForRobotTurn(false);
            setAdjustRadarForRobotTurn(false);
            ahead(x-20);
            turnLeft(90);
            ahead(y-20);
            //ocupat=
        }
        else{
            if(x<y && !ocupat[1]) moveCorner1();
            else{
                if (!ocupat[3]) moveCorner3();
                else moveCorner2();
            }
        }
    }

    public void moveCorner3() throws IOException{
        if(!ocupat[3]){
            broadcastMessage(new Missatge("C3_ocupat"));
            ocupat[3]=true;  
            position=3;
            turnRight(90-heading);
            //turnGunRight(90-gunHeading);
            //turnRadarRight(90-radarHeading);
            setAdjustGunForRobotTurn(false);
            setAdjustRadarForRobotTurn(false);
            ahead(width-x-20);
            ahead(width-x);
            turnRight(90);
            ahead(y-20);
        }
        else{
            if(x<y && !ocupat[0]) moveCorner0();
            else{
                if (!ocupat[2]) moveCorner2();
                else moveCorner1();
            }
        }
    }

    public void moveCorner1() throws IOException{
        if(!ocupat[1]){
            broadcastMessage(new Missatge("C1_ocupat"));
            ocupat[1]=true;  
            position=1;
            turnRight(270-heading);
            //turnGunRight(270-gunHeading);
            //turnRadarRight(270-radarHeading);
            setAdjustGunForRobotTurn(false);
            setAdjustRadarForRobotTurn(false);
            ahead(x-20);
            turnRight(90);
            ahead(height-y-20);
        }
        else{
            if(x<y && !ocupat[0]) moveCorner0();
            else{
                if (!ocupat[3]) moveCorner3();
                else moveCorner2();
            }
        }
    }

    public void moveCorner2() throws IOException{
        if(!ocupat[2]){
            broadcastMessage(new Missatge("C2_ocupat"));
            ocupat[2]=true;
            position=2;
            turnRight(90-heading);
            //turnGunRight(90-gunHeading);
            //turnRadarRight(90-radarHeading);
            setAdjustGunForRobotTurn(false);
            setAdjustRadarForRobotTurn(false);
            ahead(width-x-20);
            ahead(width-x);
            turnLeft(90);
            ahead(height-y-20);
        }
        else{
            if(x<y && !ocupat[1]) moveCorner1();
            else{
                if (!ocupat[3]) moveCorner3();
                else moveCorner0();
            }
        }
    }   

    public void target_kamikaze(){
        // En aquesta funció el radar gira 360º i quan troba un enemic, el segueix mitjançant la funció onScannedRobot        
        // El radar segueix el moviment del cano
        setAdjustRadarForGunTurn(false);
        turnGunRight(360);
execute();
}

   
    public void onStatus(StatusEvent e) {
        this.robotStatus = e.getStatus();
    }    

    public void onScannedRobot(ScannedRobotEvent e) {

        // Si el robot escanejat és del nostre equip, no fem res
        if (isTeammate(e.getName())){
            return;
         }
        
        // Si el robot escanejat és un enemic, el seguim
        else{
            // Si el robot escanejat és un enemic, el seguim
            if (e.getDistance() < 100){
                // Si l'enemic està a una distància inferior a 100, el seguim
                // getBearing() ens retorna la direcció del robot escanejat respecte el nostre robot (en graus)
                setTurnRight(e.getBearing());
                setAhead(e.getDistance() - 100);
            }
            else{
                // Si l'enemic està a una distància superior a 100, el seguim
                setTurnRight(e.getBearing());
                setAhead(e.getDistance() - 100);
            }
        }
    }
    
    public void onMessageReceived(MessageEvent e){
                if(robotllegit(e.getSender()))return;
                System.out.println("missatge rebut");
turnRight(360);
                Missatge M = (Missatge) e.getMessage();
                String m=M.getText();
                switch(m){
                    case "A quin corner vaig capità?":
                        posicions[posicionsRebudes]=new Posicio(e.getSender(), M.getX(), M.getY());//obtener nombre y posicion y agregarlo al array posicions
                        ++posicionsRebudes;// incrementar posicionsRebudes
                        System.out.println("posicions rebudes incrementat, he afegit a "+e.getSender());
    }
    }
    
    public Boolean robotllegit(String n){
        for(int i=0;i<4;++i){
        if(posicions[i]!=null && posicions[i].getName()==n)return true;
        }
        return false;
    }
}