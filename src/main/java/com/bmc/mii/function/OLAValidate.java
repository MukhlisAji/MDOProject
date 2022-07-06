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
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author MukhlisAj
 */
public class OLAValidate {

    protected static Logger logger = Logger.getLogger("OLA Validation : ");

    public static void Validation(List<String> oLAValidation, List<String> reportOLAValidation, Entry reportEntry, ARServerUser serverUser, ConfigurationValue configValue) throws ARException {

        logger.info("------------------ Validating OLA Record ------------------");

        ArrayList<Integer> fieldList = new ArrayList<>();
        fieldList.add(536871003);
        fieldList.add(536871009);
        fieldList.add(536871018);
        fieldList.add(536871039);

        for (Integer i = 0; i < oLAValidation.size(); i++) {
            if (reportOLAValidation.contains(oLAValidation.get(i))) {
                logger.info(oLAValidation.get(i) + " is Valid");
                reportEntry.put(fieldList.get(i), new Value("True"));
            } else {
                logger.info(oLAValidation.get(i) + " is Invalid");
                reportEntry.put(fieldList.get(i), new Value("False"));
            }

        }
        serverUser.setEntry(configValue.getRemedyMiddleFormMDO(), reportEntry.getEntryId(), reportEntry, null, 0);
        logger.info("Validation Finished!");
    }
    
    public static void duration(){
        
    }
}
