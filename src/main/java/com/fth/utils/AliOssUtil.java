package com.fth.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
@Slf4j
public class AliOssUtil {
public String upload(byte[] bytes, String objectName) {
    String endpoint="oss-cn-beijing.aliyuncs.com";
    String bucketName="dailyproject";
    String accessKeyId=System.getenv("OSS_ACCESS_KEY_ID");
    String accessKeySecret=System.getenv("OSS_ACCESS_KEY_SECRET");

    // 创建OSSClient实例。
    OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    try {
        // 创建PutObject请求。
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
    } catch (OSSException oe) {
        System.out.println("Caught an OSSException, which means your request made it to OSS, "
                + "but was rejected with an error response for some reason.");
        System.out.println("Error Message:" + oe.getErrorMessage());
        System.out.println("Error Code:" + oe.getErrorCode());
        System.out.println("Request ID:" + oe.getRequestId());
        System.out.println("Host ID:" + oe.getHostId());
    } catch (ClientException ce) {
        System.out.println("Caught an ClientException, which means the client encountered "
                + "a serious internal problem while trying to communicate with OSS, "
                + "such as not being able to access the network.");
        System.out.println("Error Message:" + ce.getMessage());
    } finally {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    //文件访问路径规则 https://BucketName.Endpoint/ObjectName
    StringBuilder stringBuilder = new StringBuilder("https://");
    stringBuilder
            .append(bucketName)
            .append(".")
            .append(endpoint)
            .append("/")
            .append(objectName);

    log.info("文件上传到:{}", stringBuilder.toString());

    return stringBuilder.toString();
    }
}