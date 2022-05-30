package com.sf.utils;

import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @Description:
 * @author: leung
 * @date: 2022-05-28 20:20
 */
@Component
public class OBSUtils {

    @Resource
    private ObsClient obsClient;

    private static ObsClient staticObsClient;

    @PostConstruct
    public void setObsClient() {
        staticObsClient = obsClient;
    }

    @Value("${files.upload.bucketname}")
    private static String bucketName;

    /**
     * OBS 上传文件
     *
     * @param fileName
     * @param filePath
     * @throws IOException
     */
    public static void uploadFile(String fileName, String filePath) throws IOException {

        try {
            //  为待上传的本地文件路径，需要指定到具体的文件名
            PutObjectRequest request = new PutObjectRequest();
            request.setBucketName("file-but");
            request.setObjectKey(fileName);
            request.setFile(new File(filePath));
            staticObsClient.putObject(request);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
