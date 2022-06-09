package com.sf.actuator;

import cn.hutool.json.JSONObject;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.sf.entity.dto.SysDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-07 17:00
 */
@Component
public class SysData {
    private static final Logger log = LoggerFactory.getLogger(SysData.class);
    private static SystemInfo systemInfo = new SystemInfo();
    private static HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private static OperatingSystem operatingSystem = systemInfo.getOperatingSystem();

    public static SysDto getSysInfo() {

        SysDto sysDto = new SysDto();
        getCpuInfo(sysDto);
        getOsInfo(sysDto);
        getMemoryInfo(sysDto);
        getDiskInfo(sysDto);
        getComputerSystem(sysDto);
        return sysDto;

    }

    private static SysDto getComputerSystem(SysDto sysDto) {
        ComputerSystem computerSystem = hardware.getComputerSystem();
        //主机型号
        sysDto.setManufacturer(computerSystem.getManufacturer() + "-" + computerSystem.getModel());
        return sysDto;
    }


    private static SysDto getDiskInfo(SysDto sysDto) {
        FileSystem fileSystem = operatingSystem.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            //总大小
            sysDto.setDiskTotal(FormatUtil.formatBytes(fs.getTotalSpace()));

            //剩余大小
            sysDto.setDiskFree(FormatUtil.formatBytes(fs.getUsableSpace()));

            //已经使用量
            sysDto.setDiskUsed(FormatUtil.formatBytes(fs.getTotalSpace() - fs.getUsableSpace()));

            if (fs.getTotalSpace() == 0) {
                //资源的使用率
                sysDto.setDiskUsedRatio(0.00 + "%");
            } else {
                sysDto.setDiskUsedRatio(new DecimalFormat("#.##%").format((fs.getTotalSpace() - fs.getUsableSpace()) * 1.0 / fs.getTotalSpace()));

            }

        }

        return sysDto;
    }


    private static SysDto getMemoryInfo(SysDto sysDto) {
        //内存相关
        GlobalMemory memory = OshiUtil.getMemory();

        //总内存
        long memoryTotal = memory.getTotal();

        sysDto.setMemoryTotal(new DecimalFormat("#.##").format(memoryTotal * 1.0 / 1024 / 1024 / 1024) + "GB");

        //可用内存
        long available = memory.getAvailable();
        sysDto.setMemoryAvailable(new DecimalFormat("#.##").format(available * 1.0 / 1024 / 1024 / 1024) + "GB");

        //已使用内存
        long used = memoryTotal - available;
        sysDto.setMemoryUsed(new DecimalFormat("#.##").format(used * 1.0 / 1024 / 1024 / 1024) + "GB");
        return sysDto;
    }


    private static SysDto getOsInfo(SysDto sysDto) {

        //操作系统版本信息
        OperatingSystem.OSVersionInfo versionInfo = operatingSystem.getVersionInfo();

        //操作系统名称
        String family = operatingSystem.getFamily();

        sysDto.setSysInfo(family + versionInfo);

        return sysDto;
    }

    private static SysDto getCpuInfo(SysDto sysDto) {
        //cpu相关
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();

        //cpu核心数
        sysDto.setCpuCoreCount(cpuInfo.getCpuNum().toString());

        //总CPU使用率
        double used = cpuInfo.getUsed();
        sysDto.setCpuUsedRatio(used + "%");

        //CPU系统使用率
        double sys = cpuInfo.getSys();
        sysDto.setCpuSysUsedRatio(sys + "%");

        //CPU用户使用率
        String user = new DecimalFormat("#.##").format(used - sys);
        sysDto.setCpuUserUsedRatio(user + "%");

        //CPU当前等待率
        sysDto.setCpuWaitRatio(cpuInfo.getWait() + "%");

        //CPU当前空闲率
        sysDto.setCpuFreeRatio(cpuInfo.getFree() + "%");

        //CPU型号信息
        String[] split = cpuInfo.getCpuModel().split("\n");
        sysDto.setCpuModel(split[0]);

        return sysDto;
    }

}
