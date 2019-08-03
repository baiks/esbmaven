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

    /**
     * Connection
     */
    public Connection con = null;
    /**
     * ResultSet
     */
    public ResultSet rst = null;
    /**
     * Statement
     */
    Statement st = null;
    /**
     * InitialContext
     */
    public InitialContext initialContext;

    /**
     * This function invokes Datasource connection.
     *
     * @param DataSourceName
     * @return
     */
    public Connection getDBConnection(String DataSourceName) {
        Connection connect = null;
        try {
            // Create global DB connections and you are sorted
            initialContext = new InitialContext();
            DataSource ds;
            ds = (DataSource) initialContext.lookup(DataSourceName);
            connect = ds.getConnection();
        } catch (NamingException | SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("getDBConnection", sw.toString());
        }
        return connect;
    }

    /**
     * This function executes a Select statemet that returns a single column as
     * parsed.
     *
     * @param sql
     * @param rslt
     * @param index
     * @return
     */
    public String ExecuteQueryStringValue(String sql, String rslt, String index) {
        con = getDBConnection(ConfigSession.ESB_DATA_SOURCE);
        try {
            if (!con.isClosed()) {
                // Test your query
                st = con.createStatement(); // instaniate an object that is used to execute sql statements
                rst = st.executeQuery(sql);
                while (rst.next()) {
                    rslt = rst.getString(index);
                }
            }
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ExecuteQueryStringValue", sw.toString());
        } finally {
            ReleaseConnections();
        }
        return rslt;
    }

    /**
     * This function executes Update/Delete/Insert/Drop/Create statement.
     *
     * @param sql
     * @return
     */
    public boolean ExecuteUpdate(String sql) {
        con = getDBConnection(ConfigSession.ESB_DATA_SOURCE);
        boolean result = false;
        try {
            if (!con.isClosed()) {
                st = con.createStatement(); // instaniate an object that is used to eecute sql statements
                if (st.executeUpdate(sql) >= 1) {
                    result = true;
                }
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ExecuteUpdate", sw.toString());
            result = false;
        } finally {
            ReleaseConnections();
        }
        return result;
    }

    /**
     * This function executes a select statemet that returns all columns.
     *
     * @param connection
     * @param sql
     * @return
     */
    public ResultSet ExecuteQueryReturnString(Connection connection, String sql) {
        try {
            if (!connection.isClosed()) {
                // Test your query
                st = connection.createStatement(); // instaniate an object that is used to eecute sql statements
                rst = st.executeQuery(sql);

            }
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ExecuteQueryReturnString", sw.toString());
        }
        return rst;
    }

    /**
     * This function executes a select statement depending on the params parsed
     *
     * @param table
     * @param mycolumns
     * @param mycondition
     * @param val
     * @param myspecialcondition
     * @return
     */
    public static String ExcuteSelect(String table, HashMap<String, String> mycolumns, HashMap<String, String> mycondition, HashMap<String, String> val, String myspecialcondition) {
        String query;
        try {
            String fields = SelectColumns(mycolumns);
            String wherecondition = condition(mycondition, val);
            query = "SELECT " + fields + " FROM " + table + " " + wherecondition + " " + myspecialcondition;
            return query;
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("DeleteString", sw.toString());
            return null;
        }
    }

    /**
     * This function iterates through all columns parsed in ExcuteSelect()
     * above.
     *
     * @param cols
     * @return
     */

    private static String SelectColumns(HashMap<String, String> cols) {
        // get columns to return pass * for all columns
        String fields = "";
        int columnsize = cols.size();
        if (columnsize > 0) {
            int i = 1;
            for (i = 1; i <= columnsize; i++) {
                if (i == 1) {

                    fields = fields + cols.get(Integer.toString(i));
                } else {
                    fields = fields + "," + cols.get(Integer.toString(i));
                }
            }
        }
        return fields;
    }

    /**
     * This function iterates through the condition of the select statement and
     * values parsed in the function ExcuteSelect()
     *
     * @param mycondition
     * @param myval
     * @return
     */
    private static String condition(HashMap<String, String> mycondition, HashMap<String, String> myval) {
        String wherecondition = "";
        int conditionsize = mycondition.size();
        int valsize = myval.size();
        if (conditionsize > 0 && valsize > 0 && conditionsize == valsize) {
            int i = 1;
            for (i = 1; i <= conditionsize; i++) {
                if (i == 1) {

                    wherecondition = wherecondition + mycondition.get(Integer.toString(i)) + "='"
                            + myval.get(Integer.toString(i)) + "'";
                } else {
                    wherecondition = wherecondition + " AND " + mycondition.get(Integer.toString(i)) + "='"
                            + myval.get(Integer.toString(i)) + "'";
                }
            }
            wherecondition = " WHERE " + wherecondition;
        }
        return wherecondition;
    }

    /**
     * This function execute an SP that returns no values just executes does an
     * update , insert or delete.
     *
     * @param sql
     * @param params
     * @param paramvalues
     * @return
     */
    public boolean ExecuteUpdateSP(String sql, ArrayList<String> params, ArrayList<String> paramvalues) {
        con = getDBConnection(ConfigSession.ESB_DATA_SOURCE);
        boolean result = true;
        CallableStatement collablestmt = null;
        String query;
        String spparams = "?";
        try {
            int paramsize = params.size();

            if (!con.isClosed()) {
                if (params.iterator().hasNext()) { // SP with param values
                    for (int i = 0; i < paramsize; i++) {
                        if (i > 0) {
                            spparams = spparams + ",?";
                        }
                    }
                    query = "{call " + sql + "(" + spparams + ")}";
                    collablestmt = con.prepareCall(query); // CALL SP

                    // data setting
                    for (int i = 0; i < paramsize; i++) {
                        collablestmt.setString(params.get(i), paramvalues.get(i));
                    }

                } else { //// SP without param values
                    query = "{call " + sql + "}";
                    collablestmt = con.prepareCall(query); // CALL SP
                }
                if (collablestmt.execute()) { // execute sp
                    result = true;
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ExecuteUpdateSP", sw.toString());
            result = false;
        } finally {
            ReleaseConnections();
        }
        return result;
    }

    /**
     * This function executes a select statement and return all columns.
     *
     * @param mycon
     * @param mysql
     * @param myparams
     * @param myparamvalues
     * @return
     */
    public static ResultSet ExecuteSPReturnString(Connection mycon, String mysql, ArrayList<String> myparams, ArrayList<String> myparamvalues) {
        ResultSet returnValue = null;
        CallableStatement collablestmt;
        String query;
        String spparams = "?";
        try {
            if (!mycon.isClosed()) {
                if (myparams.iterator().hasNext()) { // SP with param values
                    int paramsize = myparams.size();
                    for (int i = 0; i < paramsize; i++) {
                        if (i > 0) {
                            spparams = spparams + ",?";
                        }
                    }
                    query = "{call " + mysql + "(" + spparams + ")}";
                    collablestmt = mycon.prepareCall(query); // CALL SP

                    // data setting
                    for (int i = 0; i < paramsize; i++) {
                        collablestmt.setString(myparams.get(i), myparamvalues.get(i));
                    }

                } else { //// SP without param values
                    query = "{call " + mysql + "}";
                    collablestmt = mycon.prepareCall(query); // CALL SP
                }
                if (collablestmt.getUpdateCount() == -1) { // && collablestmt.getMoreResults() == true) {
                    collablestmt.execute();
                    returnValue = collablestmt.getResultSet();
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ExecuteSPReturnString", sw.toString());
        }
        return returnValue;
    }

    /**
     * This function executes a select statement and return columns as json
     * object.
     *
     * @param sql
     * @return
     */
    public String ExecuteQueryReturnJson(String sql) {
        con = getDBConnection(ConfigSession.ESB_DATA_SOURCE);
        JSONObject jsonobject = new JSONObject();
        String jsonArray = null;
        ResultSetMetaData resultSetMetaData;
        try {
            if (!con.isClosed()) {
                // Test your query
                st = con.createStatement(); // instaniate an object that is used to eecute sql statements
                rst = st.executeQuery(sql);
                resultSetMetaData = rst.getMetaData();
                int resultColumns = resultSetMetaData.getColumnCount();
                int j = 0;
                while (rst.next()) {
                    for (int i = 1; i < (resultColumns + 1); i++) { // Number of columns plus 1 coz resultset starts
                        // from index one onwads
                        jsonobject.put(resultSetMetaData.getColumnName(i), rst.getString(i));
                    }
                    if (j == 0) { // if first loop in the while add opening array brace
                        jsonArray = "[" + jsonobject.toString() + ",";
                    } else {
                        jsonArray = jsonArray + jsonobject.toString() + ",";
                    }
                    jsonobject.clear(); // clear after adding to json array
                    j++;
                }
                if (!ClassImportantValues.empty(jsonArray)) {
                    // once done with while loop , remove the last comma and add the closing
                    // jsonarray brace
                    jsonArray = jsonArray.substring(0, jsonArray.lastIndexOf(",")) + "]";
                }
                ReleaseConnections();
            }
        } catch (SQLException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ExecuteQueryReturnJson", sw.toString());
            jsonArray = "An error occurred!";
        }
        return jsonArray;
    }

    /**
     * This function releases SLQ resources
     */
    public void ReleaseConnections() {
        try {
            if (rst != null) {
                rst.close();
            }
            if (st != null) {
                st.close();
            }
            con.close();
            initialContext.close();
        } catch (SQLException | NamingException ex) {
            ex.printStackTrace();
        }
    }
}
