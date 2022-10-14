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
/**
 *
 * @author speed
 */
public class padel extends TeamRobot {

    // Creem les variables privades que necessitem
    private Double xi;               // Coordenada inicial x del robot
    private Double yi;               // Coordenada inicial y del robot
    private String nomLider = null;
    private Boolean lider = false;
    private Map<Double, Posicio> posicions;
    private Integer posicionsRebudes = 0;
    private Boolean esperaCompanys = true;
    private String status; //pot ser: IMPORTANT: cal seguir el codi i tocar l'estatus que toca a cada lloc 
                                        //kamikaze(el robot s'ha xocat amb un enemic i es transforma en kamikaze fins que el mati o es mori)
                                        //noPosi(espera a rebre a quina posicio ha de començar)
                                        //arribant(en procés d'arribar al punt d'inici)
                                        //esperant(havent arribat espera el start)
                                        //atacant

    public padel() {
        this.posicions = null;
    }


    public void run(){
        // Modifiquem el color del robot
        setBodyColor(Color.black);
        setGunColor(Color.black);
        setRadarColor(Color.black);
        setBulletColor(Color.black);
        setScanColor(Color.black);

        lider=(getName().contains("(1)"));

        if(lider){

            try {
                // Esperem a que tots els robots enviin les seves posicions
                rebrePosicions();
            } catch (IOException ex) {
                Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                // Enviem les posicions a tots els robots
                enviaPosicions();
            } catch (IOException ex) {
                Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
            }
            //que vagi al lloc que li toca, inicial
            goAndTurn(posicions.get(xi).getX(),posicions.get(xi).getY());
            estaAlaPosi(getName());
            try {
                // Ens assegurem de que tots els membres de l'equip agin contestat
                preVoltes();
            } catch (IOException ex) {
                Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        else{
            
            preparaPosicions();

            try {
                // Enviar posicio inicial al lider, i esperar posició a la que s'ha d'anar, i anar-hi
                goToPosition();
            } catch (IOException ex) {
                Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Esperant companys");
            status="esperant";
            try {
                esperaCompanys();
            } catch (IOException ex) {
                Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        while(true){
            // Els robots es mous en voltant del taulell en sentit horari
            voltes_al_taulell();
        }
    }

    public void preVoltes() throws IOException{
        Boolean esperar = true;
        while(esperar){
            esperar = true;
            for(Map.Entry<Double,Posicio> entry : posicions.entrySet()) {
                    esperar=esperar && entry.getValue().isReady();
            }
            esperar = !esperar;
            if(esperar == true){
                broadcastMessage(new Missatge("Has arribat?"));
                doNothing();
            }
        }
        broadcastMessage(new Missatge("Start"));
        System.out.println("Start");
        status="atacant";
        doNothing();
    }

    public void esperaCompanys() throws IOException{
        System.out.println("Esperant companys fins de la funció");
        while(status.equals("esperant")){//aquí hay que poner que status==esperant pero primero hay que revisar todo y poner los status donde toque
                turnRadarRight(36000);//funcion que el radar da vueltas para disparar robots mientras los compañeros llegan a su posi de inicio
                execute();
                sendMessage(nomLider,new Missatge("Ja estic a la meva posi"));
                System.out.println("Ja estic a la meva posi");
                turnRight(1);
                doNothing();
        }
        System.out.println("Ja estic en marxa");
        doNothing();
    }

    public void preparaPosicions(){
        xi=getX();
        yi=getY();
        posicions =  new TreeMap<Double,Posicio>();
        posicions.put(xi, new Posicio(getName(),xi,yi));
        ++posicionsRebudes;
    }

    public void rebrePosicions() throws IOException{
        posicions = new TreeMap<Double,Posicio>();
        xi = getX();
        yi = getY();
        posicions.put(xi,new Posicio(getName(), xi, yi));
        ++posicionsRebudes;
        while(posicionsRebudes<5){
            broadcastMessage(new Missatge("Necessito dades"));
            System.out.println("Necessito dades, posicionsrebudes = "+posicionsRebudes);
            doNothing();
        }
    }

    public void enviaPosicions() throws IOException{
        Double y_mitja=0.0;
        for(Map.Entry<Double,Posicio> entry : posicions.entrySet()) {
            y_mitja+=entry.getKey();
        }
        y_mitja/=5;
        if(y_mitja > (getBattleFieldHeight()/2)){
            y_mitja = getBattleFieldHeight()-20;
        }else{
            y_mitja = 20.0;
        }
        Double send_x = 20.0;
        for(Map.Entry<Double,Posicio> entry : posicions.entrySet()) {
            if(entry.getValue().getName() != getName())
                sendMessage(entry.getValue().getName(),new Missatge("Ves a",send_x,y_mitja));
             System.out.println("Ves a "+send_x+"  "+y_mitja);
            entry.getValue().setX(send_x);
            entry.getValue().setY(y_mitja);
            send_x+=(getBattleFieldWidth()-40)/4;
        }
    }

    public void goTo(Double X, Double Y){
        Double mvx=X-getX(),mvy=Y-getY();
        Double distance = Math.sqrt(Math.pow(mvx,2)+Math.pow(mvy,2));
        Double headingg = Math.toDegrees(Math.atan(mvx/mvy));
        if (Y==20){
            headingg+=180;
        }
        turnRight(headingg-getHeading());
        ahead(distance);
        //Movem el robot perque estigui en paral·lel amb el taulell
    }
    
    public void goAndTurn(Double X, Double Y){
        goTo(X, Y);
        System.out.println("Antes del while");
        while(Math.abs(getX()-X) > 1 || Math.abs(getY()-Y) > 1){
            goTo(X, Y);
            System.out.println("Despues del while, x = "+X+" Y = "+Y+" getX = "+getX()+" getY = "+getY());
        }
        doNothing();
        System.out.println("Fuera del while");
        if(Y <= getBattleFieldHeight())turnLeft(getHeading()-270);
        else turnRight(180-getHeading());
        turnGunRight(45);
    }
    public void onMessageReceived(MessageEvent e){
        
        try {
            Missatge M = (Missatge) e.getMessage();
            String m=M.getText();
            switch(m){
                case "Ves a":
                    if(posicions == null)
                        break;
                    posicions.get(xi).setX(M.getX());
                    posicions.get(xi).setY(M.getY());
                    System.out.println("M'han comunicat que vagi al punt ("+M.getX()+" , "+M.getY()+")");
                    break;
                case "Necessito dades":
                                System.out.println("Toma dades, posicionsRebudes  = "+posicionsRebudes);
                    nomLider = e.getSender();
                    if (posicionsRebudes==0)break;
                    sendMessage(e.getSender(),new Missatge("Toma dades",xi,yi));
                    doNothing();
                    break;
                case "Toma dades":
                     System.out.println("Gracies per les dades, "+e.getSender());
                    nouMembre(e.getSender(),M.getX(),M.getY());
                    doNothing();
                    break;
                case "Ja estic a la meva posi":
                    if(status == "atacant"){
                        sendMessage(e.getSender(),new Missatge("Start"));
                        doNothing();
                    }
                    estaAlaPosi(e.getSender());
                case "Stop":
                    stop();
                    break; 
                case "Has arribat?":
                    if(status == "esperant")
                        sendMessage(nomLider,new Missatge("Ja estic a la meva posi"));
                case "On vaig?":
                    if(status == "esperant"){
                        for(Map.Entry<Double,Posicio> entry : posicions.entrySet()) {
                            if(entry.getValue().getName() == e.getSender()){
                                sendMessage(e.getSender(),new Missatge("Ves a",entry.getValue().getX(),entry.getValue().getY()));
                                break;
                            }
                        }
                    }
                break;
                case "Start":
                    status="atacant";
                    System.out.println("He rebut el start");
                default:
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    

    public void estaAlaPosi(String name){
        for(Map.Entry<Double,Posicio> entry : posicions.entrySet()) {
            if(entry.getValue().getName() == name){
                entry.getValue().jaEstaPreparat();
                break;
            }
        }
    }

     public void nouMembre(String name,Double xx,Double yy){
        if(posicions.containsKey(xx)){
            Boolean trobat=false;
            for(int i = 0;i<5 && !trobat;++i){
                if(posicions.get(xx+i*0.000001) != null && posicions.get(xx+i*0.000001).getName() == name)
                    trobat=true;
            }
            if (!trobat){
                for(int i = 0;i<5;++i){
                   if(posicions.get(xx+i*0.000001) == null){}
                        posicions.put(xx+i*0.000001, new Posicio(name, xx, yy));
                        ++posicionsRebudes;
                        break;
                }
            }
        }else{
          posicions.put(xx, new Posicio(name, xx, yy));
          ++posicionsRebudes;
        }
    }
    
    public void voltes_al_taulell(){
        System.out.println("Estamos en el LOOOOOOOOP");
        setAdjustRadarForGunTurn(false);
        // Els robots es mous en voltant del taulell en sentit horari
        // El -23 serveix per a que el robot intenti no xocar amb la paret
        Double maxHeight = getBattleFieldHeight() - 23;
        Double maxWidth = getBattleFieldWidth() - 23;
        doNothing();
        // Si ens trobem a alguna cantonada, realitzem un gir de 90º
        if((getX() <= 21 && getY() <= 21 ) || (getX() <= 21 && getY() >= maxHeight) || (getX() >= maxWidth && getY() <= 21) || (getX() >= maxWidth && getY() >= maxHeight)){
            turnRight(90);
            // Fem un ahead, ja que di no el robot es quedaria en bucle infinit donant voltes ja que detectaria que esta sempre a una cantonada
            ahead(10);
            doNothing();
            System.out.println("Estamos en IF 1");
        }
        else{
            System.out.println("Estamos en ELSE 1");
            // Mirem si ens trobem a la cantonada inferior esquerra o superior dreta, per aixi realitzar un moviment amb distancia igual a l'alçada del taulell
            if(getX() <= 21 || getX() >= maxWidth){
                System.out.println("Estamos en IF 2");
                Integer distancia = (int) ((getBattleFieldHeight() - 20) - getY());  
                setAhead(distancia);
                execute();
                turnGunRight(90);
                turnGunLeft(90); 
                doNothing();
            }

            // Mirem si ens trobem a la cantonada superior esquerra o inferior dreta, per aixi realitzar un moviment amb distancia igual a l'amplada del taulell
            else{
                System.out.println("Estamos en ELSE 2");
                //Integer distancia = (int) ((getBattleFieldWidth() - 20) - getX());  
                //setAhead(distancia);
                setAhead(100);
                execute();
                turnGunRight(90);
                turnGunLeft(90);
                doNothing();
            }         
            
        } 
    }
    
    // Si el robot es xoca contra algun enemic, tots els robots es converteixen en kamikaze
    /*public void onHitRobot(HitRobotEvent e){
        try {
            broadcastMessage(new Missatge("Stop"));
        } catch (IOException ex) {
            Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(e.isMyFault()){

            // Mirem la vida del robot que ens ha xocat i si es menor que la nostra, ataquem
            if(getEnergy() > e.getEnergy()){
                turnGunRight(e.getBearing());
                fire(3);
            }
            else{
                turnRight(90);
                ahead(50);
                turnGunLeft(e.getBearing());
                fire(3);
                turnLeft(180);
                ahead(50);
                turnRight(90);
                fire(1);
            }
        }
    }*/
    
    public void onHitRobot(HitRobotEvent e){
        setTurnRight(90);
        setAhead(100);
        setTurnRadarRight(3600);
        execute();
        
    }
    
    public void onScannedRobot(ScannedRobotEvent e){
        Double x = getX();
        Double y = getY();
        // Si encara no ha comnçat el procés , i el robot escaneja a un del seu euqip
        if (isTeammate(e.getName())){
            Double width = getBattleFieldWidth(), height = getBattleFieldHeight();
            // Obtenim posició del robot aliat
           Double posicio_x = x + e.getDistance() * Math.sin(Math.toRadians(getHeading() + e.getBearing()));
           Double posicio_y = y + e.getDistance() * Math.cos(Math.toRadians(getHeading() + e.getBearing()));
           if(posicio_x-x <= 40 && posicio_x-x >= -40 && posicio_y-y <= 40 && posicio_y-y >= -40){
                // Si es troba aprop del corner 2
                if(width-x >= width-80 || height-y >= height-80){
                    turnRight(90);
                    ahead(80);
                }
                // Si es troba aprop del corner 0
                if(x >= 80 || y >= 80){
                    turnRight(90);
                    ahead(80);
                }
                // Si es troba aprop del corner 1
                if(x >= 80 || height-y >= height-80){
                    turnLeft(90);
                    ahead(80);
                }
                // Si es troba aprop del corner 3
                if(width-x >= width-80 || y >= 80){
                    turnRight(90);
                    ahead(80);
                }
           }
           return;
 
        }
        else{

            Double enemyBearing = getHeading() + e.getBearing();
            Double dx = e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
            Double dy = e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
            Double theta = Math.toDegrees(Math.atan2(dx, dy));
            turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
            fire(3);          
        }
    }

    public void goToPosition() throws IOException {
        while(posicions.get(xi).getX() == 0.0){
            sendMessage(nomLider,new Missatge("On vaig?"));
            doNothing();
        }
        goAndTurn(posicions.get(xi).getX(),posicions.get(xi).getY());
    }

}