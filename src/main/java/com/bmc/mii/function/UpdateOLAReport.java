/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.mii.function;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Value;
import com.bmc.mii.domain.ConfigurationValue;
import com.bmc.mii.domain.MeasurementOLA;
import com.bmc.mii.domain.SupportGroup;
import com.bmc.mii.function.RemedyAPI;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author MukhlisAj
 */
public class UpdateOLAReport {

    protected static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("UpdateOLAReport: ");

    public synchronized void sendOLARecord(
            MeasurementOLA mola, Entry reportEntry,
            ARServerUser remedyServer, SupportGroup sg, Integer SVTCount, 
            ConfigurationValue configValue) throws ARException {

        //Get configuration value from DCconfig.properties
//        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
//        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        logger.info("------------------ Sending OLA Record to Report Table ------------------");

        try {
            switch (SVTCount) {
                case 1:
                    reportEntry.put(536871012, new Value(mola.getMeasurementStatus()));
                    reportEntry.put(536870953, new Value(mola.getGoalschedTime()));
                    reportEntry.put(536870952, new Value(mola.getOverallElapsedTime()));
                    reportEntry.put(536870985, new Value(mola.getOverallStartTime()));
                    reportEntry.put(536870986, new Value(mola.getOverallStopTime()));
                    reportEntry.put(536870954, new Value(mola.getRecordAssigment()));
                    reportEntry.put(536870969, new Value(mola.getSVTDueDate()));
                    reportEntry.put(536871016, new Value(sg.getSupportGroupName()));
                    reportEntry.put(536871015, new Value(sg.getSupportGroupOrg()));
                    if(mola.getOverallStopTime().isEmpty()){
                    reportEntry.put(536870958, new Value(mola.getEstimateDuration()));
                    }else{
                        reportEntry.put(536870958, new Value(mola.getOverallElapsedTime()));
                    }
                    logger.info("OLA 1 Assigned");
                    break;
                case 2:
                    reportEntry.put(536870996, new Value(mola.getMeasurementStatus()));
                    reportEntry.put(536870961, new Value(mola.getGoalschedTime()));
                    reportEntry.put(536870960, new Value(mola.getOverallElapsedTime()));
                    reportEntry.put(536870987, new Value(mola.getOverallStartTime()));
                    reportEntry.put(536870990, new Value(mola.getOverallStopTime()));
                    reportEntry.put(536870955, new Value(mola.getRecordAssigment()));
                    reportEntry.put(536870970, new Value(mola.getSVTDueDate()));
                    reportEntry.put(536870962, new Value(mola.getEstimateDuration()));
                    reportEntry.put(536871004, new Value(sg.getSupportGroupName()));
                    reportEntry.put(536871002, new Value(sg.getSupportGroupOrg()));
                    if(mola.getOverallStopTime().isEmpty()){
                    reportEntry.put(536870962, new Value(mola.getEstimateDuration()));
                    }else{
                        reportEntry.put(536870962, new Value(mola.getOverallElapsedTime()));
                    }
                    logger.info("OLA 2 Assigned");
                    break;
                case 3:
                    reportEntry.put(536870937, new Value(mola.getMeasurementStatus()));
                    reportEntry.put(536870964, new Value(mola.getGoalschedTime()));
                    reportEntry.put(536870963, new Value(mola.getOverallElapsedTime()));
                    reportEntry.put(536870988, new Value(mola.getOverallStartTime()));
                    reportEntry.put(536870991, new Value(mola.getOverallStopTime()));
                    reportEntry.put(536870956, new Value(mola.getRecordAssigment()));
                    reportEntry.put(536870971, new Value(mola.getSVTDueDate()));
                    reportEntry.put(536870950, new Value(sg.getSupportGroupName()));
                    reportEntry.put(536870941, new Value(sg.getSupportGroupOrg()));
                    if(mola.getOverallStopTime().isEmpty()){
                    reportEntry.put(536870965, new Value(mola.getEstimateDuration()));
                    }else{
                        reportEntry.put(536870965, new Value(mola.getOverallElapsedTime()));
                    }
                    logger.info("OLA 3 Assigned");
                    break;
                case 4:
                    reportEntry.put(536870944, new Value(mola.getMeasurementStatus()));
                    reportEntry.put(536870967, new Value(mola.getGoalschedTime()));
                    reportEntry.put(536870966, new Value(mola.getOverallElapsedTime()));
                    reportEntry.put(536870989, new Value(mola.getOverallStartTime()));
                    reportEntry.put(536870992, new Value(mola.getOverallStopTime()));
                    reportEntry.put(536870957, new Value(mola.getRecordAssigment()));
                    reportEntry.put(536870972, new Value(mola.getSVTDueDate()));
                    reportEntry.put(536870949, new Value(sg.getSupportGroupName()));
                    reportEntry.put(536870948, new Value(sg.getSupportGroupOrg()));
                    if(mola.getOverallStopTime().isEmpty()){
                    reportEntry.put(536870968, new Value(mola.getEstimateDuration()));
                    }else{
                        reportEntry.put(536870968, new Value(mola.getOverallElapsedTime()));
                    }
                    logger.info("OLA 4 Assigned");
                    break;
            }
           
            remedyServer.setEntry(configValue.getRemedyMiddleFormMDO(), reportEntry.getEntryId(), reportEntry, null, 0);
            logger.info("OLA Updated!!");
        } catch (Exception e) {
            logger.info("Error Updating OLA : " + e);
            logger.info("OLA NOT Updated!!!");
        }

    }

}
