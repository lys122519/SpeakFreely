package com.sf.config;

import com.obs.services.ObsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 华为云OBS
 * @author: leung
 * @date: 2022-05-28 21:10
 */
@Configuration
public class ObsConfig {
    @Value("${files.upload.ak}")
    private String ak;
    @Value("${files.upload.sk}")
    private String sk;
    @Value("${files.upload.endpoint}")
    private String endPoint;


    @Bean
    public ObsClient getObsClient() {
        ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        return obsClient;
    }
}
