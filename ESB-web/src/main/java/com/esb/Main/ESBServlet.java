/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.Main;

import com.esb.common.ClassImportantValues;
import com.esb.common.ValidateMessageFields;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author adm
 */
@WebServlet(name = "ESBApi", urlPatterns = {"/ESBApi"})
public class ESBServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        JSONObject inrequest = new JSONObject();
        JSONObject outresponse = new JSONObject();

        ServletRequestProcessor processor = new ServletRequestProcessor();
        try {
            String message = null;
            int size = 20000;
            StringBuilder stringBuilder = new StringBuilder(size);
            String line = null;

            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();

            if (!ClassImportantValues.empty(stringBuilder.toString())) { //Check if empty
                message = new String(Base64.decodeBase64(stringBuilder.toString()), "UTF-8");
                ClassImportantValues.LogErrors("request", message);
                inrequest = new JSONObject(message);

                //1. Validate Message Fields
                ValidateMessageFields validate = new ValidateMessageFields();
                JSONObject validatetxn = new JSONObject();
                validatetxn = validate.ValidateMessage(inrequest);
                if (validatetxn.get("field99").equals("00")) {

                    //Determine it its card or not card transaction
                    inrequest = ServletRequestProcessor.CardTransaction(inrequest);

                    //2. Validate channel
                    String[] channels = ConfigSession.CHANNELS.split(",");
                    String channel = inrequest.get("field32").toString();
                    boolean contains = Stream.of(channels).anyMatch(x -> x.equals(channel));
                    if (contains) {
                        outresponse = processor.processRequest(inrequest);
                    } else {
                        outresponse = inrequest;
                        outresponse.put("field39", "57");
                        outresponse.put("field48", "Invalid channel or channel not confgured");
                    }
                } else {
                    outresponse.put("field39", validatetxn.get("field99"));
                    outresponse.put("field48", validatetxn.get("field48"));
                }

            } else {
                outresponse.put("field39", "57");
                outresponse.put("field48", "Empty requests not accepted");
            }
        } catch (IOException | JSONException ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            ClassImportantValues.LogErrors("ServletError", sw.toString());
            outresponse.put("field39", "57");
            outresponse.put("field48", "An error occurred!");
        }
        ClassImportantValues.LogErrors("response", outresponse.toString());
        //Write response back
        if (outresponse.has("field127") && outresponse.get("field32").equals("USSD")) {
            outresponse.remove("field127");
        }
        String Response = Base64.encodeBase64String(outresponse.toString().getBytes());
        out.print(Response);

        out.close();

    }

    /**
     * This function handles all the GET requests
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processGETRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<H1><div class=\"content\"><center>ESB API is online</center></div></H1>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processGETRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
