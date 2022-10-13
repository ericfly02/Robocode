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
    private Double xi;      // Posiocio inicial x del robot
    private Double yi;      // Posiocio inicial y del robot
    private Double x;       // Posiocio x a la que ha de dirigir-se el robot
    private Double y;       // Posiocio y a la que ha de dirigir-se el robot
    private Boolean preparat;

    public Posicio(String name, Double xi, Double yi) {
        this.name = name;
        this.xi = xi;
        this.yi = yi;
        this.x=0.0;
        this.y=0.0;
        this.preparat=false;
    }

    public String getName() {
        return name;
    }

    public Double getXi() {
        return xi;
    }

    public Double getYi() {
        return yi;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }
    
    public Boolean isReady() {
        return preparat;
    }

    public void jaEstaPreparat() {
        preparat=true;
    }
    
}