/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.software.DTO;

/**
 *
 * @author drago
 */
public class DTOUser {

    private final String DB;
    private final String USER;
    private final String PASSWORD;
    private final String Instancia;
    private final String IpServidor;
    private final String Puerto;

    public DTOUser(String DB, String USER, String PASSWORD, String Instancia, String IpServidor, String Puerto) {
        this.DB = DB;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        this.Instancia = Instancia;
        this.IpServidor = IpServidor;
        this.Puerto = Puerto;
    }

    public String getDB() {
        return DB;
    }

    public String getUSER() {
        return USER;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public String getInstancia() {
        return Instancia;
    }

    public String getIpServidor() {
        return IpServidor;
    }

    public String getPuerto() {
        return Puerto;
    }
    
    

}
