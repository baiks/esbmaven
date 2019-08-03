package com.esb.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.rolling.FixedWindowRollingPolicy;
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.SizeBasedTriggeringPolicy;

public class Logs4jLogger {

    public void LogEngine(String messsage, String directory, String fileName) {
        RollingFileAppender rollingFileAppender = new RollingFileAppender();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String daylog = format.format(new Date());
            messsage = daylog + "::::" + messsage;
            String path = createDailyDirectory(directory);
            Logger logger = Logger.getLogger(directory);
            Layout ly = new PatternLayout();
            rollingFileAppender.setLayout(ly);
            rollingFileAppender.setFile(path + fileName + ".log");
            rollingFileAppender.setLayout(ly);
            rollingFileAppender.setFile(path + fileName + ".log");
            FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
            fixedWindowRollingPolicy.setFileNamePattern(path + fileName + "%i.log.html");
            int size = 12;
            fixedWindowRollingPolicy.setMaxIndex(size);
            SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy(10000L);

            rollingFileAppender.setTriggeringPolicy(sizeBasedTriggeringPolicy);
            rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);
            rollingFileAppender.activateOptions();
            logger.addAppender(rollingFileAppender);
            logger.info(messsage);
            logger.removeAllAppenders();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rollingFileAppender != null) {
                rollingFileAppender.close();
            }
        }
    }

    public String createDailyDirectory(String appdirectory) {
        String dailyDirecory = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String daylog = format.format(new Date());

            dailyDirecory = appdirectory + "/" + daylog;
            new File(dailyDirecory).mkdirs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dailyDirecory + "/";
    }
}
