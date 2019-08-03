/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esb.common;

import javax.naming.InitialContext;
import javax.resource.spi.work.WorkManager;

/**
 *
 * @author adm
 */
public class WorkerManager {

    public WorkManager JbossWorkManager() {
        WorkManager wm = null;
        try {
            InitialContext ic = new InitialContext();
            wm = (WorkManager) ic.lookup("java:comp/env/wm/WorkManager");


        } catch (Exception ex) {

        }
        return wm;
    }
}
