/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.prop.robocode;
import java.awt.Color;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;


/**
 *
 * @author speed
 */
public class RoboCorner extends AdvancedRobot{
    public void run(){
        // Modifiquem el color del robot
        setBodyColor(Color.black);
        setGunColor(Color.black);
        setRadarColor(Color.black);
        setBulletColor(Color.black);
        setScanColor(Color.black);

        // Enviar posicio inicial al lider, i esperar posició a la que s'ha d'anar, i anar-hi
        goTo(0, 0, true);

        while(true){
            // Els robots es mous en voltant del taulell en sentit horari
            voltes_al_taulell();
        }
    }

    public void goTo(double X, double Y,Boolean abaix){
        double mvx=X-getX(),mvy=Y-getY();
        double distance = Math.sqrt(Math.pow(mvx,2)+Math.pow(mvy,2));
        double headingg = Math.toDegrees(Math.atan(mvx/mvy));
        if (abaix){
            headingg+=180;
        }
        turnRight(headingg-getHeading());
        ahead(distance);
    }

    public void voltes_al_taulell(){
        // Els robots es mous en voltant del taulell en sentit horari
        Double maxHeight = getBattleFieldHeight() - 20;
        Double maxWidth = getBattleFieldWidth() - 20;

        if((getX() == 20 && getY() == 20)){
            turnRight(90);
            setAhead(maxHeight - 20);
            execute();
            turnGunLeft(45);
            turnGunRight(45);
        }
        else if((getX() == 20 && getY() == maxHeight)){
            turnRight(90);
            setAhead(maxWidth - 20);
            execute();
            turnGunLeft(45);
            turnGunRight(45);
        }
        else if((getX() == maxWidth && getY() == maxHeight)){
            turnRight(90);
            setAhead(maxHeight - 20);
            execute();
            turnGunLeft(45);
            turnGunRight(45);
        }
        else if((getX() == maxWidth && getY() == 20)){
            turnRight(90);
            setAhead(maxWidth - 20);
            execute();
            turnGunLeft(45);
            turnGunRight(45);
        }
        else{
            // Si no es cap dels casos anteriors, vol dir que el robot no h començat a moure's
            Integer distancia = (int) (maxWidth - getX());
            setAhead(distancia);
            execute();
            turnGunLeft(45);
            turnGunRight(45);
        }
        
    }
}
    

