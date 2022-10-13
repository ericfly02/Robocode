/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.prop.robocode;

/**
 *
 * @author speed
 */
public class padel {

    // Creem les variables privades que necessitem
    private double xi;               // Coordenada inicial x del robot
    private double yi;               // Coordenada inicial y del robot
    private String nomLider = null;
    private Boolean lider = false;
    private Map<double, Posicio> posicions = null;
    private Integer posicionsRebudes = 0;


    public void run(){
        // Modifiquem el color del robot
        setBodyColor(Color.black);
        setGunColor(Color.black);
        setRadarColor(Color.black);
        setBulletColor(Color.black);
        setScanColor(Color.black);

        lider=(getName().contains("(1)"));

        if(lider){

            // Esperem a que tots els robots enviin les seves posicions
            rebrePosicions();

            try {
                // Enviem les posicions a tots els robots
                enviaPosicions();
            } catch (IOException ex) {
                Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
            }
            //que vagi al lloc que li toca, inicial
            goTo(posicions.get(xi).getX(),posicions.get(xi).getY());
        }
        
        else{
            
            try {
                // Enviar posicio inicial al lider, i esperar posició a la que s'ha d'anar, i anar-hi
                goToPosition();
            } catch (IOException ex) {
                Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        while(true){
            // Els robots es mous en voltant del taulell en sentit horari
            voltes_al_taulell();
        }
    }

    public void rebrePosicions(){
        posicions = new TreeMap<double,Posicio>;
        double xi = getX(), yi = getY();
        posicions.put(xi,new Posicio(nomLider, xi, yi));
        while(posicionsRebudes<5){
            broadcastMessage(new Missatge("Necessito dades"));
            doNothing();
        }
    }

    public void enviaPosicions(){
        double y_mitja;
        for(Map.Entry<String,Integer> entry : treeMap.entrySet()) {
            y_miyja+=entry.getKey();
        }
        y_miyja/=5;
        if(y_mitja > (getBattleFieldHeight()/2)){
            y_mitja = getBattleFieldHeight()-20;
        }else{
            y_mitja = 20;
        }
        double send_x = 20;
        for(Map.Entry<String,Integer> entry : treeMap.entrySet()) {
            if(entry.getValue().getName() != getName())
                sendMessage(entry.getValue().getName(),new Missatge("Ves a",send_x,y_mitja));
            entry.getValue().setX(send_x);
            entry.getValue().setY(y_mitja);
            send_x+=(getBattleFieldWidth()-40)/4;
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

    public void onMessageReceived(MessageEvent e){
        
        try {
            Missatge M = (Missatge) e.getMessage();
            String m=M.getText();
            switch(m){
                case "Ves a":
                    if(posicions != null)
                        break;
                    posicions = new TreeMap<double,Posicio>;
                    posicions.put(xi,new Posicio(getName(),xi,yi,M.getX(),M.getY());
                    System.out.println("M'han comunicat que vagi al punt ("+M.getX()+" , "+M.getY()+")");
                    break;
                case "Necessito dades":
                    sendMessage(e.getSender(),new Missatge("Toma dades",xi,yi));
                    doNothing();
                    break;
                case "Toma dades":
                    nouMembre(e.getSender(),M.getX(),M.getY());
                    doNothing();
                    break;
                case "On vaig capità?":
                    if(posicionsRebudes<5) 
                        nouMembre(e.getSender(),M.getX(),M.getY());  // Incrementar posicionsRebudes
                    else{
                        sendMessage(e.getSender(),new Missatge("Ves a",posicio(e.getSender()),(double)0));
                    }
                    break;

                case "Stop":
                    stop();
                    break;               
                default:
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    public void nouMembre(String name,double xx,double yy){
        if(posicions.containsKey(xx)){
            Boolean trobat=false;
            for(int i = 0;i<5 && !trobat;++i){
                if(posicions.get(xx+i*0.000001) != null && posicions.get(xx+i*0.000001).getName() == name)
                    trobat=true;
            }
            if (!trobat){
                for(int i = 0;i<5;++i){
                   if(posicions.get(xx+i*0.000001) == null){}
                        posicions.put(xx+i*0.000001, new Posicio(name, xx, yy);)
                        break;
                }
            }
        }else{
          posicions.put(xx, new Posicio(name, xx, yy);)

        }
    }
    
    public void voltes_al_taulell(){
        // Els robots es mous en voltant del taulell en sentit horari
        Integer maxHeight = getBattleFieldHeight() - 20;
        Integer maxWidth = getBattleFieldWidth() - 20;

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
    
    // Si el robot es xoca contra algun enemic, tots els altres es paren i ataquen
    public void onHitRobot(HitRobotEvent e){
        // es comunica als altres robot que tambe han de parar
        broadcastMessage(new Missatge("Stop"));
        
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
        


    }
    
}


public void onScannedRobot(ScannedRobotEvent e){
        x = getX();
        y = getY();
        if (isTeammate(e.getName())){
            // Obtenim posició del robot aliat
           double posicio_x = x + e.getDistance() * Math.sin(Math.toRadians(getHeading() + e.getBearing()));
           double posicio_y = y + e.getDistance() * Math.cos(Math.toRadians(getHeading() + e.getBearing()));
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
 
        }else{
            if(lider && millora2){
                    double enemyBearing = getHeading() + e.getBearing();
                    double dx = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
                    double dy = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
                    try {
                        broadcastMessage(new Missatge("Ataca",dx,dy,e.getHeading(),e.getVelocity()));
                    } catch (IOException ex) {
                        Logger.getLogger(RoboCorner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            if(kamikaze){
                    // Si el robot escanejat és un enemic, el seguim
                    if (e.getDistance() < 0){
                        // Si l'enemic està a una distància inferior a 100, el seguim
                        // getBearing() ens retorna la direcció del robot escanejat respecte el nostre robot (en graus)
                        setTurnRight(e.getBearing());
                        setAhead(e.getDistance());
                        fire(3);
                    }
                    else{
                        // Si l'enemic està a una distància superior a 100, el seguim
                        setTurnRight(e.getBearing());
                        setAhead(e.getDistance());
                        fire(3);
                    }
                
            }
            else{
                    /*double enemyBearing = getHeading() + e.getBearing();
                    double dx = e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
                    double dy = e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
                    double theta = Math.toDegrees(Math.atan2(dx, dy));
                    turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
                    fire(3);*/
                    System.out.println("Vull disparar a "+e.getName());
                    double enemyBearing = getHeading() + e.getBearing();
                    double px = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
                    double py = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
                    fireMovingTarget(px, py, e.getHeading(), e.getVelocity());
            }
        }
    }

}
