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
    private Double x;
    private Double y;
    private Double z;
    private Double t;

    public Missatge(String text, Double x, Double y, Double z, Double t) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.z = z;
        this.t = t;
    }

    public Double getZ() {
        return z;
    }

    public Double getT() {
        return t;
    }
    
    public Missatge(String m){
        this.text=m;
    }
    
    public Missatge(String m,Double X, Double Y){
        this.text=m;
        this.x=X;
        this.y=Y;
    }
    
    public String getText() {
        return text;
    }
    
    public Double getX() {
        return x;
    }
    
    public Double getY() {
        return y;
    }
    
}
