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
        goTo(20, 20);

        while(true){
            // Els robots es mous en voltant del taulell en sentit horari
            voltes_al_taulell();
        }
    }

    public void goTo(double X, double Y){
        double mvx=X-getX(),mvy=Y-getY();
        double distance = Math.sqrt(Math.pow(mvx,2)+Math.pow(mvy,2));
        double headingg = Math.toDegrees(Math.atan(mvx/mvy));
        if (Y <= getBattleFieldHeight()){
            headingg+=180;
        }
        turnRight(headingg-getHeading());
        ahead(distance);
        //Movem el robot perque estigui en paral·lel amb el taulell
        if(Y <= getBattleFieldHeight())turnLeft(getHeading()-270);
        else turnRight(180-getHeading());
        turnGunRight(45);
    }

    public void voltes_al_taulell(){
        // Els robots es mous en voltant del taulell en sentit horari
        // El -23 serveix per a que el robot intenti no xocar amb la paret
        Double maxHeight = getBattleFieldHeight() - 23;
        Double maxWidth = getBattleFieldWidth() - 23;

        // Si ens trobem a alguna cantonada, realitzem un gir de 90º
        if((getX() <= 21 && getY() <= 21 ) || (getX() <= 21 && getY() >= maxHeight) || (getX() >= maxWidth && getY() <= 21) || (getX() >= maxWidth && getY() >= maxHeight)){
            turnRight(90);
            // Fem un ahead, ja que di no el robot es quedaria en bucle infinit donant voltes ja que detectaria que esta sempre a una cantonada
            ahead(10);
        }
        else{
            // Mirem si ens trobem a la cantonada inferior esquerra o superior dreta, per aixi realitzar un moviment amb distancia igual a l'alçada del taulell
            if(getX() <= 21 || getX() >= maxWidth){
                Integer distancia = (int) ((getBattleFieldHeight() - 20) - getY());  
                setAhead(distancia);
                execute();
                turnGunRight(90);
                turnGunLeft(90);
                
            }

            // Mirem si ens trobem a la cantonada superior esquerra o inferior dreta, per aixi realitzar un moviment amb distancia igual a l'amplada del taulell
            else{
                Integer distancia = (int) ((getBattleFieldWidth() - 20) - getX());  
                setAhead(distancia);
                execute();
                turnGunRight(90);
                turnGunLeft(90);
            }         
            
        }

        
    }
}
    

