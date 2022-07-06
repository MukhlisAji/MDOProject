package com.bmc.mii.domain;

public class ConfigurationValue {

    private String remedyServer;
    private String remedyUsername, remedyPassword;
    private String remedyPort;
    private String remedyMiddleFormMDO;
    private String remedyMiddleFormSRM;
    private String remedyMiddleFormOLA;

    public ConfigurationValue(String remedyServer, String remedyUsername, String remedyPassword, String remedyPort, String remedyMiddleFormMDO, String remedyMiddleFormSRM, String remedyMiddleFormOLA) {
        this.remedyServer = remedyServer;
        this.remedyUsername = remedyUsername;
        this.remedyPassword = remedyPassword;
        this.remedyPort = remedyPort;
        this.remedyMiddleFormMDO = remedyMiddleFormMDO;
        this.remedyMiddleFormSRM = remedyMiddleFormSRM;
        this.remedyMiddleFormOLA = remedyMiddleFormOLA;
    }

    public String getRemedyServer() {
        return remedyServer;
    }

    public void setRemedyServer(String remedyServer) {
        this.remedyServer = remedyServer;
    }

    public String getRemedyUsername() {
        return remedyUsername;
    }

    public void setRemedyUsername(String remedyUsername) {
        this.remedyUsername = remedyUsername;
    }

    public String getRemedyPassword() {
        return remedyPassword;
    }

    public void setRemedyPassword(String remedyPassword) {
        this.remedyPassword = remedyPassword;
    }

    public String getRemedyPort() {
        return remedyPort;
    }

    public void setRemedyPort(String remedyPort) {
        this.remedyPort = remedyPort;
    }

    public String getRemedyMiddleFormMDO() {
        return remedyMiddleFormMDO;
    }

    public void setRemedyMiddleFormMDO(String remedyMiddleFormMDO) {
        this.remedyMiddleFormMDO = remedyMiddleFormMDO;
    }

    public String getRemedyMiddleFormSRM() {
        return remedyMiddleFormSRM;
    }

    public void setRemedyMiddleFormSRM(String remedyMiddleFormSRM) {
        this.remedyMiddleFormSRM = remedyMiddleFormSRM;
    }

    public String getRemedyMiddleFormOLA() {
        return remedyMiddleFormOLA;
    }

    public void setRemedyMiddleFormOLA(String remedyMiddleFormOLA) {
        this.remedyMiddleFormOLA = remedyMiddleFormOLA;
    }

    
}
