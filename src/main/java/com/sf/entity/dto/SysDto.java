package com.sf.entity.dto;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-07 22:49
 */
public class SysDto {
    //主机型号
    private String manufacturer;

    //系统版本信息
    private String sysInfo;

    //cpu核心数
    private String cpuCoreCount;

    //cpu利用率
    private String cpuUsedRatio;

    //cpu系统利用率
    private String cpuSysUsedRatio;

    //cpu用户利用率
    private String cpuUserUsedRatio;

    //cpu当前空闲率
    private String cpuFreeRatio;

    //cpu当前空闲率
    private String cpuWaitRatio;

    //cpu型号
    private String cpuModel;

    //总运行内存
    private String memoryTotal;

    //可用运行内存
    private String memoryAvailable;

    //已使用运行内存
    private String memoryUsed;

    //磁盘总大小
    private String diskTotal;

    //磁盘剩余大小
    private String diskFree;

    //磁盘已用大小
    private String diskUsed;

    //磁盘使用率
    private String diskUsedRatio;


    public String getCpuCoreCount() {
        return cpuCoreCount;
    }

    public void setCpuCoreCount(String cpuCoreCount) {
        this.cpuCoreCount = cpuCoreCount;
    }

    public String getCpuUsedRatio() {
        return cpuUsedRatio;
    }

    public void setCpuUsedRatio(String cpuUsedRatio) {
        this.cpuUsedRatio = cpuUsedRatio;
    }


    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }


    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public String getCpuSysUsedRatio() {
        return cpuSysUsedRatio;
    }

    public void setCpuSysUsedRatio(String cpuSysUsedRatio) {
        this.cpuSysUsedRatio = cpuSysUsedRatio;
    }

    public String getCpuUserUsedRatio() {
        return cpuUserUsedRatio;
    }

    public void setCpuUserUsedRatio(String cpuUserUsedRatio) {
        this.cpuUserUsedRatio = cpuUserUsedRatio;
    }

    public String getCpuFreeRatio() {
        return cpuFreeRatio;
    }

    public void setCpuFreeRatio(String cpuFreeRatio) {
        this.cpuFreeRatio = cpuFreeRatio;
    }


    public String getCpuWaitRatio() {
        return cpuWaitRatio;
    }

    public void setCpuWaitRatio(String cpuWaitRatio) {
        this.cpuWaitRatio = cpuWaitRatio;
    }


    public String getSysInfo() {
        return sysInfo;
    }

    public void setSysInfo(String sysInfo) {
        this.sysInfo = sysInfo;
    }


    public String getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(String memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public String getMemoryAvailable() {
        return memoryAvailable;
    }

    public void setMemoryAvailable(String memoryAvailable) {
        this.memoryAvailable = memoryAvailable;
    }

    public String getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(String memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public String getDiskTotal() {
        return diskTotal;
    }

    public void setDiskTotal(String diskTotal) {
        this.diskTotal = diskTotal;
    }


    public String getDiskFree() {
        return diskFree;
    }

    public void setDiskFree(String diskFree) {
        this.diskFree = diskFree;
    }

    public String getDiskUsed() {
        return diskUsed;
    }

    public void setDiskUsed(String diskUsed) {
        this.diskUsed = diskUsed;
    }

    public String getDiskUsedRatio() {
        return diskUsedRatio;
    }

    public void setDiskUsedRatio(String diskUsedRatio) {
        this.diskUsedRatio = diskUsedRatio;
    }

    @Override
    public String toString() {
        return "SysDto{" +
                "cpuCoreCount='" + cpuCoreCount + '\'' +
                ", cpuUsedRatio='" + cpuUsedRatio + '\'' +
                ", cpuSysUsedRatio='" + cpuSysUsedRatio + '\'' +
                ", cpuUserUsedRatio='" + cpuUserUsedRatio + '\'' +
                ", cpuFreeRatio='" + cpuFreeRatio + '\'' +
                ", cpuWaitRatio='" + cpuWaitRatio + '\'' +
                ", cpuModel='" + cpuModel + '\'' +
                ", sysInfo='" + sysInfo + '\'' +
                ", memoryTotal='" + memoryTotal + '\'' +
                ", memoryAvailable='" + memoryAvailable + '\'' +

                '}';
    }
}
