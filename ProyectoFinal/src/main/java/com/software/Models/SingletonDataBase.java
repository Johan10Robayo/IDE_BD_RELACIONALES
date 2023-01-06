package com.software.Models;

import com.software.DTO.DTOUser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author drago
 */
public class SingletonDataBase {

    static DTOUser dtoUser;
    private static SingletonDataBase Singleton;

    private String Driver;
    private String URL;
    private String USER;
    private String PASSWORD;
    private static Connection cadena;

    public static SingletonDataBase getSingleton() {
        if (SingletonDataBase.Singleton == null) {
            SingletonDataBase.Singleton = new SingletonDataBase();
        }
        return SingletonDataBase.Singleton;
    }

    private SingletonDataBase() {
    }

    public Connection connect() {
        if (SingletonDataBase.cadena == null) {
            switch (dtoUser.getDB()) {
                case "Oracle", "Postgres" -> {
                    try {
                        Class.forName(Driver);
                        this.cadena = DriverManager.getConnection(URL, USER, PASSWORD);
                        this.cadena.setAutoCommit(true);
                        return SingletonDataBase.cadena;
                    } catch (ClassNotFoundException | SQLException e) {
                        System.out.println("Error " + e);
                        return null;
                    }
                }
                case "SQLServer" -> {
                    try {
                        this.cadena = DriverManager.getConnection(URL);
                        this.cadena.setAutoCommit(true);
                        return SingletonDataBase.cadena;
                    } catch (SQLException e) {
                        System.out.println("Error " + e);
                        return null;
                    }
                }
                case "Mysql" -> {
                    try {
                        Class.forName(Driver);

                        this.cadena = DriverManager.getConnection(URL, USER, PASSWORD);
                        this.cadena.setAutoCommit(true);
                        return SingletonDataBase.cadena;
                    } catch (ClassNotFoundException | SQLException e) {
                        System.out.println("Error " + e);
                        return null;
                    }
                }
                default -> {
                }
            }
        }

        return SingletonDataBase.cadena;
    }

    public void desconnect() {
        try {
            SingletonDataBase.cadena.close();
            SingletonDataBase.Singleton = null;
            SingletonDataBase.cadena = null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void setDatosConexion(String Driver, String URL, String USER, String PASSWORD) {
        this.Driver = Driver;
        this.URL = URL;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
    }

    public void setDatosConexionSQLServer(String URL) {
        this.URL = URL;
    }

    public void setDTO(DTOUser dtouser) {
        SingletonDataBase.dtoUser = dtouser;
    }

}
