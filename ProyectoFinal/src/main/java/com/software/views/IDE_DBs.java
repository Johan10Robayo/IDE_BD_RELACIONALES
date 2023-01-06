/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.software.views;

import com.software.Control.ControlDBs;
import com.software.DTO.DTOUser;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author drago
 */
public final class IDE_DBs extends javax.swing.JInternalFrame {

    /**
     * Creates new form IDE_DBs
     */
    DTOUser dtouser;
    ControlDBs ctr = new ControlDBs();
    String originalSql;
    String Sentencia = "";
    DefaultMutableTreeNode user_raiz = new DefaultMutableTreeNode("Usuarios");
    DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Esquemas");
    DefaultMutableTreeNode tablas_raiz = new DefaultMutableTreeNode("Tablas");
    DefaultMutableTreeNode funciones_raiz = new DefaultMutableTreeNode("funciones");
    DefaultMutableTreeNode paquetes_raiz = new DefaultMutableTreeNode("Paquetes");
    DefaultMutableTreeNode procedimientos_raiz = new DefaultMutableTreeNode("Procedure");
    DefaultMutableTreeNode triggers_raiz = new DefaultMutableTreeNode("Trigger");
    DefaultMutableTreeNode vistas_raiz = new DefaultMutableTreeNode("Vistas");
    DefaultMutableTreeNode usuarios_raiz = new DefaultMutableTreeNode("Usuarios");

    public ArrayList<ArrayList<DefaultMutableTreeNode>> agregarEsquema() {
        ArrayList<String> esquemas = ControlDBs.getSchemas();

        ArrayList<ArrayList<DefaultMutableTreeNode>> ret = new ArrayList<>();

        ArrayList<DefaultMutableTreeNode> esq = new ArrayList<>();
        ArrayList<DefaultMutableTreeNode> tab = new ArrayList<>();
        ArrayList<DefaultMutableTreeNode> funciones = new ArrayList<>();
        ArrayList<DefaultMutableTreeNode> procedimientos = new ArrayList<>();
        ArrayList<DefaultMutableTreeNode> triggers = new ArrayList<>();
        ArrayList<DefaultMutableTreeNode> vistas = new ArrayList<>();

        for (String aux : esquemas) {
            DefaultMutableTreeNode esquemas_raiz = new DefaultMutableTreeNode(aux);
            DefaultMutableTreeNode tablas_raiz = new DefaultMutableTreeNode("Tablas");
            DefaultMutableTreeNode funciones_raiz = new DefaultMutableTreeNode("Funciones");
            DefaultMutableTreeNode procedimientos_raiz = new DefaultMutableTreeNode("Procedimientos");
            DefaultMutableTreeNode triggers_raiz = new DefaultMutableTreeNode("Triggers");
            DefaultMutableTreeNode vistas_raiz = new DefaultMutableTreeNode("Vistas");

            esq.add(esquemas_raiz);
            tab.add(tablas_raiz);
            funciones.add(funciones_raiz);
            procedimientos.add(procedimientos_raiz);
            triggers.add(triggers_raiz);
            vistas.add(vistas_raiz);
        }
        ret.add(esq);
        ret.add(tab);
        ret.add(funciones);
        ret.add(procedimientos);
        ret.add(triggers);
        ret.add(vistas);

        return ret;
    }

    public IDE_DBs(DTOUser dtouser) {
        this.dtouser = dtouser;
        initComponents();
        if (dtouser.getDB().equals("Postgres")) {
            llenarArbolPostgres();
            ctr.executeDDLfunction();
        } else {
            llenarArbol();
        }
        setTittle();
        cargarTiuloJTabbed(dtouser);
    }

    public void recorrerArbol(ArrayList<String> datos, DefaultMutableTreeNode nodo) {
        try {
            for (String dato : datos) {
                DefaultMutableTreeNode aux = new DefaultMutableTreeNode();
                aux.setUserObject(dato);
                nodo.add(aux);
            }
        } catch (Exception e) {
        }
    }

    public void llenarArbol() {
        raiz.setUserObject("Listado");

        raiz.add(tablas_raiz);
        raiz.add(funciones_raiz);
        if (dtouser.getDB().toUpperCase().equals("ORACLE")) {
            raiz.add(paquetes_raiz);
        }
        raiz.add(procedimientos_raiz);
        raiz.add(procedimientos_raiz);
        raiz.add(triggers_raiz);
        raiz.add(vistas_raiz);
        raiz.add(usuarios_raiz);

        recorrerArbol(ControlDBs.getTables(), tablas_raiz);
        recorrerArbol(ControlDBs.getFunctions(), funciones_raiz);
        if (dtouser.getDB().toUpperCase().equals("ORACLE")) {
            recorrerArbol(ControlDBs.getPackages(), paquetes_raiz);
        }
        recorrerArbol(ControlDBs.getProcedures(), procedimientos_raiz);
        recorrerArbol(ControlDBs.getTriggers(), triggers_raiz);
        recorrerArbol(ControlDBs.getViews(), vistas_raiz);
        recorrerArbol(ControlDBs.getUsers(), usuarios_raiz);

    }

    public void llenarArbolPostgres() {
        ArrayList<ArrayList<DefaultMutableTreeNode>> arbol = this.agregarEsquema();

        ArrayList<DefaultMutableTreeNode> esquema = arbol.get(0);
        ArrayList<DefaultMutableTreeNode> tablas = arbol.get(1);
        ArrayList<DefaultMutableTreeNode> funciones = arbol.get(2);
        ArrayList<DefaultMutableTreeNode> procedimientos = arbol.get(3);
        ArrayList<DefaultMutableTreeNode> triggers = arbol.get(4);
        ArrayList<DefaultMutableTreeNode> vistas = arbol.get(5);

        for (int i = 0; i < esquema.size(); i++) {
            raiz.add(esquema.get(i));
            esquema.get(i).add(tablas.get(i));
            esquema.get(i).add(funciones.get(i));
            esquema.get(i).add(procedimientos.get(i));
            esquema.get(i).add(triggers.get(i));
            esquema.get(i).add(vistas.get(i));

            recorrerArbol(ControlDBs.getTables(esquema.get(i).toString()), tablas.get(i));
            recorrerArbol(ControlDBs.getFunctions(esquema.get(i).toString()), funciones.get(i));
            recorrerArbol(ControlDBs.getProcedures(esquema.get(i).toString()), procedimientos.get(i));
            recorrerArbol(ControlDBs.getTriggers(esquema.get(i).toString()), triggers.get(i));
            recorrerArbol(ControlDBs.getViews(esquema.get(i).toString()), vistas.get(i));
        }
        raiz.add(user_raiz);
        recorrerArbol(ControlDBs.getUsers(), user_raiz);
    }

    private boolean comprobarSentencia(String sentencia, String areaTexto) {
        return Pattern.compile(Pattern.quote(sentencia), Pattern.CASE_INSENSITIVE).matcher(areaTexto).find();
    }

    private void cargarTiuloJTabbed(DTOUser dtouser) {
        switch (dtouser.getDB().toUpperCase()) {
            case "ORACLE" ->
                jTabbedPane_Sentencias.setTitleAt(1, "PLSQL");
            case "MYSQL" ->
                jTabbedPane_Sentencias.setTitleAt(1, "MySQL");
            case "POSTGRES" ->
                jTabbedPane_Sentencias.setTitleAt(1, "PSQL");
            case "SQLSERVER" ->
                jTabbedPane_Sentencias.setTitleAt(1, "TSQL");
            default -> {
            }
        }
    }

    private String unificarArray(ArrayList<String> array) {
        String arrayUnido = "";
        for (int i = 0; i < array.size(); i++) {
            arrayUnido = arrayUnido + array.get(i);
        }
        return arrayUnido;
    }

    public boolean identificarNodo(String nodo, String seleccion) {
        return jTree.getSelectionPath().toString().equals("[Listado, " + nodo + ", " + seleccion + "]");
    }

    public boolean identificarNodoP(String nombreEsq, String nodo, String seleccion) {
        return jTree.getSelectionPath().toString().equals("[Esquemas, " + nombreEsq + ", " + nodo + ", " + seleccion + "]");
    }

    public void removeAllChildren() {
        raiz.removeAllChildren();
        tablas_raiz.removeAllChildren();
        funciones_raiz.removeAllChildren();
        paquetes_raiz.removeAllChildren();
        procedimientos_raiz.removeAllChildren();
        triggers_raiz.removeAllChildren();
        vistas_raiz.removeAllChildren();
        usuarios_raiz.removeAllChildren();
        if (dtouser.getDB().equals("Postgres")) {
            user_raiz.removeAllChildren();
        }
    }

    public void setTittle() {
        String tittle = "Motor : " + dtouser.getDB() + " - Usuario : " + dtouser.getUSER() + " - IP Servidor : " + dtouser.getIpServidor() + " - Sesión : " + dtouser.getInstancia();
        this.setTitle(tittle);
    }

    public String quitarPuntoyComa(String sentencia) {
        if (sentencia.contains(";")) {
            return sentencia.replaceAll(";", "");
        } else {
            return sentencia;
        }
    }

    public String getTable(String texto) {
        texto = texto.replaceAll("describe ", "");
        return texto;
    }

    private String SepararSentencia(String cadena) {
        String[] palabras = (cadena.toUpperCase()).split(" ");
        for (int i = 0; i < palabras.length; i++) {
            System.out.println(palabras[i]);
            if (palabras[i].equals("UNDEFINED") || palabras[i].equals("SQL") || palabras[i].equals("VIEW") || palabras[i].equals("SELECT") || palabras[i].equals("FROM") || palabras[i].equals("WHERE")) {
                palabras[i] = "\n" + palabras[i];
            } else if (palabras[i].equals("CREATE") || palabras[i].equals("ALGORITHM=UNDEFINED")) {
                palabras[i] = palabras[i] + "\n";
            }
        }
        return String.join(" ", palabras);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea_Error = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane_Tablas = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTableInfo1 = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableInfo2 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane_Sentencias = new javax.swing.JTabbedPane();
        jPanel_SQL = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea_SQL = new javax.swing.JTextArea();
        jPanel_Varios = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea_Varios = new javax.swing.JTextArea();
        jPanel_DDL = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea_DDL = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        clear = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        Recargar = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree = new javax.swing.JTree(raiz);

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximizable(true);
        setResizable(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/cambio.png"))); // NOI18N
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextArea_Error.setEditable(false);
        jTextArea_Error.setColumns(20);
        jTextArea_Error.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jTextArea_Error.setRows(5);
        jTextArea_Error.setText("*Mensajes de Error*\n");
        jScrollPane2.setViewportView(jTextArea_Error);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1400, 100));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 580, 1420, 120));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane_Tablas.setFont(new java.awt.Font("Calisto MT", 1, 18)); // NOI18N

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setFont(new java.awt.Font("Calisto MT", 0, 14)); // NOI18N
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTableInfo1.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jTableInfo1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableInfo1.setColumnSelectionAllowed(true);
        jTableInfo1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTableInfo1.setFocusTraversalPolicyProvider(true);
        jTableInfo1.setOpaque(false);
        jScrollPane7.setViewportView(jTableInfo1);

        jPanel6.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1100, 120));

        jTabbedPane_Tablas.addTab("Estructura", jPanel6);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTableInfo2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableInfo2.setEnabled(false);
        jScrollPane3.setViewportView(jTableInfo2);

        jPanel7.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1100, 120));

        jTabbedPane_Tablas.addTab("Datos", jPanel7);

        jPanel3.add(jTabbedPane_Tablas, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1130, 170));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 380, 1150, 190));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setFont(new java.awt.Font("Calisto MT", 1, 18)); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane_Sentencias.setBackground(new java.awt.Color(204, 204, 204));
        jTabbedPane_Sentencias.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane_Sentencias.setFont(new java.awt.Font("Calisto MT", 1, 18)); // NOI18N

        jPanel_SQL.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_SQL.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextArea_SQL.setColumns(20);
        jTextArea_SQL.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        jTextArea_SQL.setRows(5);
        jScrollPane4.setViewportView(jTextArea_SQL);

        jPanel_SQL.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1080, 230));

        jTabbedPane_Sentencias.addTab("SQL", jPanel_SQL);

        jPanel_Varios.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_Varios.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextArea_Varios.setColumns(20);
        jTextArea_Varios.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        jTextArea_Varios.setRows(5);
        jScrollPane5.setViewportView(jTextArea_Varios);

        jPanel_Varios.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1080, 230));

        jTabbedPane_Sentencias.addTab("PLSQL- TSQL - PGSQL - Otros...", jPanel_Varios);

        jPanel_DDL.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_DDL.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextArea_DDL.setColumns(20);
        jTextArea_DDL.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        jTextArea_DDL.setRows(5);
        jScrollPane6.setViewportView(jTextArea_DDL);

        jPanel_DDL.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1080, 230));

        jTabbedPane_Sentencias.addTab("DDL", jPanel_DDL);

        jPanel4.add(jTabbedPane_Sentencias, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1110, 290));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, 1150, 320));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/carpeta.png"))); // NOI18N
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 10, 40, 30));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/disco-flexible.png"))); // NOI18N
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 10, -1, -1));

        clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/archivo.png"))); // NOI18N
        clear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearMouseClicked(evt);
            }
        });
        jPanel1.add(clear, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 10, -1, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/boton-de-play.png"))); // NOI18N
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 10, -1, -1));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/magic-wand.png"))); // NOI18N
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1370, 10, -1, -1));

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Recargar.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18)); // NOI18N
        Recargar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/recargar.png"))); // NOI18N
        Recargar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Recargar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RecargarMouseClicked(evt);
            }
        });
        jPanel5.add(Recargar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 30, 40));

        jTree.setFont(new java.awt.Font("Calisto MT", 0, 12)); // NOI18N
        jTree.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTree);

        jPanel5.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 220, 510));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 260, 520));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1440, 710));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeValueChanged
        try {
            DefaultMutableTreeNode nodo;
            nodo = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
            String seleccion = (String) nodo.getUserObject();

            if (dtouser.getDB().equals("Postgres")) {
                String abuelo = nodo.getParent().getParent().toString();
                String padre = nodo.getParent().toString();
                if (identificarNodoP(abuelo, "Tablas", seleccion)) {
                    ctr.InfoTablas(abuelo, jTableInfo1, jTableInfo2, seleccion);
                    jTabbedPane_Sentencias.setSelectedIndex(2);
                    jTextArea_DDL.setText(ctr.LlamarDDL(seleccion, padre));
                    jTextArea_DDL.setCaretPosition(1);
                    jTabbedPane_Tablas.setEnabledAt(0, true);
                    jTabbedPane_Tablas.setSelectedIndex(0);
                } else if (identificarNodoP(abuelo, "Funciones", seleccion) || identificarNodoP(abuelo, "Procedimientos", seleccion)
                        || identificarNodoP(abuelo, "Triggers", seleccion) || identificarNodoP(abuelo, "Vistas", seleccion)) {
                    jTabbedPane_Sentencias.setSelectedIndex(2);
                    jTextArea_DDL.setText(ctr.LlamarDDL(seleccion, padre));
                    jTextArea_DDL.setCaretPosition(1);
                    jTabbedPane_Tablas.setEnabledAt(0, true);
                    jTabbedPane_Tablas.setSelectedIndex(0);
                }
            } else if (identificarNodo("Tablas", seleccion) || identificarNodo("Vistas", seleccion)) {
                ctr.InfoTablas(jTableInfo1, jTableInfo2, seleccion);
                jTabbedPane_Sentencias.setSelectedIndex(2);
                if (identificarNodo("Vistas", seleccion)) {
                    if ("Mysql".equals(dtouser.getDB())) {
                        String cadena = unificarArray(ctr.InfoMetodosMysql(jTableInfo1, seleccion, "VIEW"));
                        jTextArea_DDL.setText(SepararSentencia(cadena));
                    } else if ("SQLServer".equals(dtouser.getDB())) {
                        jTextArea_DDL.setText(unificarArray(ctr.InfoMetodos(jTableInfo1, seleccion)));
                    }

                } else {
                    jTextArea_DDL.setText(ctr.LlamarDDL(seleccion));
                }
                jTextArea_DDL.setCaretPosition(1);
                jTabbedPane_Tablas.setEnabledAt(0, true);
                jTabbedPane_Tablas.setSelectedIndex(0);

            } else if (identificarNodo("funciones", seleccion) || identificarNodo("Procedure", seleccion) || identificarNodo("Trigger", seleccion)) {
                if ("Mysql".equals(dtouser.getDB())) {
                    jTabbedPane_Tablas.setSelectedIndex(1);
                    jTabbedPane_Tablas.setEnabledAt(0, false);
                    if (identificarNodo("funciones", seleccion)) {
                        originalSql = "DELIMITER // \n" + unificarArray(ctr.InfoMetodosMysql(jTableInfo2, seleccion, "FUNCTION")) + " //";
                    } else if (identificarNodo("Procedure", seleccion)) {
                        originalSql = "DELIMITER // \n" + unificarArray(ctr.InfoMetodosMysql(jTableInfo2, seleccion, "PROCEDURE")) + " //";
                    } else if (identificarNodo("Trigger", seleccion)) {
                        originalSql = "DELIMITER // \n" + unificarArray(ctr.InfoMetodosMysql(jTableInfo2, seleccion, "TRIGGER")) + " //";
                    }
                    jTextArea_DDL.setText(originalSql);
                    jTabbedPane_Sentencias.setSelectedIndex(2);
                } else if ("Mysql".equals(dtouser.getDB())) {
                    jTabbedPane_Tablas.setSelectedIndex(1);
                    jTabbedPane_Tablas.setEnabledAt(0, false);
                    originalSql = unificarArray(ctr.InfoMetodos(jTableInfo2, seleccion));
                    jTextArea_DDL.setText(originalSql);
                    jTabbedPane_Sentencias.setSelectedIndex(2);
                } else {
                    jTabbedPane_Tablas.setSelectedIndex(1);
                    jTabbedPane_Tablas.setEnabledAt(0, false);
                    originalSql = "create or replace " + unificarArray(ctr.InfoMetodos(jTableInfo2, seleccion));
                    jTextArea_DDL.setText(originalSql);
                    jTabbedPane_Sentencias.setSelectedIndex(2);
                }
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_jTreeValueChanged

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        String[] Sentencias;
        switch (jTabbedPane_Sentencias.getSelectedIndex()) {
            case 0 -> {
                if (jTextArea_SQL.getSelectedText() == null) {
                    Sentencia = jTextArea_SQL.getText();
                } else {
                    Sentencia = jTextArea_SQL.getSelectedText();
                }
                Sentencias = Sentencia.split(";");
                if (Sentencias.length > 1) {
                    for (int i = 0; i < Sentencias.length; i++) {
                        ctr.EjecutarSentencia(Sentencias[i], "Tablas");
                        jTextArea_Error.setText("*Mensajes de Error*\n\n" + unificarArray(ctr.getErrores()));
                    }
                } else {
                    if (comprobarSentencia("select", Sentencia)) {
                        jTabbedPane_Tablas.setSelectedIndex(1);
                        jTabbedPane_Tablas.setEnabledAt(0, false);
                        ctr.EjecutarSelect(jTableInfo2, quitarPuntoyComa(Sentencia));
                    } else if (comprobarSentencia("describe", Sentencia) || comprobarSentencia("show", Sentencia) || comprobarSentencia("call", Sentencia)) {
                        if (dtouser.getDB().equals("Oracle")) {
                            jTabbedPane_Tablas.setSelectedIndex(1);
                            jTabbedPane_Tablas.setEnabledAt(0, false);
                            ctr.EjecutarDescribe(jTableInfo2, quitarPuntoyComa(getTable(Sentencia)));
                        } else if (dtouser.getDB().equals("Mysql")) {
                            jTabbedPane_Tablas.setSelectedIndex(1);
                            jTabbedPane_Tablas.setEnabledAt(0, false);
                            ctr.EjecutarSelect(jTableInfo2, quitarPuntoyComa(Sentencia));
                        }
                    } else if ("".equals(Sentencia) || comprobarSentencia("function", Sentencia) || comprobarSentencia("procedure", Sentencia) || comprobarSentencia("trigger", Sentencia) || comprobarSentencia("package", Sentencia)) {
                        ctr.getErrores().add("Sentencia SQL no válida\n");
                    } else {
                        ctr.EjecutarSentencia(quitarPuntoyComa(Sentencia), "Tablas");
                    }
                    jTextArea_Error.setText("*Mensajes de Error*\n\n" + unificarArray(ctr.getErrores()));
                }

            }

            case 1 -> {
                if (jTextArea_Varios.getSelectedText() == null) {
                    Sentencia = jTextArea_Varios.getText();
                } else {
                    Sentencia = jTextArea_Varios.getSelectedText();
                }
                Sentencias = Sentencia.split("//");
                if (Sentencias.length > 1) {
                    for (String Sentencia1 : Sentencias) {
                        ctr.EjecutarSentencia(Sentencia1, "-");
                    }
                } else {
                    if (comprobarSentencia("function", Sentencia)) {
                        ctr.EjecutarSentencia(Sentencia, "Function");
                    } else if (comprobarSentencia("procedure", Sentencia)) {
                        ctr.EjecutarSentencia(Sentencia, "Procedure");
                    } else if (comprobarSentencia("trigger", Sentencia)) {
                        ctr.EjecutarSentencia(Sentencia, "Trigger");
                    } else if (comprobarSentencia("package", Sentencia)) {
                        ctr.EjecutarSentencia(Sentencia, "Package");
                    } else if (comprobarSentencia("view", Sentencia)) {
                        ctr.EjecutarSentencia(Sentencia, "View");
                    } else if ("".equals(Sentencia)) {
                        ctr.getErrores().add("Sentencia no válida\n");
                    } else {
                        ctr.EjecutarSentencia(Sentencia, "-");
                    }
                }
                jTextArea_Error.setText("*Mensajes de Error*\n\n" + unificarArray(ctr.getErrores()));
            }
            default -> {
            }
        }

    }//GEN-LAST:event_jLabel6MouseClicked

    private void RecargarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RecargarMouseClicked
        removeAllChildren();
        if (dtouser.getDB().equals("Postgres")) {
            llenarArbolPostgres();
        } else {
            llenarArbol();
        }
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        model.reload();
    }//GEN-LAST:event_RecargarMouseClicked

    private void clearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearMouseClicked
        switch (jTabbedPane_Sentencias.getSelectedIndex()) {
            case 0 ->
                jTextArea_SQL.setText("");
            case 1 ->
                jTextArea_Varios.setText("");
            case 2 ->
                jTextArea_DDL.setText("");
            default -> {
            }
        }
    }//GEN-LAST:event_clearMouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        Login login = new Login();
        MainView.Desktop.add(login);
        try {
            ctr.Desconectar();
            ctr.getErrores().clear();
            this.dispose();
            JOptionPane.showMessageDialog(null, "Sesión finalizada");
            login.show();
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar desconectar.");
        }
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        // TODO add your handling code here:
        switch (jTabbedPane_Sentencias.getSelectedIndex()) {
            case 0 ->
                ctr.guardarArchivo(jTextArea_SQL.getText());
            case 1 ->
                ctr.guardarArchivo(jTextArea_Varios.getText());
            case 2 ->
                ctr.guardarArchivo(jTextArea_DDL.getText());
            default -> {
            }
        }
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        try {
            ControlDBs.createBackup();
            ctr.guardarArchivo(ctr.getCodigos());
            JOptionPane.showMessageDialog(null, "Backup guardado correctamente");
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar guardar el Backup");
        }
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        String datos = "";

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "SQL FILES", "sql");
        JFileChooser c = new JFileChooser(
                new File(".")
        );
        c.setFileFilter(filter);
        int i = c.showOpenDialog(null);
        if (i == JFileChooser.APPROVE_OPTION) {
            FileInputStream f = null;
            try {
                f = new FileInputStream(c.getSelectedFile());
                StringBuilder g = new StringBuilder();
                try {
                    InputStreamReader r = new InputStreamReader(f);
                    BufferedReader re = new BufferedReader(r);
                    String leido;

                    while ((leido = re.readLine()) != null) {
                        g.append(leido).append("\n");
                        //g.append(" \n ");
                    }
                    jTabbedPane_Sentencias.setSelectedIndex(2);
                    jTextArea_DDL.setText("");
                    jTextArea_DDL.setText(g.toString());

                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(null, "Error en la lectura de archivo ,Archivo no encontrado");
                    e.printStackTrace();
                } catch (IOException ex) {
                    Logger.getLogger(IDE_DBs.class.getName()).log(Level.SEVERE, null, ex);
                } 

            } catch (FileNotFoundException ex) {
                Logger.getLogger(IDE_DBs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jLabel3MouseClicked

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Recargar;
    private javax.swing.JLabel clear;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel_DDL;
    private javax.swing.JPanel jPanel_SQL;
    private javax.swing.JPanel jPanel_Varios;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane_Sentencias;
    private javax.swing.JTabbedPane jTabbedPane_Tablas;
    private javax.swing.JTable jTableInfo1;
    private javax.swing.JTable jTableInfo2;
    private javax.swing.JTextArea jTextArea_DDL;
    private javax.swing.JTextArea jTextArea_Error;
    private javax.swing.JTextArea jTextArea_SQL;
    private javax.swing.JTextArea jTextArea_Varios;
    private javax.swing.JTree jTree;
    // End of variables declaration//GEN-END:variables
}
