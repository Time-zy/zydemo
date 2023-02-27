package com.example.zydemo.datasource.enums;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2023/02/21 10:45
 * @Since jdk8+
 **/
public enum DataSourceConfigEnum {

    PostgreSQL("PostgreSQL","jdbc:postgresql://","/",null,"org.postgresql.Driver"),
    MySQL5("MySQL5.7","jdbc:mysql://","/","?useSSL=false","com.mysql.jdbc.Driver"),
    MySQL8("MySQL8.0","jdbc:mysql://","/","?useSSL=false&serverTimezone=UTC","com.mysql.cj.jdbc.Driver"),
    Oracle("Oracle","jdbc:oracle:thin:@","/",null,"oracle.jdbc.driver.OracleDriver"),
    SQLServer("SQLServer","jdbc:sqlserver://",";DatabaseName=",null,"com.microsoft.sqlserver.jdbc.SQLServerDriver");

    private String SQLType;
    private String urlFront;
    private String urlMid;
    private String urlBack;
    private String driverClassName;

    DataSourceConfigEnum(String SQLType, String urlFront, String urlMid, String urlBack, String driverClassName) {
        this.SQLType = SQLType;
        this.urlFront = urlFront;
        this.urlMid = urlMid;
        this.urlBack = urlBack;
        this.driverClassName = driverClassName;
    }

    public String getUrlPre() {
        return urlFront;
    }

    public String getUrlMid() {
        return urlMid;
    }

    public String getUrlBack() {
        return urlBack;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getSQLType() {
        return SQLType;
    }

    public static DataSourceConfigEnum getEnum(String type){
        DataSourceConfigEnum[] dataSources = values();
        for (DataSourceConfigEnum datasource : dataSources) {
            if(datasource.getSQLType().equals(type)){
                return  datasource;
            }
        }
        return null;
    }
}
