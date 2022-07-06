/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.mii.controller;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Value;
import com.bmc.mii.domain.ConfigurationValue;
import com.bmc.mii.domain.MeasurementOLA;
import com.bmc.mii.domain.PublicHoliday;
import com.bmc.mii.domain.ReportOLA;
import com.bmc.mii.domain.SupportGroup;
import com.bmc.mii.function.OLAValidate;
import com.bmc.mii.function.RemedyAPI;
import com.bmc.mii.function.UpdateOLAReport;
import com.bmc.mii.function.WorkingHour;
import com.bmc.mii.remedy.RemedyConnection;
import java.io.IOException;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import org.apache.commons.collections4.ListUtils;

/**
 *
 * @author MukhlisAj
 */
@Controller
public class IntegrationController {

    protected static Logger logger = Logger.getLogger("IntegrationController: ");

    @RequestMapping(value = "TestOLA", method = RequestMethod.GET)
    public String tesT() {

        List<EntryListInfo> inputList = new ArrayList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
        int chunkSize = 5;
        List<List<EntryListInfo>> partitionedList = ListUtils.partition(inputList, chunkSize);
        System.out.println(partitionedList);
        partitionedList.get(0).get(1);

        return "result";

    }

    @RequestMapping(value = "UpdateOLA", method = RequestMethod.GET)
    public synchronized String UpdateOLA() throws IOException {
        Timestamp now = Timestamp.from(Instant.now());
        logger.info("Timestamp method start : " + now);

        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //Connection testing
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        String reportForm = configValue.getRemedyMiddleFormMDO();

        List<EntryListInfo> ListReportOLA = remedyAPI.getRemedyRecordByQuery(remedyServer, reportForm, "'OLA Sync'=\"0\" ");
        logger.info("------------------ Looking for Report ------------------");
        try {
            for (EntryListInfo ReportOLA : ListReportOLA) {
                Entry reportEntry = remedyServer.getEntry(reportForm, ReportOLA.getEntryID(), null);
                reportEntry.put(7, new Value("2"));
                remedyServer.setEntry(reportForm, reportEntry.getEntryId(), reportEntry, null, 0);
                ReportOLA reportOLA = new ReportOLA();
                reportOLA.WONumber = getValueFromRemedy(reportEntry, 536870915);
                reportOLA.InstanceID = getValueFromRemedy(reportEntry, 536870928);
                reportOLA.OLAName = getValueFromRemedy(reportEntry, 536871011);
                reportOLA.OLAName1 = getValueFromRemedy(reportEntry, 536870994);
                reportOLA.OLAName2 = getValueFromRemedy(reportEntry, 536870936);
                reportOLA.OLAName3 = getValueFromRemedy(reportEntry, 536870943);
                logger.info("WO Number : " + reportOLA.WONumber);

                List<String> oLAValidation = new ArrayList<>();
                getValueFromRemedytoList(reportEntry, 536870998, oLAValidation);
                getValueFromRemedytoList(reportEntry, 536871027, oLAValidation);
                getValueFromRemedytoList(reportEntry, 536871031, oLAValidation);
                getValueFromRemedytoList(reportEntry, 536871035, oLAValidation);

                WorkingHour workingHour = new WorkingHour();
                PublicHoliday ph = new PublicHoliday();
                ph.setHolidayDate(getHoliday(remedyServer, remedyAPI));
                List<String> reportOLAValidation = new ArrayList<>();
                int totalDuration = 0;
                List<EntryListInfo> OLAMeasurements = remedyAPI.getRemedyRecordByQuery(remedyServer, configValue.getRemedyMiddleFormOLA(),
                        "'ApplicationInstanceID'=\"" + reportOLA.InstanceID + "\" and 'SLACategory' =\"1\" and 'MeasurementStatus' !=\"0\"");
                for (EntryListInfo OLAMeasurement : OLAMeasurements) {
                    Entry OLAEntry = remedyServer.getEntry(configValue.getRemedyMiddleFormOLA(), OLAMeasurement.getEntryID(), null);

                    MeasurementOLA mola = new MeasurementOLA();
                    mola.SVTName = getValueFromRemedy(OLAEntry, 300411500);
                    mola.setSVTName(getValueFromRemedy(OLAEntry, 300411500));
                    mola.setRecordAssigment(getValueFromRemedy(OLAEntry, 300923600));
                    int timeDuration = getIntValueFromRemedy(OLAEntry, 300436500);
                    int timeGoal = getIntValueFromRemedy(OLAEntry, 300272700);
                    String actualDuration = secondToHour(timeDuration);
                    String actualGoal = secondToHour(timeGoal);
                    mola.setGoalschedTime(actualGoal);
                    mola.setOverallElapsedTime(actualDuration);
                    totalDuration = totalDuration + timeDuration;
//                        mola.setTotalDurationOLA(secondToHour(totalDuration));
                    int statusOLA = getIntValueFromRemedy(OLAEntry, 300365100);

                    String fethedStatus = fetchOlaStatus(statusOLA);
                    if (fethedStatus.equals("Detached")) {
                        if (timeDuration <= timeGoal) {
                            mola.setMeasurementStatus("Within Service Target");
                        } else {
                            mola.setMeasurementStatus("Service Target Breach");
                        }
                    } else {
                        mola.setMeasurementStatus(fethedStatus);
                    }

                    String SGID = getValueFromRemedy(OLAEntry, 300923600);
                    SupportGroup sg = new SupportGroup();
                    getSupportGroup(remedyAPI, remedyServer, SGID, sg, reportOLAValidation);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
                    TimeZone tz = TimeZone.getTimeZone("Asia/Jakarta");
                    dateFormat.setTimeZone(tz);
                    mola.setOverallStartTime(dateFormat(getValueFromRemedy(OLAEntry, 300364400), dateFormat));
                    String stopTimeOLA = getValueFromRemedy(OLAEntry, 300364500);
                    mola.setOverallStopTime(dateFormat(stopTimeOLA, dateFormat));
                    mola.setSVTDueDate(dateFormat(getValueFromRemedy(OLAEntry, 300364900), dateFormat));

                    SimpleDateFormat estDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                    TimeZone estZone = TimeZone.getTimeZone("Asia/Jakarta");
                    estDateFormat.setTimeZone(estZone);

                    if (stopTimeOLA.isEmpty()) {
                        Timestamp t = Timestamp.valueOf(dateFormat(getValueFromRemedy(OLAEntry, 300364400), estDateFormat));
                        mola.setEstimateDuration(secondToHour(workingHour.getWorkingMinutesSince(t, ph) * 60));
                    }

                    UpdateOLAReport aReport = new UpdateOLAReport();
                    Thread t1 = new Thread(() -> {
                        try {
                            if (mola.SVTName.equalsIgnoreCase(reportOLA.OLAName)) {

                                aReport.sendOLARecord(mola, reportEntry, remedyServer, sg, 1, configValue);

                            }
                            if (mola.SVTName.equalsIgnoreCase(reportOLA.OLAName1)) {
                                aReport.sendOLARecord(mola, reportEntry, remedyServer, sg, 2, configValue);
                            }
                        } catch (ARException ex) {
                            java.util.logging.Logger.getLogger(IntegrationController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                    Thread t2 = new Thread(() -> {
                        try {

                            if (mola.SVTName.equalsIgnoreCase(reportOLA.OLAName2)) {
                                aReport.sendOLARecord(mola, reportEntry, remedyServer, sg, 3, configValue);

                            }
                            if (mola.SVTName.equalsIgnoreCase(reportOLA.OLAName3)) {
                                aReport.sendOLARecord(mola, reportEntry, remedyServer, sg, 4, configValue);
                            }
                        } catch (ARException ex) {
                            java.util.logging.Logger.getLogger(IntegrationController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                    t1.start();
                    t2.start();

                }
                reportEntry.put(536870947, new Value(secondToHour(totalDuration)));
                reportEntry.put(7, new Value("1"));
                OLAValidate.Validation(oLAValidation, reportOLAValidation, reportEntry, remedyServer, configValue);
                remedyServer.setEntry(reportForm, reportEntry.getEntryId(), reportEntry, null, 0);
            }
        } catch (ARException | NumberFormatException e) {
            logger.info("Error getting OLA Records : " + e);
        }
        Timestamp end = Timestamp.from(Instant.now());
        logger.info("Timestamp method end : " + end);
        return "result";
    }

    @RequestMapping(value = "updateDuration", method = RequestMethod.GET)
    public String updateNow() {
        Timestamp now = Timestamp.from(Instant.now());
        logger.info("Timestamp update dual start : " + now);
        String query1 = "'WO Status'!=\"Completed\" AND 'WO Status'!=\"Rejected\" AND 'WO Status'!=\"Cancelled\" and 'SSC Tower'=\"SSC Finance\"";
        String query2 = "'WO Status'!=\"Completed\" AND 'WO Status'!=\"Rejected\" AND 'WO Status'!=\"Cancelled\" and 'SSC Tower'=\"Non SSC Finance\"";
        Thread t1 = new Thread(() -> {
            logger.info("Thread 1 Start");
            updateDuration(query1);
        });

        Thread t2 = new Thread(() -> {
            logger.info("Thread 2 Start");
            updateDuration(query2);
        });

        t1.start();
        t2.start();

        Timestamp end = Timestamp.from(Instant.now());
        logger.info("Timestamp update dual stop : " + end);
        return "result";
    }

    private static synchronized void updateDuration(String query) {
        logger.info("------------------ Updating OLA est duration ------------------");
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
        ConfigurationValue configurationValue = applicationContext.getBean(ConfigurationValue.class);

        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser serverUser = remedyConnection.connectToRemedy(configurationValue);
        String reportForm = configurationValue.getRemedyMiddleFormMDO();
        RemedyAPI remedyAPI = new RemedyAPI();
        List<EntryListInfo> entryListInfos = remedyAPI.getRemedyRecordByQuery(serverUser, reportForm, query);

        try {
            for (EntryListInfo entryInfo : entryListInfos) {
                Entry reportEntry = serverUser.getEntry(reportForm, entryInfo.getEntryID(), null);
                ReportOLA reportOLA = new ReportOLA();
                reportOLA.WONumber = getValueFromRemedy(reportEntry, 536870915);
                logger.info("WO Number : " + reportOLA.WONumber);

                WorkingHour wh = new WorkingHour();
                PublicHoliday ph = new PublicHoliday();
                ph.setHolidayDate(getHoliday(serverUser, remedyAPI));
                SimpleDateFormat estDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                TimeZone estZone = TimeZone.getTimeZone("Asia/Jakarta");
                estDateFormat.setTimeZone(estZone);

                Timestamp t;

                if (!getValueFromRemedy(reportEntry, 536871011).isEmpty()) {
                    if (getValueFromRemedy(reportEntry, 536870986).isEmpty()) {
                        t = Timestamp.valueOf(dateFormat(getValueFromRemedy(reportEntry, 536870985), estDateFormat));
                        reportEntry.put(536870958, new Value(secondToHour(wh.getWorkingMinutesSince(t, ph) * 60)));
                    } else {
                        reportEntry.put(536870958, new Value(getValueFromRemedy(reportEntry, 536870952)));
                    }
                }
                if (!getValueFromRemedy(reportEntry, 536870994).isEmpty()) {
                    if (getValueFromRemedy(reportEntry, 536870990).isEmpty()) {
                        t = Timestamp.valueOf(dateFormat(getValueFromRemedy(reportEntry, 536870987), estDateFormat));
                        reportEntry.put(536870962, new Value(secondToHour(wh.getWorkingMinutesSince(t, ph) * 60)));
                    } else {
                        reportEntry.put(536870962, new Value(getValueFromRemedy(reportEntry, 536870960)));
                    }
                }
                if (!getValueFromRemedy(reportEntry, 536870936).isEmpty()) {
                    if (getValueFromRemedy(reportEntry, 536870991).isEmpty()) {
                        t = Timestamp.valueOf(dateFormat(getValueFromRemedy(reportEntry, 536870988), estDateFormat));
                        reportEntry.put(536870965, new Value(secondToHour(wh.getWorkingMinutesSince(t, ph) * 60)));

                    } else {
                        reportEntry.put(536870965, new Value(getValueFromRemedy(reportEntry, 536870963)));
                    }
                }
                if (!getValueFromRemedy(reportEntry, 536870943).isEmpty()) {
                    if (getValueFromRemedy(reportEntry, 536870992).isEmpty()) {
                        t = Timestamp.valueOf(dateFormat(getValueFromRemedy(reportEntry, 536870989), estDateFormat));
                        reportEntry.put(536870968, new Value(secondToHour(wh.getWorkingMinutesSince(t, ph) * 60)));

                    } else {
                        reportEntry.put(536870968, new Value(getValueFromRemedy(reportEntry, 536870966)));
                    }
                }
                serverUser.setEntry(configurationValue.getRemedyMiddleFormMDO(), reportEntry.getEntryId(), reportEntry, null, 0);
            }
        } catch (ARException e) {

            logger.info("Something went wrong : " + e);
        }
    }

    @RequestMapping(value = "OLAValidation", method = RequestMethod.GET)
    public synchronized String OLAValidation() {
        Timestamp start = Timestamp.from(Instant.now());
        logger.info("Timestamp method validation start : " + start);
        //Get configuration value from DCconfig.properties
        ApplicationContext context = new AnnotationConfigApplicationContext(com.bmc.mii.domain.ConfigFile.class);
        ConfigurationValue configValue = context.getBean(ConfigurationValue.class);

        //Connection testing
        RemedyConnection remedyConnection = new RemedyConnection();
        ARServerUser remedyServer = remedyConnection.connectToRemedy(configValue);
        RemedyAPI remedyAPI = new RemedyAPI();
        String reportForm = configValue.getRemedyMiddleFormMDO();
        List<EntryListInfo> ListReportOLA = remedyAPI.getRemedyRecordByQuery(remedyServer, reportForm, "'Validate Group'=\"0\" ");
        logger.info("------------------ Looking for Report ------------------");
        try {
            for (EntryListInfo ReportOLA : ListReportOLA) {
                Entry reportEntry = remedyServer.getEntry(reportForm, ReportOLA.getEntryID(), null);
                reportEntry.put(536870919, new Value("2"));
                remedyServer.setEntry(reportForm, reportEntry.getEntryId(), reportEntry, null, 0);
                ReportOLA reportOLA = new ReportOLA();
                List<String> reportOLAValidation = new ArrayList<>();
                reportOLAValidation.add(getValueFromRemedy(reportEntry, 536871016));
                reportOLAValidation.add(getValueFromRemedy(reportEntry, 536871004));
                reportOLAValidation.add(getValueFromRemedy(reportEntry, 536870950));
                reportOLAValidation.add(getValueFromRemedy(reportEntry, 536870949));

                List<String> oLAValidation = new ArrayList<>();
                getValueFromRemedytoList(reportEntry, 536870998, oLAValidation);
                getValueFromRemedytoList(reportEntry, 536871027, oLAValidation);
                getValueFromRemedytoList(reportEntry, 536871031, oLAValidation);
                getValueFromRemedytoList(reportEntry, 536871035, oLAValidation);

                OLAValidate.Validation(oLAValidation, reportOLAValidation, reportEntry, remedyServer, configValue);
            }
        } catch (ARException are) {
            logger.info(are);
        }
        Timestamp ended = Timestamp.from(Instant.now());
        logger.info("Timestamp method validation end : " + ended);
        return "result";
    }

    private void getSupportGroup(RemedyAPI remedyAPI, ARServerUser remedyServer, String SGID, SupportGroup sg, List<String> reportOLAValidation) {

        String supportGroupForm = "CTM:Support Group";
        List<EntryListInfo> ListSupportGroup = remedyAPI.getRemedyRecordByQuery(remedyServer, supportGroupForm, "'Support Group ID'=\"" + SGID + "\"");
        logger.info("------------------ Looking for Support Group ------------------");
        try {
            for (EntryListInfo supportGroup : ListSupportGroup) {
                Entry groupData = remedyServer.getEntry(supportGroupForm, supportGroup.getEntryID(), null);
                sg.setSupportGroupName(getValueFromRemedy(groupData, 1000000015));
                sg.setSupportGroupOrg(getValueFromRemedy(groupData, 1000000014));
                reportOLAValidation.add(getValueFromRemedy(groupData, 1000000015));
            }
        } catch (ARException are) {
            logger.info("error getting support group" + are);
        }
    }

    private static String getValueFromRemedy(Entry record, Object fieldID) {
        if (record.get(fieldID).getValue() == null) {
            return "";
        }

        return record.get(fieldID).getValue().toString();
    }

    private static int getIntValueFromRemedy(Entry record, Object fieldID) {
        if (record.get(fieldID).getValue() == null) {
            return 0;
        }

        return record.get(fieldID).getIntValue();
    }

    private boolean getValueFromRemedytoList(Entry record, Object fieldID, List<String> reportOLA) {
        if (record.get(fieldID).getValue() == null) {
            return false;
        }

        return reportOLA.add(record.get(fieldID).getValue().toString());
    }

    private static String secondToHour(int secondTime) {

        int secondsLeft = secondTime % 3600 % 60;
        int minutes = (int) Math.floor(secondTime % 3600 / 60);
        int hours = (int) Math.floor(secondTime / 3600);

        String HH = ((hours < 10) ? "0" : "") + hours;
        String MM = ((minutes < 10) ? "0" : "") + minutes;
        String SS = ((secondsLeft < 10) ? "0" : "") + secondsLeft;

        return HH + ":" + MM + ":" + SS;
    }

    private static String fetchOlaStatus(int statusNumber) {
        Map<Integer, String> statusFormat = new HashMap<Integer, String>();
        statusFormat = new HashMap<Integer, String>();
        statusFormat.put(0, "Invalid");
        statusFormat.put(1, "Within Service Target");
        statusFormat.put(2, "Pending");
        statusFormat.put(3, "Invalid");
        statusFormat.put(4, "Within Service Target");
        statusFormat.put(5, "Service Target Breach");
        statusFormat.put(6, "Invalid");
        statusFormat.put(7, "Service Target Breach");
        statusFormat.put(8, "Detached");
        statusFormat.put(9, "Service Target Warning");
        statusFormat.put(10, "Invalid");
        statusFormat.put(11, "Invalid");

        return statusFormat.get(statusNumber);
    }

    private static String dateFormat(String date, SimpleDateFormat dateFormat) {

        if (date.isEmpty()) {
            return "";
        }
        String time = date.substring(11, 21);;
        long longParsedTime = Long.parseLong(time);
        Date dateTime = new Date(longParsedTime * 1000);

        return dateFormat.format(dateTime);
    }

    private static List<String> formatedHoliday(String Holiday) {
        List<String> holidays = new ArrayList<>();
        try {

//            Holiday = "12/31/20;1/1/21;2/12/21;3/11/21;3/14/21;4/2/21;5/1/21;5/12/21;5/13/21;5/14/21;5/26/21;6/1/21;7/20/21;8/11/21;8/17/21;10/20/21;12/25/21;18/10/21;";
            String[] listHoliday = Holiday.split(";");

            for (String holiday : listHoliday) {
                SimpleDateFormat dt = new SimpleDateFormat("MM/dd/yy");
                Date date = dt.parse(holiday);

                SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println(dt1.format(date));
                System.out.println(holiday);
                holidays.add(dt1.format(date));

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return holidays;
    }

    private static List<String> getHoliday(ARServerUser remedyServer, RemedyAPI remedyAPI) {
        String holidayForm = "Business Time Segment";
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String query = "Public Holiday " + year;
        List<EntryListInfo> listHolidayInfo = remedyAPI.getRemedyRecordByQuery(remedyServer, holidayForm, "'Description'=\"" + query + "\"");
        logger.info("Getting Public Holliday");
        WorkingHour workingHour = new WorkingHour();
        List<String> holidays = new ArrayList<>();
        try {
            for (EntryListInfo holidayInfo : listHolidayInfo) {
                Entry HolidayEntry = remedyServer.getEntry(holidayForm, holidayInfo.getEntryID(), null);
                String Holiday = getValueFromRemedy(HolidayEntry, 2325);
                String[] listHoliday = Holiday.split(";");

                for (String holiday : listHoliday) {
                    SimpleDateFormat dt = new SimpleDateFormat("MM/dd/yy");
                    Date date = dt.parse(holiday);

                    SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
                    System.out.println(dt1.format(date));
                    System.out.println(holiday);
                    holidays.add(dt1.format(date));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return holidays;
    }
}
