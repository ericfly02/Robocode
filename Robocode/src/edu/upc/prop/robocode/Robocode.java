/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package edu.upc.prop.robocode;
import robocode.*;

/**
 *
 * @author speed
 */
public class Robocode extends Robot{

    public void run(){
        turnLeft(getHeading());
        while(true){
            ahead(1000);
            turnRight(90);
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e){
        fire(1);
    }
    
    public void onHitByBullet(HitByBulletEvent e){
        turnLeft(180);
    }
    
}
