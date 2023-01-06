/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.software.Control;

import com.software.DTO.DTOUser;
import com.software.Models.EjecutarSentencias;
import com.software.Models.Consultas;
import com.software.Models.MySql;
import com.software.Models.Oracle;
import com.software.Models.Postgresql;
import com.software.Models.SingletonDataBase;
import com.software.Models.SqlServer;
import java.sql.Connection;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author drago
 */
public class ControlDBs {

    static DTOUser dtoUser;
    Consultas cons = new Consultas();
    Oracle oracle = new Oracle();
    MySql mysql = new MySql();
    Postgresql postgres = new Postgresql();
    SqlServer sqlserver = new SqlServer();

    EjecutarSentencias ejSentencias = new EjecutarSentencias();

    public Connection Login() {

        SingletonDataBase.getSingleton().setDTO(dtoUser);
        switch (dtoUser.getDB()) {
            case "Oracle" -> {
                oracle.setDTO(dtoUser);
                SingletonDataBase.getSingleton().setDatosConexion(oracle.datosConexion()[0], oracle.datosConexion()[1], oracle.datosConexion()[2], oracle.datosConexion()[3]);
                try {
                    return SingletonDataBase.getSingleton().connect();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
            case "SQLServer" -> {
                sqlserver.setDTO(dtoUser);
                SingletonDataBase.getSingleton().setDatosConexionSQLServer(sqlserver.datosConexion());
                try {
                    return SingletonDataBase.getSingleton().connect();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
            case "Mysql" -> {
                mysql.setDTO(dtoUser);
                SingletonDataBase.getSingleton().setDatosConexion(mysql.datosConexion()[0], mysql.datosConexion()[1], mysql.datosConexion()[2], mysql.datosConexion()[3]);
                try {
                    return SingletonDataBase.getSingleton().connect();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
            case "Postgres" -> {
                postgres.setDTO(dtoUser);
                SingletonDataBase.getSingleton().setDatosConexion(postgres.datosConexion()[0], postgres.datosConexion()[1], postgres.datosConexion()[2], postgres.datosConexion()[3]);
                try {
                    return SingletonDataBase.getSingleton().connect();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
            default -> {
            }
        }
        return null;
    }

    public void Desconectar() {
        SingletonDataBase.getSingleton().desconnect();
    }

    public void InfoTablas(JTable tableDescribe, JTable tableInfo, String tabla) {
        switch (dtoUser.getDB()) {
            case "Oracle" ->
                cons.InfoTablas(tableDescribe, tableInfo, oracle.getSentenciasTablas(tabla)[0], oracle.getSentenciasTablas(tabla)[1]);
            case "Mysql" ->
                cons.InfoTablas(tableDescribe, tableInfo, mysql.getSentenciasTablas(tabla)[0], mysql.getSentenciasTablas(tabla)[1]);
            case "SQLServer" ->
                cons.InfoTablas(tableDescribe, tableInfo, sqlserver.getSentenciasTablas(tabla)[0], sqlserver.getSentenciasTablas(tabla)[1]);
            default -> {
            }
        }
    }

    public void InfoTablas(String esquema, JTable tableDescribe, JTable tableInfo, String tabla) {
        cons.InfoTablas(tableDescribe, tableInfo, postgres.getSentenciasTablas(tabla)[0], postgres.getSentenciasTablas(esquema, tabla)[1]);
    }

    public ArrayList InfoMetodos(JTable tableInfo, String seleccion) {
        if (dtoUser.getDB().equals("Oracle")) {
            return cons.InfoMetodos(tableInfo, oracle.getSentenciaMetodo(seleccion));
        } else if (dtoUser.getDB().equals("SQLServer")) {
            return cons.InfoMetodosSQLServer(tableInfo, sqlserver.getSentenciaMetodo(seleccion));
        }
        return null;
    }

    public ArrayList InfoMetodosMysql(JTable tableInfo, String seleccion, String tipo) {
        if (dtoUser.getDB().equals("Mysql") && tipo.equals("VIEW")) {
            return cons.InfoVistasMysql(mysql.getSentenciaMetodo(seleccion, tipo));
        } else {
            return cons.InfoMetodosMysql(tableInfo, mysql.getSentenciaMetodo(seleccion, tipo));
        }
    }

    public String LlamarDDL(String tablaSelected) {
        switch (dtoUser.getDB()) {
            case "Oracle" -> {
                return cons.LlamarDDL(oracle.getDDL(tablaSelected), 1);
            }
            case "Mysql" -> {
                return cons.LlamarDDL(mysql.getDDL(tablaSelected), 2);
            }
            case "SQLServer" -> {
                return cons.LlamarDDL(sqlserver.getDDL(tablaSelected), 1);
            }
            default -> {
            }
        }
        return "";
    }

    public String LlamarDDL(String tablaSelected, String padre) {
        String sql = "";
        switch (padre) {
            case "Funciones", "Procedimientos" ->
                sql = postgres.getDDLFuntProc(tablaSelected);
            case "Triggers" ->
                sql = postgres.getDDLTrigger(tablaSelected);
            case "Vistas" ->
                sql = postgres.getDDLView(tablaSelected);
            case "Tablas" ->
                sql = postgres.getDDLTable(tablaSelected);
            default -> {
            }
        }
        if (dtoUser.getDB().equals("Postgres")) {
            return cons.LlamarDDL(sql, 1);
        }
        return "";
    }

    public void guardarArchivo(String Sentencias) {
        Oracle.guardarArchivo(Sentencias);
    }

    public void EjecutarSelect(JTable tableInfo, String sql) {
        ejSentencias.EjecutarSelect(tableInfo, sql);
    }

    public void EjecutarDescribe(JTable tableInfo, String table) {
        ejSentencias.EjecutarSelect(tableInfo, oracle.getDescribeTable(table));
    }

    public void EjecutarSentencia(String sql, String item) {
        ejSentencias.EjecutarSentencia(sql, item);
    }

    public void executeDDLfunction() {
        ejSentencias.EjecutarSentencia(postgres.getDDLFunction());
    }

    public ArrayList getErrores() {
        return ejSentencias.getErrores();
    }

    public static ArrayList<String> getTables() {
        switch (dtoUser.getDB()) {
            case "Oracle" -> {
                return Oracle.getTables();
            }
            case "Mysql" -> {
                return MySql.getTables();
            }
            case "SQLServer" -> {
                return SqlServer.getTables();
            }
            default -> {
            }
        }
        return null;
    }

    public static ArrayList<String> getTables(String esquema) {
        return Postgresql.getTables(esquema);
    }

    public static void createBackup() {
        if (dtoUser.getDB().equals("Oracle")) {
            Oracle.createBackup();
        }
    }

    public String getCodigos() {
        return Oracle.getArregloCodigos();
    }

    public static ArrayList<String> getSchemas() {
        if (dtoUser.getDB().equals("Postgres")) {
            return Postgresql.getSchemas();
        }
        return null;
    }

    public static ArrayList<String> getFunctions() {
        switch (dtoUser.getDB()) {
            case "Oracle" -> {
                return Oracle.getFunctions();
            }
            case "Mysql" -> {
                return MySql.getFunctions();
            }
            case "SQLServer" -> {
                return SqlServer.getFunctions();
            }
            default -> {
            }
        }
        return null;
    }

    public static ArrayList<String> getFunctions(String esquema) {
        if (dtoUser.getDB().equals("Postgres")) {
            return Postgresql.getFunctions(esquema);
        }
        return null;
    }

    public static ArrayList<String> getProcedures() {
        switch (dtoUser.getDB()) {
            case "Oracle" -> {
                return Oracle.getProcedures();
            }
            case "Mysql" -> {
                return MySql.getProcedures();
            }
            case "SQLServer" -> {
                return SqlServer.getProcedures();
            }
            default -> {
            }
        }
        return null;
    }

    public static ArrayList<String> getProcedures(String esquema) {
        if (dtoUser.getDB().equals("Postgres")) {
            return Postgresql.getProcedures(esquema);
        }
        return null;
    }

    public static ArrayList<String> getTriggers() {
        switch (dtoUser.getDB()) {
            case "Oracle" -> {
                return Oracle.getTriggers();
            }
            case "Mysql" -> {
                return MySql.getTriggers();
            }
            case "SQLServer" -> {
                return SqlServer.getTriggers();
            }
            default -> {
            }
        }
        return null;
    }

    public static ArrayList<String> getTriggers(String esquema) {
        if (dtoUser.getDB().equals("Postgres")) {
            return Postgresql.getTriggers(esquema);
        }
        return null;
    }

    public static ArrayList<String> getViews() {
        switch (dtoUser.getDB()) {
            case "Oracle" -> {
                return Oracle.getViews();
            }
            case "Mysql" -> {
                return MySql.getViews();
            }
            case "SQLServer" -> {
                return SqlServer.getViews();
            }
            default -> {
            }
        }
        return null;
    }

    public static ArrayList<String> getViews(String esquema) {
        if (dtoUser.getDB().equals("Postgres")) {
            return Postgresql.getViews(esquema);
        }
        return null;
    }

    public static ArrayList<String> getPackages() {
        if (dtoUser.getDB().equals("Oracle")) {
            return Oracle.getPackages();
        }
        return null;
    }

    public static ArrayList<String> getUsers() {
        switch (dtoUser.getDB()) {
            case "Oracle" -> {
                return Oracle.getUsers();
            }
            case "Mysql" -> {
                return MySql.getUsers();
            }
            case "SQLServer" -> {
                return SqlServer.getUsers();
            }
            case "Postgres" -> {
                return Postgresql.getUsers();
            }
            default -> {
            }
        }
        return null;
    }

    public void setDTO(DTOUser dtouser) {
        ControlDBs.dtoUser = dtouser;
    }

}
