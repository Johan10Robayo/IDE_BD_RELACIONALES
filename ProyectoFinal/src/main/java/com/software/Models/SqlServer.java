/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.software.Models;

import com.software.DTO.DTOUser;
import static com.software.Models.Consultas.getInfo;

import java.util.ArrayList;

/**
 *
 * @author drago
 */
public class SqlServer {

    DTOUser dtouser;
    private String URL;

    static String listarTablas = "SELECT * FROM SYS.tables ORDER BY name";
    static String listarProcedimientos = "SELECT ROUTINE_NAME FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_TYPE = 'PROCEDURE'";
    static String listarFunciones = "SELECT ROUTINE_NAME from [INFORMATION_SCHEMA].[ROUTINES] where routine_type='FUNCTION'";
    static String listarViews = "SELECT TABLE_NAME FROM [INFORMATION_SCHEMA].[VIEWS]";
    static String listarTriggers = "SELECT name FROM sys.triggers";
    static String listarUsuarios = "SELECT name FROM sys.sql_logins";

    public String datosConexion() {
        String datos;
        datos = "jdbc:sqlserver://" + dtouser.getIpServidor() + ":" + dtouser.getPuerto() + ";"
                + "database=" + dtouser.getInstancia().toLowerCase() + ";"
                + "user=" + dtouser.getUSER() + ";"
                + "password=" + dtouser.getPASSWORD() + ";"
                + "loginTimeout=30;";
        System.out.println("DATOS: " + datos);
        return datos;
    }

    public String[] getSentenciasTablas(String tablaSelected) {
        String[] sqls = new String[2];
        sqls[0] = "SELECT column_name, data_type FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME='" + tablaSelected.toUpperCase() + "'";
        sqls[1] = "SELECT * FROM " + tablaSelected + " ORDER BY 1";
        return sqls;
    }

    public String getSentenciaMetodo(String tablaSelected) {
        //vistas y traiggers no funcionan asì
        //return "Select * FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_NAME='" + tablaSelected.toUpperCase() + "'";

        //"SELECT SPECIFIC_NAME, SPECIFIC_SCHEMA,1,ROUTINE_DEFINITION FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_NAME= '" + tablaSelected.toUpperCase() + "'";
        //String tabla_inferior = "";
        //tabla_inferior += tablaSelected;
        //tabla_inferior += " 1 ";
        String tabla_inferior = " sp_helptext " + tablaSelected;

        return tabla_inferior;

    }

    public void setDTO(DTOUser dtouser) {
        this.dtouser = dtouser;
    }

    public static ArrayList<String> getTables() {
        return getInfo(listarTablas);
    }

    public static ArrayList<String> getUsers() {
        return getInfo(listarUsuarios);
    }

    public static ArrayList<String> getProcedures() {
        return getInfo(listarProcedimientos);
    }

    public static ArrayList<String> getFunctions() {
        return getInfo(listarFunciones);
    }

    public static ArrayList<String> getViews() {
        return getInfo(listarViews);
    }

    public static ArrayList<String> getTriggers() {
        return getInfo(listarTriggers);
    }

    public String getDDL(String tablaSelected) {
        return "DECLARE @table_name SYSNAME\n"
                + "SELECT @table_name = 'dbo." + tablaSelected + "'\n"
                + "\n"
                + "DECLARE \n"
                + "      @object_name SYSNAME\n"
                + "    , @object_id INT\n"
                + "\n"
                + "SELECT \n"
                + "      @object_name = '[' + s.name + '].[' + o.name + ']'\n"
                + "    , @object_id = o.[object_id]\n"
                + "FROM sys.objects o WITH (NOWAIT)\n"
                + "JOIN sys.schemas s WITH (NOWAIT) ON o.[schema_id] = s.[schema_id]\n"
                + "WHERE s.name + '.' + o.name = @table_name\n"
                + "    AND o.[type] = 'U'\n"
                + "    AND o.is_ms_shipped = 0\n"
                + "\n"
                + "DECLARE @SQL NVARCHAR(MAX) = ''\n"
                + "\n"
                + ";WITH index_column AS \n"
                + "(\n"
                + "    SELECT \n"
                + "          ic.[object_id]\n"
                + "        , ic.index_id\n"
                + "        , ic.is_descending_key\n"
                + "        , ic.is_included_column\n"
                + "        , c.name\n"
                + "    FROM sys.index_columns ic WITH (NOWAIT)\n"
                + "    JOIN sys.columns c WITH (NOWAIT) ON ic.[object_id] = c.[object_id] AND ic.column_id = c.column_id\n"
                + "    WHERE ic.[object_id] = @object_id\n"
                + "),\n"
                + "fk_columns AS \n"
                + "(\n"
                + "     SELECT \n"
                + "          k.constraint_object_id\n"
                + "        , cname = c.name\n"
                + "        , rcname = rc.name\n"
                + "    FROM sys.foreign_key_columns k WITH (NOWAIT)\n"
                + "    JOIN sys.columns rc WITH (NOWAIT) ON rc.[object_id] = k.referenced_object_id AND rc.column_id = k.referenced_column_id \n"
                + "    JOIN sys.columns c WITH (NOWAIT) ON c.[object_id] = k.parent_object_id AND c.column_id = k.parent_column_id\n"
                + "    WHERE k.parent_object_id = @object_id\n"
                + ")\n"
                + "SELECT @SQL = 'CREATE TABLE ' +@object_name+'('+STUFF(( \n"
                + "   SELECT '            '+ '[' + c.name + '] '+ \n"
                + "        CASE WHEN c.is_computed = 1 \n"
                + "            THEN 'AS ' + cc.[definition] \n"
                + "            ELSE UPPER(tp.name) + \n"
                + "                CASE WHEN tp.name IN ('varchar', 'char', 'varbinary', 'binary', 'text')\n"
                + "                       THEN '(' + CASE WHEN c.max_length = -1 THEN 'MAX' ELSE CAST(c.max_length AS VARCHAR(5)) END + ')'\n"
                + "                     WHEN tp.name IN ('nvarchar', 'nchar', 'ntext')\n"
                + "                       THEN '(' + CASE WHEN c.max_length = -1 THEN 'MAX' ELSE CAST(c.max_length / 2 AS VARCHAR(5)) END + ')'\n"
                + "                     WHEN tp.name IN ('datetime2', 'time2', 'datetimeoffset') \n"
                + "                       THEN '(' + CAST(c.scale AS VARCHAR(5)) + ')'\n"
                + "                     WHEN tp.name = 'decimal' \n"
                + "                       THEN '(' + CAST(c.[precision] AS VARCHAR(5)) + ',' + CAST(c.scale AS VARCHAR(5)) + ')'\n"
                + "                    ELSE ''\n"
                + "                END +\n"
                + "                CASE WHEN c.collation_name IS NOT NULL THEN ' COLLATE ' + c.collation_name ELSE '' END +\n"
                + "                CASE WHEN c.is_nullable = 1 THEN ' NULL' ELSE ' NOT NULL' END + \n"
                + "                CASE WHEN dc.[definition] IS NOT NULL THEN ' DEFAULT' + dc.[definition] ELSE '' END +\n"
                + "                CASE WHEN ic.is_identity = 1 THEN ' IDENTITY(' + CAST(ISNULL(ic.seed_value, '0') AS CHAR(1)) + ',' + CAST(ISNULL(ic.increment_value, '1') AS CHAR(1)) + ')' ELSE '' END \n"
                + "        END +','+ char(10)\n"
                + "    FROM sys.columns c WITH (NOWAIT)\n"
                + "    JOIN sys.types tp WITH (NOWAIT) ON c.user_type_id = tp.user_type_id\n"
                + "    LEFT JOIN sys.computed_columns cc WITH (NOWAIT) ON c.[object_id] = cc.[object_id] AND c.column_id = cc.column_id\n"
                + "    LEFT JOIN sys.default_constraints dc WITH (NOWAIT) ON c.default_object_id != 0 AND c.[object_id] = dc.parent_object_id AND c.column_id = dc.parent_column_id\n"
                + "    LEFT JOIN sys.identity_columns ic WITH (NOWAIT) ON c.is_identity = 1 AND c.[object_id] = ic.[object_id] AND c.column_id = ic.column_id\n"
                + "    WHERE c.[object_id] = @object_id\n"
                + "    ORDER BY c.column_id\n"
                + "    FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, CHAR(9) + ' ')\n"
                + "    + ISNULL((SELECT  +'   '+'CONSTRAINT [' + k.name + '] PRIMARY KEY (' + \n"
                + "                    (SELECT STUFF((\n"
                + "                         SELECT ', [' + c.name + '] ' + CASE WHEN ic.is_descending_key = 1 THEN 'DESC' ELSE 'ASC' END\n"
                + "                         FROM sys.index_columns ic WITH (NOWAIT)\n"
                + "                         JOIN sys.columns c WITH (NOWAIT) ON c.[object_id] = ic.[object_id] AND c.column_id = ic.column_id\n"
                + "                         WHERE ic.is_included_column = 0\n"
                + "                             AND ic.[object_id] = k.parent_object_id \n"
                + "                             AND ic.index_id = k.unique_index_id     \n"
                + "                         FOR XML PATH(N''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, ''))\n"
                + "            + ')' + CHAR(13)\n"
                + "            FROM sys.key_constraints k WITH (NOWAIT)\n"
                + "            WHERE k.parent_object_id = @object_id \n"
                + "                AND k.[type] = 'PK'), '') + ')'  + CHAR(10) +\n"
                + "    + CHAR(13)+ISNULL((SELECT (\n"
                + "        SELECT CHAR(10) +\n"
                + "             CHAR(10)+'ALTER TABLE ' + @object_name + ' WITH' \n"
                + "            + CASE WHEN fk.is_not_trusted = 1 \n"
                + "                THEN ' NOCHECK' \n"
                + "                ELSE ' CHECK' \n"
                + "              END + \n"
                + "              ' ADD CONSTRAINT [' + fk.name  + '] FOREIGN KEY(' \n"
                + "              + STUFF((\n"
                + "                SELECT ', [' + k.cname + ']'\n"
                + "                FROM fk_columns k\n"
                + "                WHERE k.constraint_object_id = fk.[object_id]\n"
                + "                FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '')\n"
                + "               + ')' +\n"
                + "              ' REFERENCES [' + SCHEMA_NAME(ro.[schema_id]) + '].[' + ro.name + '] ('\n"
                + "              + STUFF((\n"
                + "                SELECT ', [' + k.rcname + ']'\n"
                + "                FROM fk_columns k\n"
                + "                WHERE k.constraint_object_id = fk.[object_id]\n"
                + "                FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '')\n"
                + "               + ')'\n"
                + "            + CASE \n"
                + "                WHEN fk.delete_referential_action = 1 THEN ' ON DELETE CASCADE' \n"
                + "                WHEN fk.delete_referential_action = 2 THEN ' ON DELETE SET NULL'\n"
                + "                WHEN fk.delete_referential_action = 3 THEN ' ON DELETE SET DEFAULT' \n"
                + "                ELSE '' \n"
                + "              END\n"
                + "            + CASE \n"
                + "                WHEN fk.update_referential_action = 1 THEN ' ON UPDATE CASCADE'\n"
                + "                WHEN fk.update_referential_action = 2 THEN ' ON UPDATE SET NULL'\n"
                + "                WHEN fk.update_referential_action = 3 THEN ' ON UPDATE SET DEFAULT'  \n"
                + "                ELSE '' \n"
                + "              END \n"
                + "            + CHAR(10) + 'ALTER TABLE ' + @object_name + ' CHECK CONSTRAINT [' + fk.name  + ']' + CHAR(13)\n"
                + "        FROM sys.foreign_keys fk WITH (NOWAIT)\n"
                + "        JOIN sys.objects ro WITH (NOWAIT) ON ro.[object_id] = fk.referenced_object_id\n"
                + "        WHERE fk.parent_object_id = @object_id\n"
                + "        FOR XML PATH(N''), TYPE).value('.', 'NVARCHAR(MAX)')), '')\n"
                + "    + ISNULL(((SELECT\n"
                + "         CHAR(10) + 'CREATE' + CASE WHEN i.is_unique = 1 THEN ' UNIQUE' ELSE '' END \n"
                + "                + ' NONCLUSTERED INDEX [' + i.name + '] ON ' + @object_name + ' (' +\n"
                + "                STUFF((\n"
                + "                SELECT ', [' + c.name + ']' + CASE WHEN c.is_descending_key = 1 THEN ' DESC' ELSE ' ASC' END\n"
                + "                FROM index_column c\n"
                + "                WHERE c.is_included_column = 0\n"
                + "                    AND c.index_id = i.index_id\n"
                + "                FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') + ')'  \n"
                + "                + ISNULL(CHAR(13) + 'INCLUDE (' + \n"
                + "                    STUFF((\n"
                + "                    SELECT ', [' + c.name + ']'\n"
                + "                    FROM index_column c\n"
                + "                    WHERE c.is_included_column = 1\n"
                + "                        AND c.index_id = i.index_id\n"
                + "                    FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') + ')', '')  + CHAR(13)\n"
                + "        FROM sys.indexes i WITH (NOWAIT)\n"
                + "        WHERE i.[object_id] = @object_id\n"
                + "            AND i.is_primary_key = 0\n"
                + "            AND i.[type] = 2\n"
                + "        FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)')\n"
                + "    ), '')\n"
                + "SELECT @SQL";
    }

    //SQL PARA LISTAR USUARIOS
    //SELECT name FROM sys.sql_logins
}
