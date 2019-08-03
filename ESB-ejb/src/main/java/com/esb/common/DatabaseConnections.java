package com.esb.common;

import com.esb.Main.ConfigSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.json.simple.JSONObject;

/**
 *
 * @author adm
 */
public class DatabaseConnections {

    public Connection con = null;
    public ResultSet rst = null;
    Statement st = null;
    public InitialContext initialContext;

    public Connection getDBConnection(String DataSourceName) {
        try {
            //Create global DB connections and you are sorted
            initialContext = new InitialContext();
            DataSource dataSource;
            dataSource = (DataSource) initialContext.lookup(DataSourceName);
            con = dataSource.getConnection();
        } catch (NamingException | SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("getDBConnection", sw.toString());
        }
        return con;
    }

    //1. Select statemet that returns  mutiple columns using imported DataSources 
    /**
     *
     * @param con
     * @param sql
     * @param rslt
     * @param index
     * @return
     */
    public ArrayList ExecuteQueryStringValue(Connection con, String sql, String rslt, ArrayList index) {
        ArrayList value = new ArrayList();
        try {
            if (!con.isClosed()) {
                //Test your query      
                st = con.createStatement();  //instaniate an object that is used to eecute sql statements
                rst = st.executeQuery(sql);
                while (rst.next()) {
                    for (int i = 0; i < index.size(); i++) {
                        rslt = rst.getString(index.get(i).toString());
                        value.add(i, rslt);
                    }
                }
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteQueryStringValue", sw.toString());
        } finally {
            try {
                if (con != null) {
                    con.close();
                    rst.close();
                    initialContext.close();
                }
            } catch (SQLException | NamingException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("ExecuteQueryStringValue", sw.toString());
            }
        }
        return value;
    }
    //End Select statement return   mutiple columns using imported Datasource  

    //2. Select statemet that returns a single column  using imported DataSource   
    /**
     *
     * @param con
     * @param sql
     * @param rslt
     * @param index
     * @return
     */
    public String ExecuteQueryStringValue(Connection con, String sql, String rslt, String index) {
        try {
            if (!con.isClosed()) {
                //Test your query      
                st = con.createStatement();  //instaniate an object that is used to eecute sql statements
                rst = st.executeQuery(sql);
                while (rst.next()) {
                    rslt = rst.getString(index);
                }
            }
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteQueryStringValue", sw.toString());
        } finally {
            try {
                if (con != null) {
                    rst.close();
                    con.close();
                    initialContext.close();
                }
            } catch (SQLException | NamingException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("ExecuteQueryStringValue", sw.toString());
            }
        }
        return rslt;
    }
    //End Select statement return single column using imported DataSource

    //3. Execute Update ---Update/Delete/Insert/Drop/Create statement using imported DataSource
    /**
     *
     * @param con
     * @param sql
     * @return
     */
    public boolean ExecuteUpdate(Connection con, String sql) {
        boolean result = false;
        try {
            if (!con.isClosed()) {
                st = con.createStatement();  //instaniate an object that is used to eecute sql statements
                if (st.executeUpdate(sql) >= 1) {
                    result = true;
                }
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteUpdate", sw.toString());
            result = false;
        } finally {
            try {
                if (con != null) {
                    st.close();
                    con.close();
                    initialContext.close();
                }
            } catch (SQLException | NamingException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("ExecuteUpdate", sw.toString());
            }
        }
        return result;
    }
    //End execute update using imported DataSource

//4. Select statemet that returns all columns of the query using imported DataSource
    /**
     *
     * @param con
     * @param sql
     * @return
     */
    public ResultSet ExecuteQueryReturnString(Connection con, String sql) {
        try {
            if (!con.isClosed()) {
                //Test your query      
                st = con.createStatement();  //instaniate an object that is used to eecute sql statements
                rst = st.executeQuery(sql);

            }
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteQueryReturnString", sw.toString());
        }
        return rst;
    }
//End of Select statement that return all columns of a string using imported DataSource

//6.start Update string     
    /**
     *
     * @param table
     * @param whereval
     * @param whereColumns
     * @param Columnstoupdate
     * @return
     */
    public String Updatestring(String table, ArrayList whereval, ArrayList whereColumns, ArrayList Columnstoupdate) {
        String sql = "";
        try {

            //Value of the all columns in the query(..order them correctly)
            String where = whereval.toString().replace("]", "");
            where = where.replace("[", "");

            //Columns to update      
            String wherecols = whereColumns.toString().replace("]", "");
            wherecols = wherecols.replace("[", "");
            wherecols = wherecols.replace(", ", " = '%s' and ");

            //where clause 
            String Cols = Columnstoupdate.toString().replace("]", "");
            Cols = Cols.replace("[", "");
            Cols = Cols.replace(", ", " = '%s',");

            if (whereval == null || whereval.equals("")) {
                sql = "UPDATE " + table + " SET " + Cols + " = '%s'" + "";
                sql = String.format(sql, where);
            } else {
                sql = "UPDATE " + table + " SET " + Cols + " = '%s' where " + wherecols + "= '%s'";    //Fixed Issue)
                String values[] = where.split(",");
                //  for(int i=0;i<values.length;i++){
                //We assume a maximum of five columns updated and a max of columns on where clause(good staff baiks)
                switch (values.length) {
                    case 1:
                        sql = String.format(sql, values[0].trim());
                        break;
                    case 2:
                        sql = String.format(sql, values[0].trim(), values[1].trim());
                        break;
                    case 3:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim());
                        break;
                    case 4:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim());
                        break;
                    case 5:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim());
                        break;
                    case 6:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim());
                        break;
                    case 7:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim(), values[6].trim());
                        break;
                    case 8:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim());
                        break;
                    case 9:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(), values[8].trim());
                        break;
                    case 10:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(), values[8].trim(), values[9].trim());
                        break;
                    case 11:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(), values[8].trim(), values[9].trim(), values[10].trim());
                        break;
                    case 12:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(), values[8].trim(), values[9].trim(), values[10].trim(), values[11].trim());
                        break;
                    case 13:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(), values[8].trim(), values[9].trim(), values[10].trim(), values[11].trim(), values[12].trim());
                        break;
                    case 14:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(), values[8].trim(), values[9].trim(), values[10].trim(), values[11].trim(), values[12].trim(), values[13].trim());
                        break;
                    case 15:
                        sql = String.format(sql, values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim(), values[4].trim(), values[5].trim(), values[6].trim(), values[7].trim(), values[8].trim(), values[9].trim(), values[10].trim(), values[11].trim(), values[12].trim(), values[13].trim(), values[14].trim());
                        break;
                }
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("Updatestring", sw.toString());
        }

        return sql;
    }
//End of update string using 

// 8. start Insert into database using imported JDBC
    /**
     *
     * @param Server
     * @param pass
     * @param user
     * @param db
     * @param table
     * @param insertvalues
     * @param Columns
     * @param DBtype
     * @return
     */
    // 8. start Insert into database using imported DataSource
    /**
     *
     * @param con
     * @param table
     * @param insertvalues
     * @param Columns
     * @return
     */
    public boolean ExecuteInsert(Connection con, String table, ArrayList insertvalues, ArrayList Columns) {
        String sql = "";
        boolean success = false;
        try {

            //After a struggle I managed the crap
            String Values = insertvalues.toString().replace("]", "'");
            Values = Values.replace(",", "','");
            Values = Values.replace("[", "'");
            Values = Values.replace("' ", "'");
            //System.out.println(Values);

            //Crap done
            //Columns to insert into
            String Cols = Columns.toString().replace("]", "");
            Cols = Cols.replace("[", "");
            Cols = Cols.replace(", ", ",");
            //End of columns to insert into

            if (Columns.equals("") || Columns == null) {
                sql = "insert into " + table + " values(" + Values + ")";
                //System.out.println(sql); 
            } else {
                sql = "insert into  " + table + " (" + Cols + ") values(" + Values + ")";
                //System.out.println(sql);
            }
            if (!con.isClosed()) {
                st = con.createStatement();  //instaniate an object that is used to execute sql statements
                if ((st.executeUpdate(sql)) >= 1) {
                    success = true;
                }
            }
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteInsert", sw.toString());
        } finally {
            try {
                if (con != null) {
                    con.close();
                    initialContext.close();
                }
            } catch (SQLException | NamingException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("ExecuteInsert", sw.toString());
            }
        }
        return success;
    }
    //End insert statement using imported DataSource

    //9. DeleteString start
    /**
     *
     * @param Server
     * @param pass
     * @param user
     * @param db
     * @param table
     * @param wherevalues
     * @param Columns
     * @param DBtype
     * @return
     */
    public String DeleteString(String Server, String pass, String user, String db, String table, ArrayList wherevalues, ArrayList Columns, String DBtype) {  //(Cool Done)
        String sql = "";
        try {
            //After a struggle I managed the crap
            String Values = wherevalues.toString().replace("[", "'");
            Values = Values.replaceAll(",", "','");
            Values = Values.replaceAll("]", "'");
            Values = Values.replace("' ", "\"");
            Values = Values.replace("'", "\"");

            String varFormat = "";
            if (Columns.equals("") || Columns == null || Columns.isEmpty()) {          //null and "" does not work thus use isEmpty for arraystring
                sql = "delete from " + table;
            }//This is done
            else {
                //Columns to delete 
                String Cols = Columns.toString().replace("]", "=%s");
                Cols = Cols.replace("[", "");
                Cols = Cols.replace(", ", "=%s and ");

                varFormat = "delete from " + table + " where " + Cols;
                String formated = Values;

                if (formated.contains(",")) {
                    String[] stringsplit = formated.split(",");
                    switch (stringsplit.length) {
                        case 2:
                            sql = String.format(varFormat, stringsplit[0], stringsplit[1]);
                            break;
                        case 3:
                            sql = String.format(varFormat, stringsplit[0], stringsplit[1], stringsplit[2]);
                            break;
                        case 4:
                            sql = String.format(varFormat, stringsplit[0], stringsplit[1], stringsplit[2], stringsplit[3]);
                            break;
                        case 5:
                            sql = String.format(varFormat, stringsplit[0], stringsplit[1], stringsplit[2], stringsplit[3], stringsplit[4]);
                            break;
                        case 6:
                            sql = String.format(varFormat, stringsplit[0], stringsplit[1], stringsplit[2], stringsplit[3], stringsplit[4], stringsplit[5]);
                            break;
                        case 7:
                            sql = String.format(varFormat, stringsplit[0], stringsplit[1], stringsplit[2], stringsplit[3], stringsplit[4], stringsplit[5], stringsplit[6]);
                            break;
                        case 8:
                            sql = String.format(varFormat, stringsplit[0], stringsplit[1], stringsplit[2], stringsplit[3], stringsplit[4], stringsplit[5], stringsplit[6], stringsplit[7]);
                            break;
                        case 9:
                            sql = String.format(varFormat, stringsplit[0], stringsplit[1], stringsplit[2], stringsplit[3], stringsplit[4], stringsplit[5], stringsplit[6], stringsplit[7], stringsplit[8]);
                            break;
                        case 10:
                            sql = String.format(varFormat, stringsplit[0], stringsplit[1], stringsplit[2], stringsplit[3], stringsplit[4], stringsplit[5], stringsplit[6], stringsplit[7], stringsplit[8], stringsplit[9]);
                            break;
                    }
                } else {
                    String[] stringsplit = formated.split(",");
                    sql = String.format(varFormat, stringsplit[0]);
                }
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("DeleteString", sw.toString());
        }
        sql = sql.replace("\"", "'");                                              //Remove "" enclosing string at where caluse eg. where name="paul" to where name='paul'
        return sql;
    }
    //End deletestring

    //Start select statement
    /**
     *
     * @param table
     * @param columns
     * @param condition
     * @param val
     * @param specialcondition
     * @return
     */
    public String ExcuteSelect(String table, HashMap columns, HashMap condition, HashMap val, String specialcondition) {
        String query = null;
        try {
            //get columns to return pass * for all columns
            String fields = "";
            if (columns.size() > 0) {
                for (int i = 1; i <= columns.size(); i++) {
                    if (i == 1) {

                        fields = fields + columns.get(Integer.toString(i)).toString();
                    } else {
                        fields = fields + "," + columns.get(Integer.toString(i)).toString();
                    }
                }
            }

            //Where condition otherwise pass specialcondition eg..is null as the special condition
            String wherecondition = "";
            if (condition.size() > 0 && val.size() > 0 && condition.size() == val.size()) {
                for (int i = 1; i <= condition.size(); i++) {
                    if (i == 1) {

                        wherecondition = wherecondition + condition.get(Integer.toString(i)).toString() + "='" + val.get(Integer.toString(i)).toString() + "'";
                    } else {
                        wherecondition = wherecondition + " AND " + condition.get(Integer.toString(i)).toString() + "='" + val.get(Integer.toString(i)).toString() + "'";
                    }
                }
                wherecondition = " WHERE " + wherecondition;
            }

            query = "SELECT " + fields + " FROM " + table + " " + wherecondition + " " + specialcondition;
            return query;
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("DeleteString", sw.toString());
            return null;
        }
    }
    //End select statement   

    //Start SP returns no values just executes does an update or insert using imported DataSource
    /**
     *
     * @param con
     * @param sql
     * @param params
     * @param paramvalues
     * @return
     */
    public boolean ExecuteUpdateSP(Connection con, String sql, ArrayList params, ArrayList paramvalues) {

        boolean result = true;
        CallableStatement collablestmt = null;
        String query = "";
        String spparams = "?";
        try {

            if (!con.isClosed()) {
                if (params.iterator().hasNext()) {    //SP with param values
                    for (int i = 0; i < params.size(); i++) {
                        if (i > 0) {
                            spparams = spparams + ",?";
                        }
                    }
                    query = "{call " + sql + "(" + spparams + ")}";
                    collablestmt = con.prepareCall(query); //CALL SP

                    //data setting
                    for (int i = 0; i < params.size(); i++) {
                        collablestmt.setString(params.get(i).toString(), paramvalues.get(i).toString());
                    }

                } else { ////SP without param values
                    query = "{call " + sql + "}";
                    collablestmt = con.prepareCall(query); //CALL SP
                }
                if (collablestmt.execute()) {      //execute sp
                    result = true;
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteUpdateSP", sw.toString());
            result = false;
        } finally {
            try {
                if (con != null) {
                    collablestmt.close();
                    con.close();
                    initialContext.close();
                }
            } catch (SQLException | NamingException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("ExecuteUpdateSP", sw.toString());
            }
        }
        return result;
    }
    //End SP returns no values just executes does an update or insert using imported DataSource

    //Start Execute SP that returns a single value using imported DataSource
    /**
     *
     * @param con
     * @param sql
     * @param params
     * @param paramvalues
     * @return
     */
    public String ExecuteSPReturnValue(Connection con, String sql, ArrayList params, ArrayList paramvalues) {
        String returnValue = null;
        CallableStatement collablestmt = null;
        String query = "";
        String spparams = "?";
        int i = 0;
        try {

            if (!con.isClosed()) {
                if (params.iterator().hasNext()) {    //SP with param values
                    for (i = 0; i < params.size(); i++) {
                        if (i > 0) {
                            spparams = spparams + ",?";
                        }
                    }
                    query = "{call " + sql + "(" + spparams + ")}";
                    collablestmt = con.prepareCall(query); //CALL SP

                    //data setting
                    for (i = 0; i < params.size(); i++) {
                        collablestmt.setString(params.get(i).toString(), paramvalues.get(i).toString());
                    }

                } else { ////SP without param values
                    query = "{call " + sql + "}";
                    collablestmt = con.prepareCall(query); //CALL SP
                }

                collablestmt.registerOutParameter(i + 1, java.sql.Types.VARCHAR);
                collablestmt.execute();
                returnValue = collablestmt.getString(i + 1);
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteSPReturnValue", sw.toString());
        } finally {
            try {
                if (con != null) {
                    con.close();
                    initialContext.close();
                }
            } catch (SQLException | NamingException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("ExecuteSPReturnValue", sw.toString());

            }
        }
        return returnValue;
    }
    //End Execute SP that returns a single value if the sp has return value eg..return 1 using imported DataSource

    //Start Execute FUNC that returns a single value using imported DataSource
    /**
     *
     * @param con
     * @param sql
     * @param params
     * @param paramvalues
     * @return
     */
    public String ExecuteFUNCReturnValue(Connection con, String sql, ArrayList params, ArrayList paramvalues) {
        String returnValue = null;
        CallableStatement collablestmt = null;
        String query = "";
        String spparams = "?";
        try {
            if (!con.isClosed()) {
                if (params.iterator().hasNext()) {    //SP with param values
                    for (int i = 0; i < params.size(); i++) {
                        if (i > 0) {
                            spparams = spparams + ",?";
                        }
                    }
                    //?= call myfunction(?,?)
                    query = "{?= call " + sql + "(" + spparams + ")}";
                    collablestmt = con.prepareCall(query); //CALL SP

                    //data setting
                    int j = 0;
                    for (int i = 0; i < params.size(); i++) {
                        // String paramname = params.get(i).toString();
                        String paramvalue = paramvalues.get(i).toString();
                        if (i == 0) {
                            j = i + 2;
                            collablestmt.setString(i + 2, paramvalue);
                        } else {
                            int value = j + 1;
                            j = j + 1;
                            collablestmt.setString(value, paramvalue);
                        }
                    }

                } else { ////SP without param values
                    query = "{?= call " + sql + "()}";
                    collablestmt = con.prepareCall(query); //CALL SP
                }

                collablestmt.registerOutParameter(1, java.sql.Types.VARCHAR);
                collablestmt.execute();
                returnValue = collablestmt.getString(1);
            }
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteFUNCReturnValue", sw.toString());
        } finally {
            try {
                if (con != null) {
                    con.close();
                    initialContext.close();
                }
            } catch (SQLException | NamingException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("ExecuteFUNCReturnValue", sw.toString());
            }
        }
        return returnValue;
    }

    //End Execute FUNC that returns a single value if the sp has return value eg..return 1 using imported DataSource
    //Start Execute SP that returns a Resultset(Select statement) value (14/05/2015) using imported DataSource
    /**
     *
     * @param con
     * @param sql
     * @param params
     * @param paramvalues
     * @return
     */
    public ResultSet ExecuteSPReturnString(Connection con, String sql, ArrayList params, ArrayList paramvalues) {
        ResultSet returnValue = null;
        CallableStatement collablestmt = null;
        String query = "";
        String spparams = "?";
        try {
            if (!con.isClosed()) {
                if (params.iterator().hasNext()) {    //SP with param values
                    for (int i = 0; i < params.size(); i++) {
                        if (i > 0) {
                            spparams = spparams + ",?";
                        }
                    }
                    query = "{call " + sql + "(" + spparams + ")}";
                    collablestmt = con.prepareCall(query); //CALL SP

                    //data setting
                    for (int i = 0; i < params.size(); i++) {
                        collablestmt.setString(params.get(i).toString(), paramvalues.get(i).toString());
                    }

                } else { ////SP without param values
                    query = "{call " + sql + "}";
                    collablestmt = con.prepareCall(query); //CALL SP
                }
                if (collablestmt.getUpdateCount() == -1) { //&& collablestmt.getMoreResults() == true) {
                    collablestmt.execute();
                    returnValue = collablestmt.getResultSet();
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteSPReturnString", sw.toString());
        }
        return returnValue;
    }

    //End Execute SP that returns a Resultset(Select statement) value (14/05/2015) using imported DataSource
    //Start function execute in progress
//    public void ExecuteFunction(){
//    String call = "{ ? = call FCRLIVE.AP_CH_GET_ACCT_BALANCES(?, ?, ?, ?, ?) }";
//                      CallableStatement cstmt = conn.prepareCall(call);
//                      cstmt.setQueryTimeout(1800);
//                      cstmt.setString(1, inputCode);
//                      cstmt.registerOutParameter(2, oracle.jdbc.OracleTypes.NUMBER);
//                      cstmt.registerOutParameter(3, oracle.jdbc.OracleTypes.VARCHAR);
//                      cstmt.registerOutParameter(4, oracle.jdbc.OracleTypes.CHAR);
//                      cstmt.registerOutParameter(5, oracle.jdbc.OracleTypes.CHAR);
//                      cstmt.executeUpdate();
//    }
    //End function excute in progress
    //Start Package returns no values just executes does an update or insert  using imported DataSource
    /**
     *
     * @param con
     * @param sql
     * @param params
     * @param paramvalues
     * @return
     */
    public boolean ExecuteUpadatePackage(Connection con, String sql, ArrayList params, ArrayList paramvalues) {
        boolean result = true;
        CallableStatement collablestmt = null;
        String query = "";
        String spparams = "?";
        try {
            if (!con.isClosed()) {
                if (params.iterator().hasNext()) {    //SP with param values
                    for (int i = 0; i < params.size(); i++) {
                        if (i > 0) {
                            spparams = spparams + ",?";
                        }
                    }
                    query = "{call " + sql + "(" + spparams + ")}";
                    //query = "{?=begin TEST.SP_TEST(); end;}";
                    collablestmt = con.prepareCall(query); //CALL SP

                    //data setting
                    for (int i = 0; i < params.size(); i++) {
                        collablestmt.setString(params.get(i).toString(), paramvalues.get(i).toString());
                    }

                } else { ////SP without param values
                    query = "{call " + sql + "()}";
                    //query = "{call " + sql + "}";
                    //query = "{?=begin TEST.SP_TEST(); end;}";
                    collablestmt = con.prepareCall(query); //CALL SP
                }
                if (collablestmt.execute()) {      //execute sp
                    result = true;
                }
            }
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteUpadatePackage", sw.toString());
            result = false;
        } finally {
            try {
                if (con != null) {
                    con.close();
                    initialContext.close();
                }
            } catch (SQLException | NamingException ex) {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                ConfigSession.cl.LogErrors("ExecuteUpadatePackage", sw.toString());
            }
        }
        return result;
    }

    //End Package returns no values just executes does an update or insert using imported DataSouirce
    //7.Start Select statemet that returns all columns as json array using imported DataSource      
    /**
     *
     * @param con
     * @param sql
     * @return
     */
    public String ExecuteQueryReturnJson(Connection con, String sql) {
        JSONObject jsonobject = new JSONObject();
        String jsonArray = null;
        ResultSetMetaData resultSetMetaData = null;
        try {
            if (!con.isClosed()) {
                //Test your query      
                st = con.createStatement();  //instaniate an object that is used to eecute sql statements
                rst = st.executeQuery(sql);
                resultSetMetaData = rst.getMetaData();
                int resultColumns = resultSetMetaData.getColumnCount();
                int j = 0;
                while (rst.next()) {
                    for (int i = 1; i < (resultColumns + 1); i++) { //Number of columns plus 1 coz resultset starts from index one onwads
                        jsonobject.put(resultSetMetaData.getColumnName(i), rst.getString(i));
                    }
                    if (j == 0) { //if first loop in the while add opening array brace
                        jsonArray = "[" + jsonobject.toString() + ",";
                    } else {
                        jsonArray = jsonArray + jsonobject.toString() + ",";
                    }
                    jsonobject.clear();  //clear after adding to json array
                    j++;
                }
                if (!ConfigSession.cl.empty(jsonArray)) {
                    //once done with while loop , remove the last comma and add the closing jsonarray brace
                    jsonArray = jsonArray.substring(0, jsonArray.lastIndexOf(",")) + "]";
                }
                st.close();
                rst.close();
                con.close();
                initialContext.close();
            }
        } catch (SQLException | NamingException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ConfigSession.cl.LogErrors("ExecuteQueryReturnJson", sw.toString());
            jsonArray = "An error occurred!";
        }
        return jsonArray;
    }
//End Select statemet that returns all columns as json object using imported DataSource     
}
