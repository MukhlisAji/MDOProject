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
public class ReportOLA {
    public String WONumber, InstanceID, StartTime, StopTime, OLAStatus, OLAName, Target, Duration, GroupORG, GroupName, GroupID;
    public String StartTime1, StopTime1, OLAStatus1, OLAName1, Target1, Duration1, GroupORG1, GroupName1, GroupID1;
    public String StartTime2, StopTime2, OLAStatus2, OLAName2, Target2, Duration2, GroupORG2, GroupName2, GroupID2;
    public String StartTime3, StopTime3, OLAStatus3, OLAName3, Target3, Duration3, GroupORG3, GroupName3, GroupID3;

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String GroupName) {
        this.GroupName = GroupName;
    }

    public String getGroupName1() {
        return GroupName1;
    }

    public void setGroupName1(String GroupName1) {
        this.GroupName1 = GroupName1;
    }

    public String getGroupName2() {
        return GroupName2;
    }

    public void setGroupName2(String GroupName2) {
        this.GroupName2 = GroupName2;
    }

    public String getGroupName3() {
        return GroupName3;
    }

    public void setGroupName3(String GroupName3) {
        this.GroupName3 = GroupName3;
    }

    
    
}
