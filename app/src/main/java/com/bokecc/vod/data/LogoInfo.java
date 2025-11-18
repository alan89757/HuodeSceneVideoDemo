package com.bokecc.vod.data;

public class LogoInfo {
    private String fileName;
    private String logoUrl;

    public LogoInfo(String fileName, String logoUrl) {
        this.fileName = fileName;
        this.logoUrl = logoUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
