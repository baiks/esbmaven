/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import com.esb.common.ClassImportantValues;
import com.esb.common.DatabaseConnections;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;

/**
 *
 * @author adm
 */
public class TransactionsValidations {

    DatabaseConnections connect = new DatabaseConnections();

    public HashMap getCharges(HashMap inrequest) {
        BigDecimal charge = getCharge(inrequest);
        BigDecimal vatrate = new BigDecimal(10);
        BigDecimal VAT = charge.divide(vatrate);

        inrequest.put("field28", charge);
        inrequest.put("field26", VAT);

        return inrequest;
    }

    private BigDecimal getCharge(HashMap inrequest) {
        String TTYPE = "WW";
        if (inrequest.get("field3").equals("400000")) {
            if (inrequest.containsKey("CARDCR")) {
                TTYPE = "WC";
            }
            if (inrequest.containsKey("CARDDR")) {
                TTYPE = "CW";
            }
        }

        String sql = "select * from FN_GET_CHARGES("
                + "'0200',"
                + "'" + inrequest.get("field3") + "',"
                + "'" + inrequest.get("field4") + "',"
                + "'" + inrequest.get("field102") + "',"
                + "'" + inrequest.get("field32") + "',"
                + "'" + TTYPE + "')";
        String result = "";
        if (inrequest.get("field3").equals("400000")) {
            if (inrequest.containsKey("field71")) {
                Connection con = connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
                result = connect.ExecuteQueryStringValue(con, sql, "", "Fee");
            }
        } else {
            Connection con = connect.getDBConnection(ConfigSession.ESB_DATA_SOURCE);
            result = connect.ExecuteQueryStringValue(con, sql, "", "Fee");
        }
        if (ConfigSession.cl.empty(result)) {
            result = "0";
        }
        BigDecimal charge = new BigDecimal(result);
        return ClassImportantValues.round(charge, 2, true);
    }

}
