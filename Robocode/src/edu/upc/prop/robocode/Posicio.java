/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.prop.robocode;

/**
 *
 * @author roberto
 */
public class Posicio {
    
    private String name;
    private double x;
    private double y;
    private double z=4;

    public double getZ() {
        return z;
    }

    public Posicio(String name, double x, double y,double z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z=z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    
}
