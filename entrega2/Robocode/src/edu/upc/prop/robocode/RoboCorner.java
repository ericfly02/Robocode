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
import static robocode.Rules. *;
//import static robocode.Rules.getBulletSpeed;


/**
 *
 * @author speed
 */
public class RoboCorner extends TeamRobot  {

	boolean peek; // Don't turn if there's a robot there
	double moveAmount; // How much to move

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
    private Boolean lider=false;
    private Boolean millora2=false;  //si es true tots apuntaran a un mateix objectiu, si es false atacaran sense la millora 2 aplicada.
    private Posicio[] posicions;
    private Integer posicionsRebudes = 0;
    private Integer rotacio=1;


	/**
	 * run: Move around the walls
	 */
	public void run() {

		// Modifiquem el color del robot
		setBodyColor(Color.black);
		setGunColor(Color.black);
		setRadarColor(Color.black);
		setBulletColor(Color.black);
		setScanColor(Color.black);

		kamikaze=lider_suprem();
        lider=(getName().contains("(1)"));
        if(kamikaze){
            try {
                enviaPosicions();
            } catch (IOException ex) {
                Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
		
		else{
            // El radar segueix el moviment del cano     
            setAdjustRadarForRobotTurn(false);
            // El cano segueix el moviment del robot
            setAdjustGunForRobotTurn(false);
            // El radar segueix el moviment del cano
            setAdjustRadarForGunTurn(false);
            
            try {
                // En primer lloc el robot s'orienta cap a la seva posició inicial (el corner que està mes a prop)
                //goTo();
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


    public void goTo(double X, double Y,Boolean abaix){
        double mvx=X-getX(),mvy=Y-getY();
        double distance = Math.sqrt(Math.pow(mvx,2)+Math.pow(mvy,2));
        double headingg = Math.toDegrees(Math.atan(mvx/mvy));
        if (abaix)headingg+=180;
        if(corner==2)headingg=headingg+360;
        if(corner==3)headingg=headingg-360;
        if(corner%2==0){
            turnRight(headingg-getHeading());
        }else{
            turnLeft(-headingg+getHeading());
        }
        ahead(distance);
    }

    public Boolean lider_suprem(){
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
                        sendMessage(e.getSender(),new Missatge("Ves a la posicio",posicio(e.getSender()),(double)0));
                    }
                    break;
                case "Ataca":
                    double dx=M.getX()-getX();
                    double dy=M.getY()-getY();
                    double theta = Math.toDegrees(Math.atan2(dx, dy));
                    turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
                    fire(3);
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


	public void enviaPosicions() throws IOException{
        double distancia,distanciaMin=2000000;
        Integer posicio = 7;
        for(int i = 0;i<4;++i){
            if(posicions[i].getZ()==4){
                distancia=distancia(posicions[i].getX(),posicions[i].getY(),(double)0,(double)0);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves a la posicio",(double)0,(double)0));
        posicions[posicio].setZ(0);
        distanciaMin=2000000;
        for(int i = 0;i<4;++i){
            if(posicions[i].getZ()==4){
                distancia=distancia(posicions[i].getX(),posicions[i].getY(),(double)0,(double)800);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves a la posicio",(double)1,(double)0));
        posicions[posicio].setZ(1);
        distanciaMin=2000000;
        for(int i = 0;i<4;++i){
            if(posicions[i].getZ()==4){
                distancia=distancia(posicions[i].getX(),posicions[i].getY(),(double)1000,(double)800);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves a la posicio",(double)2,(double)0));
        posicions[posicio].setZ(2);
        distanciaMin=2000000;
        for(int i = 0;i<4;++i){
            if(posicions[i].getZ()==4){
                distancia=distancia(posicions[i].getX(),posicions[i].getY(),(double)1000,(double)0);
                if(distancia<distanciaMin){
                    distanciaMin=distancia;
                    posicio=i;
                }
            }
        }
        sendMessage(posicions[posicio].getName(),new Missatge("Ves a la posicio",(double)3,(double)0));
        posicions[posicio].setZ(3);
    }

    public void camperState() throws IOException{
        // Si el robot es troba a la posició 0 o 2, fa el moviment sentinella (gira a l'esquerra 180º, es mou 189 posicions, 
        // torna a girar a la dreta 180º i es mou 189 posicions) un cop realitzat el moviment sentinella, executa un gir de 180º per
        // disparar a tots els robots que es trobin al seu voltant
        setTurnRadarRight(360000);
        execute();
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

	public void onHitRobot(HitRobotEvent e) {
		// If he's in front of us, set back up a bit.
		if (e.getBearing() > -90 && e.getBearing() < 90) {
			back(100);
		} // else he's in back of us, so set ahead a bit.
		else {
			ahead(100);
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if(!isTeammate(e.getName())){
			double absBearing=e.getBearingRadians()+getHeadingRadians();//enemies absolute bearing
			double latVel=e.getVelocity() * Math.sin(e.getHeadingRadians() -absBearing);//enemies later velocity
			double gunTurnAmt;//amount to turn our gun
			setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//lock on the radar
			if(Math.random()>.9){
				setMaxVelocity((12*Math.random())+12);//randomly change speed
			}
			if (e.getDistance() > 150) {//if distance is greater than 150
				gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/22);//amount to turn our gun, lead just a little bit
				setTurnGunRightRadians(gunTurnAmt); //turn our gun
				setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing-getHeadingRadians()+latVel/getVelocity()));//drive towards the enemies predicted future location
				setAhead((e.getDistance() - 140)*moveDirection);//move forward
				setFire(3);//fire
			}
			else{//if we are close enough...
				gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/15);//amount to turn our gun, lead just a little bit
				setTurnGunRightRadians(gunTurnAmt);//turn our gun
				setTurnLeft(-90-e.getBearing()); //turn perpendicular to the enemy
				setAhead((e.getDistance() - 140)*moveDirection);//move forward
				setFire(3);//fire
			}
		}
	}
}