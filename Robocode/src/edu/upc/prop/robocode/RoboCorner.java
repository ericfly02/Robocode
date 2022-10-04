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
    private double heading;         // Direcció del robot
    private Boolean[] ocupat;       // Array de booleans que ens dirà si una posició està ocupada o no
    private Integer position;       // Posició del robot en el camp de batalla
    private double gunHeading;     // Direcció del canó del robot
    private double radarHeading;   // Direcció del radar del robot
    
         
    public void run(){        
        setAdjustRadarForRobotTurn(false);
        // 
        setAdjustGunForRobotTurn(false);
        setAdjustRadarForGunTurn(false);
        // En primer lloc el robot s'orienta cap a la seva posició inicial (el corner que està mes a prop)
        try {
            goToCorner();
        } catch (IOException ex) {
            Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Ara no volem que el radar giri juntament amb el robot, per tant el desactivem
        setAdjustRadarForRobotTurn(true);
        // 
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(false);
        setAdjustRadarForRobotTurn(false);
        // Ara no volem que el canó giri juntament amb el robot, per tant el desactivem
        setAdjustGunForRobotTurn(false);
        // Ara volem que el canó giri juntament amb el radar, per tant el activem
        setAdjustRadarForGunTurn(true);
        
        turnGunLeft(180);
        if (position==1 || position==3)
            turnGunLeft(90);
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
        gunHeading = getGunHeading();       // posició del canó
        radarHeading = getRadarHeading();   // posició del radar
        ocupat = new Boolean[4];
        Arrays.fill(ocupat, Boolean.FALSE);

        // Amb aquesta funció establim que la direccció del radar i la istola serà la mateixa que la direcció del robot
        /*setAdjustGunForRobotTurn(false);
=======
        setAdjustGunForRobotTurn(false);
>>>>>>> aa4e67f7391e6f8caca4e9139c99df1d8d9e01e5
        setAdjustRadarForRobotTurn(false);
        turnGunLeft(gunHeading-heading);
        turnRadarLeft(radarHeading-heading);//left gunheading-heading
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForRobotTurn(true);
*/

        // Es mou abaix a l'esquerra
        if (x < width/2 && y < height/2){
            moveCorner0();
        }
        
        // Es mou abaix a la dreta
        else if (x > width/2 && y < height/2){
            moveCorner3();
        }

        // Es mou adalt a l'esquerra
        else if (x < width/2 && y > height/2){
            moveCorner1();
        }

        // Es mou adalt a la dreta
        else if (x > width/2 && y > height/2){
            moveCorner2();
        }
    }
    
    public void moveCorner0() throws IOException{
        if(!ocupat[0]){
            broadcastMessage("C0_ocupat"); 
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
            broadcastMessage("C3_ocupat");
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
            broadcastMessage("C1_ocupat");
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
            broadcastMessage("C2_ocupat");
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

    public void camperState(){
        turnRight(180);
        ahead(189);
        turnRight(180);
        ahead(189);
        turnGunRight(90);
        turnGunLeft(90);
    }

    public void onScannedRobot(ScannedRobotEvent e){
        if (isTeammate(e.getName())){
           return;
 
        }
       fire(3);
    }
    
    public void onMessageReceived(MessageEvent e){
        String m=(String) e.getMessage();
        switch(m){
            case "C0_ocupat":
                ocupat[0]=true;
            case "C1_ocupat":
                ocupat[1]=true;
            case "C2_ocupat":
                ocupat[2]=true;
            case "C3_ocupat":
                ocupat[3]=true;
            default: 
        }
    }
}
