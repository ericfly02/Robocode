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
    
    public Missatge(String m){
        this.text=m;
    }
    
    public String getText() {
        return text;
    }
}
