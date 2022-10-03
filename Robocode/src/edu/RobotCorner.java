package edu.upc.prop.robocode;
import robocode.*;

/**
 *
 * @author speed
 */
public class RobotCorner extends AdvancedRobot{

    public void run(){
        goToCorner();
    }
    
    public void goToCorner(){
        Integer x = getX();
        Integer y = getY();
        Integer width = getBattleFieldWidth();
        Integer height = getBattleFieldHeight();
        Integer heading = getHeading();
        Boolean disponible[4]=true;

        // Es mou abaix a l'esquerra
        if (x < width/2 && y < height/2){
            moveCorner1();
        }
        
        // Es mou abaix a la dreta
        else if (x > width/2 && y < height/2){
            moveCorner2();
        }

        // E·s mou adalt a l'esquerra
        else if (x < width/2 && y > height/2){
            moveCorner3();
        }

        // Es mou adalt a la dreta
        else if (x > width/2 && y > height/2){
            moveCorner4();
        }
    }

    public void moveCorner1(){
        if(disponible[0]){
            broadcastMessage();        
            turnRight(heading);
            ahead(x);
            turnLeft(90);
            ahead(y);
        }
    }

    public void moveCorner2(){
        turnRight(heading);
        ahead(width-x);
        turnLeft(90);
        ahead(y);
    }

    public void moveCorner3(){
        turnRight(heading);
        ahead(x);
        turnLeft(90);
        ahead(height-y);
    }

    public void moveCorner4(){
        turnRight(heading);
        ahead(width-x);
        turnLeft(90);
        ahead(height-y);
    }
    
}
