/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.mii.domain;

/**
 *
 * @author MukhlisAj
 */
public class MeasurementOLA {
    public String ApplicationUserFriendlyID, SVTName, OverallStartTime, OverallStopTime, 
            OverallElapsedTime, MeasurementStatus, GoalschedTime, RecordAssigment, SVTDueDate, 
            totalDurationOLA, estimateDuration, OLAValidation, InstanceID;
    public Long StopOLATime;
    public int OLAGoal, OLADuration, totalDuration;

    public String getInstanceID() {
        return InstanceID;
    }

    public void setInstanceID(String InstanceID) {
        this.InstanceID = InstanceID;
    }

    public String getOLAValidation() {
        return OLAValidation;
    }

    public void setOLAValidation(String OLAValidation) {
        this.OLAValidation = OLAValidation;
    }

    public String getTotalDurationOLA() {
        return totalDurationOLA;
    }

    public void setTotalDurationOLA(String totalDurationOLA) {
        this.totalDurationOLA = totalDurationOLA;
    }

    
    public String getSVTDueDate() {
        return SVTDueDate;
    }

    public void setSVTDueDate(String SVTDueDate) {
        this.SVTDueDate = SVTDueDate;
    }

    public String getEstimateDuration() {
        return estimateDuration;
    }

    public void setEstimateDuration(String estimateDuration) {
        this.estimateDuration = estimateDuration;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    
    public int getOLAGoal() {
        return OLAGoal;
    }

    public void setOLAGoal(int OLAGoal) {
        this.OLAGoal = OLAGoal;
    }

    public int getOLADuration() {
        return OLADuration;
    }

    public void setOLADuration(int OLADuration) {
        this.OLADuration = OLADuration;
    }

    
    public Long getStopOLATime() {
        return StopOLATime;
    }

    public void setStopOLATime(Long StopOLATime) {
        this.StopOLATime = StopOLATime;
    }
    
    
    
    public String getApplicationUserFriendlyID() {
        return ApplicationUserFriendlyID;
    }

    public void setApplicationUserFriendlyID(String ApplicationUserFriendlyID) {
        this.ApplicationUserFriendlyID = ApplicationUserFriendlyID;
    }

    public String getSVTName() {
        return SVTName;
    }

    public void setSVTName(String SVTName) {
        this.SVTName = SVTName;
    }

    public String getOverallStartTime() {
        return OverallStartTime;
    }

    public void setOverallStartTime(String OverallStartTime) {
        this.OverallStartTime = OverallStartTime;
    }

    public String getOverallStopTime() {
        return OverallStopTime;
    }

    public void setOverallStopTime(String OverallStopTime) {
        this.OverallStopTime = OverallStopTime;
    }

    public String getOverallElapsedTime() {
        return OverallElapsedTime;
    }

    public void setOverallElapsedTime(String OverallElapsedTime) {
        this.OverallElapsedTime = OverallElapsedTime;
    }

    public String getMeasurementStatus() {
        return MeasurementStatus;
    }

    public void setMeasurementStatus(String MeasurementStatus) {
        this.MeasurementStatus = MeasurementStatus;
    }

    public String getGoalschedTime() {
        return GoalschedTime;
    }

    public void setGoalschedTime(String GoalschedTime) {
        this.GoalschedTime = GoalschedTime;
    }

    public String getRecordAssigment() {
        return RecordAssigment;
    }

    public void setRecordAssigment(String RecordAssigment) {
        this.RecordAssigment = RecordAssigment;
    }
    
    
    
}
