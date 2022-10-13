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
    
    private String name;    // Nom del robot 
    private double xi;      // Posiocio inicial x del robot
    private double yi;      // Posiocio inicial y del robot
    private double x;       // Posiocio x a la que ha de dirigir-se el robot
    private double y;       // Posiocio y a la que ha de dirigir-se el robot

    public Posicio(String name, double xi, double yi) {
        this.name = name;
        this.xi = xi;
        this.yi = yi;
        this.x=0;
        this.y=0;
    }

    public String getName() {
        return name;
    }

    public double getXi() {
        return xi;
    }

    public double getYi() {
        return yi;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    
}
