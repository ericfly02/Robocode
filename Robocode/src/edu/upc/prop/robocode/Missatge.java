/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.prop.robocode;

/**
 *
 * @author roberto
 */
public class Missatge implements java.io.Serializable {
    
    private String text;
    private double x;
    private double y;
    
    public Missatge(String m){
        this.text=m;
    }
    
    public Missatge(String m,double X, double Y){
        this.text=m;
        this.x=X;
        this.y=Y;
    }
    
    public String getText() {
        return text;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
}
