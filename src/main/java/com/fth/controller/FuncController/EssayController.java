package com.fth.controller.FuncController;

import com.fth.dto.EssayDTO;
import com.fth.dto.Result;
import com.fth.pojo.Essay;
import com.fth.service.impl.EssayService;
import com.fth.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.fth.constant.UserConstant.ESSAY_ERROR;
import static com.fth.constant.UserConstant.UPLOAD_ERROR;

@RestController
@RequestMapping("/essay")
@Slf4j
public class EssayController {
    @Autowired
    private EssayService essayService;
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/add")
    public Result addEssay(@ModelAttribute EssayDTO essay) {
        log.info("用户上传动态: {}", essay); // 用占位符打印，避免字符串拼接问题

        String ossImgUrl = null; // 存储OSS返回的真实访问URL
        MultipartFile imgFile = essay.getImg(); // 提取前端传递的图片文件

        // 1. 有图片才处理上传（保持原有文件名构造逻辑）
        if (imgFile != null && !imgFile.isEmpty()) {
            // 1.1 提取文件后缀（避免无后缀文件）
            String originalFilename = imgFile.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                log.error("图片文件格式错误：无后缀");
                return Result.fail("图片格式错误，请上传带后缀的图片（如.jpg、.png）");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 1.2 构造OSS中的文件路径+名称（动态图片存 essay-img/ 目录，保持原有逻辑）
            String objectName = "essay-img/" + UUID.randomUUID().toString() + extension;

            try {
                // 1.3 调用改造后的AliOssUtil：传递 MultipartFile（文件流）和 objectName（OSS文件名）
                ossImgUrl = aliOssUtil.upload(imgFile.getBytes(), objectName);
                log.info("动态图片上传成功，OSS访问URL：{}", ossImgUrl);
            } catch (IllegalArgumentException e) {
                // 捕获参数校验错误（如文件格式不支持、配置缺失）
                log.error("图片上传失败：{}", e.getMessage());
                return Result.fail("图片上传失败：" + e.getMessage());
            } catch (com.aliyun.oss.OSSException e) {
                // 捕获OSS服务端错误（如AK错误、Bucket权限不足）
                log.error("OSS图片上传失败：错误码={}, 错误信息={}", e.getErrorCode(), e.getErrorMessage());
                return Result.fail("图片上传失败：OSS服务异常");
            } catch (com.aliyun.oss.ClientException e) {
                // 捕获OSS客户端错误（如网络不通、Endpoint错误）
                log.error("OSS图片上传失败：客户端错误={}", e.getMessage());
                return Result.fail("图片上传失败：网络异常，请重试");
            } catch (Exception e) {
                // 捕获其他上传错误（如文件转字节流失败）
                log.error("图片上传失败：", e);
                return Result.fail("图片上传失败，请联系管理员");
            }
        }

        // 2. 调用Service层：传递真实的OSS访问URL（ossImgUrl），而非本地构造的objectName
        try {
            return essayService.addEssay(essay, ossImgUrl);
        } catch (Exception e) {
            log.error("动态发布失败：", e);
            return Result.fail(ESSAY_ERROR);
        }
    }
}
