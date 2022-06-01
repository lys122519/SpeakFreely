package com.sf.utils;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import com.sf.controller.FilesController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(OBSUtils.class);

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

    /**
     * OBS 下载文件
     * @param fileFakeName
     * @return
     * @throws IOException
     */
    public static ByteArrayOutputStream downloadFile(String fileFakeName) throws IOException {
        InputStream input = null;
        ByteArrayOutputStream bos = null;
        try {

            input = staticObsClient.getObject(BUCKET_NAME, fileFakeName).getObjectContent();
            byte[] b = new byte[1024];
            bos = new ByteArrayOutputStream();
            int len;
            while ((len = input.read(b)) != -1) {
                bos.write(b, 0, len);
            }
        }catch(ObsException ex){
            log.warn(ex.getErrorMessage());
        }
        assert bos != null;
        bos.close();
        input.close();
        return bos;
    }


}
