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

    // Create a new robot which goes to the corner of the battlefield
    // and then moves in a square pattern
    public void run() {
        // Set colors
        setBodyColor(new Color(255, 0, 0));
        setGunColor(new Color(0, 255, 0));
        setRadarColor(new Color(0, 0, 255));
        setBulletColor(new Color(255, 255, 0));
        setScanColor(new Color(255, 0, 255));

        // Loop forever
        while (true) {
            // Tell the game that when we take move,
            // we'll also want to turn right... a lot.
            setTurnRight(10000);
            // Limit our speed to 5
            setMaxVelocity(5);
            // Start moving (and turning)
            ahead(10000);
            // Repeat.
        }
    }

    // Create 5 robots
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            // Create a new robot
            Robocode r = new Robocode();
            // Set the robot colors

        }
    }


    
    public void onScannedRobot(ScannedRobotEvent e){
        fire(1);
    }
    
    public void onHitByBullet(HitByBulletEvent e){
        turnLeft(180);
    }
    
}
