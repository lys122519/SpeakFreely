package com.sf.utils;

import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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


    private static final String BUCKET_NAME = "speakfreely";

    /**
     * OBS 上传文件
     *
     * @param fileName
     * @param filePath
     * @throws IOException
     */
    public static PutObjectResult uploadFile(String fileName, String filePath) throws IOException {

        try {
            //  为待上传的本地文件路径，需要指定到具体的文件名
            PutObjectRequest request = new PutObjectRequest();
            request.setBucketName(BUCKET_NAME);
            request.setObjectKey(fileName);
            request.setFile(new File(filePath));
            PutObjectResult putObjectResult = staticObsClient.putObject(request);
            return putObjectResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public static ByteArrayOutputStream downloadFile(String fileUrl) throws IOException {
        InputStream input = staticObsClient.getObject(BUCKET_NAME, fileUrl).getObjectContent();
        byte[] b = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        while ((len = input.read(b)) != -1) {
            bos.write(b, 0, len);
        }

        bos.close();
        input.close();
        return bos;
    }


}
