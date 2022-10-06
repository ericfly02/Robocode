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
    private Integer corner=4;       // Posició del robot en el camp de batalla | S'inicialitza a quatre però el kamikaze li comunicarà a quin corner ha d'anar {0.1.2.3}
    private double gunHeading;     // Direcció del canó del robot
    private double radarHeading;   // Direcció del radar del robot
    private String nomLider=null;
         
    public void run(){   
        // Mentres els robots no hagin rebut la posició del corner a la que han d'anar, realitzaràn moviments 360º disparant 
        // a tots els robots que trobin
//System.out.println("estic aqui");
        //setAdjustRadarForGunTurn(false);
        //importante comentar
            //turnGunRight(360);
            //execute();
       //System.out.println("estic aqui"); 
       
//turnGunRight(360);
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
        while(nomLider==null)execute();//que doni voltes i dispari enemics
        //System.out.println("era broma si que tinc lider");
        //System.out.println(nomLider);
        //turnGunRight(360);
        sendMessage(nomLider,new Missatge("A quin corner vaig capità?"/*,x,y*/));
        //System.out.println("Broadcast enviat");
        //turnGunRight(360);
        while(corner==4){
        sendMessage(nomLider,new Missatge("A quin corner vaig capità?"/*,x,y*/));
        //System.out.println("Broadcast enviat");
        setTurnGunRight(360);
        execute();
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
            turnLeft(180);
            ahead(189);
            turnRight(180);
            ahead(189);
            turnLeft(180);
            turnRight(180);       
        }

        // Si el robot es troba a la posició 1 o 3, fa el moviment sentinella (gira a la dreta 180º, es mou 189 posicions, 
        // torna a girar a l'esquerra 180º i es mou 189 posicions) un cop realitzat el moviment sentinella, executa un gir de 180º per
        // disparar a tots els robots que es trobin al seu voltant
        else if(corner == 1 || corner == 3){
            turnRight(180);
            ahead(189);
            turnLeft(180);
            ahead(189);
            turnRight(180);
            turnLeft(180);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e){
        if (isTeammate(e.getName())){
           return;
 
        }
       fire(1);
    }
    
    public void onMessageReceived(MessageEvent e){
        System.out.println("missatge rebut");
            Missatge M = (Missatge) e.getMessage();
            String m=M.getText();
            switch(m){
                case "Ves al corner":
                    corner = (int) M.getX();
                    //ahead(200);
                case "Soc el líder":
                    nomLider=e.getSender();
                    //ahead(200);
                default: 
                    //System.out.println(m);
                    //ahead(200);
            }
    }
}
