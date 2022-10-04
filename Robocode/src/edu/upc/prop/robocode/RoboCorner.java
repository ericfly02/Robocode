/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.prop.robocode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.*;


/**
 *
 * @author speed
 */
public class RoboCorner extends TeamRobot{
    
    // Creem les variables privades que necessitem
    private double x;
    private double y;
    private double width;
    private double height;
    private double heading;
    private Boolean[] ocupat;
    private Integer position;
    
    public void run(){
        try {
            goToCorner();
        } catch (IOException ex) {
            Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true){
            camperState();        
            setAdjustRadarForRobotTurn(false);
            setAdjustRadarForGunTurn(true);
            turnGunLeft(180);
            if (position==1 || position==3)
                turnGunLeft(90);
        }

    }
    
    public void goToCorner() throws IOException{
        x = getX();
        y = getY();
        width = getBattleFieldWidth();
        height = getBattleFieldHeight();
        heading = getHeading();
        ocupat = new Boolean[4];

        // Amb aquesta funció establim que la direccció del radar i la istola serà la mateixa que la direcció del robot
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForRobotTurn(true);


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
    
    public void moveCorner0()throws IOException{
        if(!ocupat[0]){
            broadcastMessage("C0_ocupat"); 
            ocupat[0]=true;  
            position=0;     
            turnRight(270-heading);
            ahead(x);
            turnLeft(90);
            ahead(y);
        }
        else{
            if(x<y && !ocupat[1]) moveCorner1();
            else{
                if (!ocupat[3]) moveCorner3();
                else moveCorner2();
            }
        }
    }

    public void moveCorner3()throws IOException{
        if(!ocupat[3]){
            broadcastMessage("C3_ocupat");
            ocupat[3]=true;  
            position=3;
            turnRight(heading+90);
            ahead(width-x);
            turnRight(90);
            ahead(y);
        }
        else{
            if(x<y && !ocupat[0]) moveCorner0();
            else{
                if (!ocupat[2]) moveCorner2();
                else moveCorner1();
            }
        }
    }

    public void moveCorner1()throws IOException{
        if(!ocupat[1]){
            broadcastMessage("C1_ocupat");
            ocupat[1]=true;  
            position=1;
            turnRight(270-heading);
            ahead(x);
            turnRight(90);
            ahead(height-y);
        }
        else{
            if(x<y && !ocupat[0]) moveCorner0();
            else{
                if (!ocupat[3]) moveCorner3();
                else moveCorner2();
            }
        }
    }

    public void moveCorner2()throws IOException{
        if(!ocupat[2]){
            broadcastMessage("C2_ocupat");
            ocupat[2]=true;
            position=2;
            turnRight(heading+90);
            ahead(width-x);
            turnLeft(90);
            ahead(height-y);
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
       fire(1);
    }
}
