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
    private double z;
    private double t;

    public Missatge(String text, double x, double y, double z, double t) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.z = z;
        this.t = t;
    }

    public double getZ() {
        return z;
    }

    public double getT() {
        return t;
    }
    
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
