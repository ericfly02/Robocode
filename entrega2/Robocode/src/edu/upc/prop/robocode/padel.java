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
    private String enemyName;
    private String status; //pot ser: IMPORTANT: cal seguir el codi i tocar l'estatus que toca a cada lloc 
                                        //kamikaze(el robot s'ha xocat amb un enemic i es transforma en kamikaze fins que el mati o es mori)
                                        //noPosi(espera a rebre a quina posicio ha de començar)
                                        //arribant(en procés d'arribar al punt d'inici)
                                        //esperant(havent arribat espera el start)
                                        //atacant (viatja per les parets)
    private Integer nextCorner;

    public padel() {
        this.posicions = null;
    }


    public void run(){
        status="noPosi";
        System.out.println("Status = "+status);
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
            goForReal(posicions.get(xi).getX(),posicions.get(xi).getY());
            setTurnRadarRight(36000000);
            execute();
            status="esperant";
        System.out.println("Status = "+status);
            if(getY()>getBattleFieldHeight()/2)
                nextCorner=2;
            else 
                nextCorner=0;
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
        System.out.println("Status = "+status);
            try {
                esperaCompanys();
            } catch (IOException ex) {
                Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(getY()>getBattleFieldHeight()/2)
                nextCorner=2;
            else 
                nextCorner=0;
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
                setTurnRadarRight(36000000);
                execute();
                centinella();
                doNothing();
            }
        }
        broadcastMessage(new Missatge("Start"));
        System.out.println("Start");
        status="atacant";
        System.out.println("Status = "+status);
        doNothing();
    }

    public void esperaCompanys() throws IOException{
        System.out.println("Esperant companys fins de la funció");
        while(status.equals("esperant")){//aquí hay que poner que status==esperant pero primero hay que revisar todo y poner los status donde toque
                setTurnRadarRight(36000000);//funcion que el radar da vueltas para disparar robots mientras los compañeros llegan a su posi de inicio
                execute();
                //sendMessage(nomLider,new Missatge("Ja estic a la meva posi"));
                System.out.println("Ja estic a la meva posi");
                doNothing();
                centinella();
        }
        System.out.println("Ja estic en marxa");
        //doNothing();
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
        status="arribant";
        System.out.println("Status = "+status);
    }

    public void goTo(Double X, Double Y){
        setTurnRadarRight(3600000);
        execute();
        Double mvx=X-getX(),mvy=Y-getY();
        Double distance = Math.sqrt(Math.pow(mvx,2)+Math.pow(mvy,2));
        Double headingg = Math.toDegrees(Math.atan(mvx/mvy));
        if (Y<getY()){
            headingg-=180.0;
        }
        headingg-=getHeading();
        while(headingg<0.0)headingg+=360.0;
        while(headingg>360.0)headingg-=360.0;
        if(headingg<180.0)
            turnRight(headingg);
        else
            turnLeft(360-headingg);
        ahead(distance);
        //Movem el robot perque estigui en paral·lel amb el taulell
    }
    
    public void goForReal(Double X, Double Y){
        goTo(X, Y);
        System.out.println("Antes del while");
        while(Math.abs(getX()-X) > 80 || Math.abs(getY()-Y) > 80){
            goTo(X, Y);
            System.out.println("Despues del while, x = "+X+" Y = "+Y+" getX = "+getX()+" getY = "+getY());
        }
        //Movem el robot perque estigui en paral·lel amb el taulell
        /*if(Y <= getBattleFieldHeight())turnLeft(getHeading()-270);
        else turnRight(180-getHeading());*/
        //turnGunRight(45);
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
                    status="arribant";
        System.out.println("Status = "+status);
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
        System.out.println("Status = "+status);
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
        // Els robots es mous en voltant del taulell en sentit horari
        // El -23 serveix per a que el robot intenti no xocar amb la paret
        setTurnRadarRight(36000000);
        execute();
        
        Double cx = 20.0, cy = 20.0;
        if(nextCorner == 1){
            cy = getBattleFieldHeight()-20.0;
        }else if(nextCorner == 2){
            cx = getBattleFieldWidth()-20.0;
            cy = getBattleFieldHeight()-20.0;
        }else if (nextCorner == 3)
            cx = getBattleFieldWidth()-20.0;
            System.out.println("Entro a donar  voltes "+cx+"  "+cy);
        turnRight(2);
        goForReal(cx,cy);
        nextCorner = (nextCorner+1)%4;  
    } 
    
    public void onHitRobot(HitRobotEvent e){
        Double x = getX();
        Double y = getY();
        if(isTeammate(e.getName())){
            if(e.getBearing()<90 && e.getBearing()>-90){
                back(40);
                doNothing();
            }
            return;
        }
        /*if(status=="arribant" && e.getBearing()<75 && e.getBearing()>-75){
            back(20);
            Double rotacio=0.0;
            if(getX()>getBattleFieldWidth() && (getHeading()>270) || (getHeading()<90)){
                rotacio = -1.0;
            }
            if(getX()>getBattleFieldWidth() && !(getHeading()>270) || (getHeading()<90)){
                rotacio = 1.0;
            }
            if(getX()<=getBattleFieldWidth() && (getHeading()>270) || (getHeading()<90)){
                rotacio = 1.0;
            }
            if(getX()<=getBattleFieldWidth() && !(getHeading()>270) || (getHeading()<90)){
                rotacio = -1.0;
            }
            turnRight(90*rotacio);
            setTurnLeft(rotacio*90);
            setAhead(100);
            return;
        }*/
        //si es teammate back de 20
        

        ///si es enemigo le hace un kamikaze
        else{
            if(status == "arribant"){
                status = "atacant";
                try {
                    sendMessage(nomLider,new Missatge("Ja estic a la meva posi"));
                } catch (IOException ex) {
                    Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
                }
            System.out.println("Status = "+status);
                enemyName = e.getName();
            }
                
            Double rotacio;
                if (e.getBearing() >= 0) {
                rotacio = 1.0;
            } else {
                rotacio = -1.0;
            }
            turnRight(e.getBearing());

            // Determine a shot that won't kill the robot...
            // We want to ram him instead for bonus points
            if (e.getEnergy() > 16) {
                fire(3);
            } else if (e.getEnergy() > 10) {
                fire(2);
            } else if (e.getEnergy() > 4) {
                fire(1);
            } else if (e.getEnergy() > 2) {
                fire(.5);
            } else if (e.getEnergy() > .4) {
                fire(.1);
            }
            ahead(40);

        }

    }
    
    //Si el robot ha matat al que estaba seguint, canvia el seu estat a "atacant"
    public void onRobotDeath(RobotDeathEvent e){
        if (lider && isTeammate(e.getName())){
            for(Map.Entry<Double,Posicio> entry : posicions.entrySet()) {
                    if(entry.getValue().getName() == e.getName()){
                        posicions.remove(entry.getKey());
                        break;
                    }
            }
            System.out.println("S'ha mort un robot per tant hauria d'haverlo esborrat, mostro la llista");
            for(Map.Entry<Double,Posicio> entry : posicions.entrySet()) {
                     System.out.println(entry.getValue().getName());
            }
            return;
        }
        if(e.getName() == nomLider && status=="esperant")
            status = "atacant";
    }

    
    public void onScannedRobot(ScannedRobotEvent e){
        
        Double x = getX();
        Double y = getY();
        
            if (isTeammate(e.getName())){
                System.out.println("Bearing = "+status);
                if((e.getDistance() <= 150.0) && ((e.getBearing() >= -35.0) && (e.getBearing() <= 35.0))){
                    stop();
                    //turnRight(90);/**/
                    //setAhead(200);/**/
                    setTurnRadarRight(36000000);
                    execute();
                    //nextCorner=(nextCorner-1+4)%4;
                    //doNothing();
                    //resume();
                }
            return;
            }

            //si es enemigo y esta mas cerca de 40 se le persigue - (si status no es arribant)
            else{
                if (e.getDistance() < 30.0){
                // Si l'enemic està a una distància inferior a 50, el seguim
                // getBearing() ens retorna la direcció del robot escanejat respecte el nostre robot (en graus)
                    if(status == "arribant"){
                        status = "atacant";
                        try {
                            sendMessage(nomLider,new Missatge("Ja estic a la meva posi"));
                        } catch (IOException ex) {
                            Logger.getLogger(padel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    System.out.println("Status = "+status);
                        enemyName = e.getName();
                    }

                        Double rotacio;
                        if (e.getBearing() >= 0) {
                        rotacio = 1.0;
                    } else {
                        rotacio = -1.0;
                    }
            turnRight(e.getBearing());
                    setAhead(e.getDistance());
                    fire(3);
                }
                if(status!="arribant" && e.getDistance()<(getBattleFieldWidth()/2)){
                //if(status != "kamikaze" && status != "arribant")
                    //stop();
                
                Double enemyBearing = getHeading() + e.getBearing();
                Double dx = e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
                Double dy = e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
                Double theta = Math.toDegrees(Math.atan2(dx, dy));
                turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
                fire(3);
                //if(status != "kamikaze" && status != "arribant")
                  //  resume();
                  scan();
                  setTurnRadarRight(36000000);//funcion que el radar da vueltas para disparar robots mientras los compañeros llegan a su posi de inicio
                  execute();
            }else if(e.getDistance()<(getBattleFieldWidth()/2))
                fire(3);
        }
    }

    public void goToPosition() throws IOException {
        while(posicions.get(xi).getX() == 0.0){
            sendMessage(nomLider,new Missatge("On vaig?"));
            doNothing();
        }
        goForReal(posicions.get(xi).getX(),posicions.get(xi).getY());
    }
    
    public void centinella(){/*
        if(getX()<(getBattleFieldWidth()/2))
             turnRight(90-getHeading());
        else
            turnRight(270-getHeading());
         ahead(100);
         doNothing();
         back(100);*/
    }

}