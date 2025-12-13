package com.fth.controller.user;

import com.fth.dto.*;
import com.fth.pojo.Essay;
import com.fth.pojo.User;
import com.fth.properties.JwtProperty;
import com.fth.service.IUserService;
import com.fth.service.impl.*;
import com.fth.utils.AliOssUtil;
import com.fth.utils.JwtUtil;
import com.fth.utils.UserHolder;
import com.fth.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.fth.constant.JwtConstant.USER_ID;
import static com.fth.constant.UserConstant.ESSAY_ERROR;
import static com.fth.constant.UserConstant.UPLOAD_ERROR;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private EssayService essayService;
    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private SignService signService;
    @Autowired
    private CommentsService commentsService;

    @Autowired
    private ShopService shopService;


    @PutMapping("/essaylike/{id}")
    public Result likeEssay(@PathVariable Integer id) { // id:文章id
        return essayService.likeEssay(id);
    }

    @PutMapping("/commentslike/{id}")
    public Result likeComments(@PathVariable Integer id) { // id:评论id
        return commentsService.likeComments(id);
    }

    @PutMapping("/seckill") //用户抢购秒杀商品
    public Result seckill(Integer id){
        return shopService.seckill(id);
    }

    @PutMapping("/sell")
    public Result sell(Integer id){ //用户购买普通商品
        return shopService.sell(id);
    }

    @GetMapping("/show") //展示商品
    public Result show(){
        //TODO 展示商品
        shopService.show();
        return Result.ok();
    }

    @GetMapping("/comments/show/{essay_id}")
    public Result showComments(@PathVariable Integer essay_id){
        return commentsService.show(essay_id);
    }

    // 发表评论
    @PostMapping("/comments/add")
    public Result addComments(@RequestBody CommentsDTO commentsDTO) {
        // TODO 实现多级评论功能
        return commentsService.add(commentsDTO);
    }

    @PutMapping("/signin") //用户签到
    public Result signIn() {
        return signService.sign();
    }

    @GetMapping("/sign/show")
    public Result showSign(String time) { //time ：几几年几月份
        //TODO 显示其他年/月份的签到记录
        return signService.showSign(time);
    }

    @DeleteMapping("/deleteuser") //注销账号
    public Result deleteUser() {
        log.info("用户注销");
        Integer id = UserHolder.getUserId();
        return userService.deleteUser(id);
    }

    @PutMapping("/updatepassword")
    public Result updatePassword(String password, String newPassword) {
        return userService.updatePassword(password, newPassword);
    }

    @PutMapping("/updateavatar")
    public Result updateAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.fail(UPLOAD_ERROR);
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            log.error("图片文件格式错误：无后缀");
            return Result.fail("图片格式错误，请上传带后缀的图片（如.jpg、.png）");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 1.2 构造OSS中的文件路径+名称（动态图片存 essay-img/ 目录，保持原有逻辑）
        String objectName = "essay-img/" + UUID.randomUUID() + extension;
        String url = aliOssUtil.upload(file.getBytes(), objectName);
        User user = new User();
        user.setAvatar(url);
        user.setId(UserHolder.getUserId());
        userService.updateInfo(user);
        return Result.ok(url);
    }

    @PutMapping("/updateinfo") //更新用户个人信息
    public Result updateInfo(@RequestBody User user){
        return userService.updateInfo(user);
    }

    @GetMapping("/getOthers/{id}") //查看其他用户信息
    public Result getOthers(@PathVariable Integer id){
        UserVO user= userService.getOthers(id);
        return Result.ok(user);
    }

    @GetMapping("/info/{id}") //查看自己的个人信息
    public Result getUserInfo(@PathVariable Integer id) {
        User user = userService.getUserInfo(id);
        return Result.ok(user);
    }

    @GetMapping("/singleessay/{id}") //点击评论区后 显示文章详细内容
    public Result getSingleEssay(@PathVariable Integer id) {
        Essay singleEssay = essayService.getSingleEssay(id);
        return Result.ok(singleEssay);
    }

    @GetMapping("/allessay") //用户查看所有文章
    public Result getAllEssay() {
        return essayService.getAllEssay();
    }

    @PostMapping("/addessay")
    public Result addEssay(@ModelAttribute EssayDTO essay) {
        log.debug("用户上传动态: {}", essay); // 用占位符打印，避免字符串拼接问题

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
            String objectName = "essay-img/" + UUID.randomUUID() + extension;

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

    @DeleteMapping("/delete/{id}")
    public Result deleteEssay(@PathVariable Integer id) {
        essayService.deleteEssay(id);
        return Result.ok();
    }





    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录：{}",loginDTO);
        User login = userService.login(loginDTO);
        Map<String,Object> claims =new HashMap<>();
        claims.put(USER_ID,login.getId());
        String jwt = JwtUtil.createJWT(jwtProperty.getUserSecretKey()
                , jwtProperty.getUserTtl()
                , claims);
        login.setToken(jwt);
        return Result.ok(login);
    }

    @PostMapping("/register")
    public Result register(@ModelAttribute RegisterDTO userDTO) {
        log.info("用户注册：{}", userDTO);
        try {
            String avatarUrl = null;
            // 1. 获取前端传递的头像文件（从DTO中取出MultipartFile）
            MultipartFile avatarFile = userDTO.getAvatar();

            // 2. 处理文件上传（有头像且文件有效才上传）
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // 2.1 提取文件后缀（保持你的原有逻辑）
                String originalFilename = avatarFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                // 2.2 构造OSS中的唯一文件名（避免重名，保持原有逻辑）
                String objectName = "user-avatar/" + UUID.randomUUID() + extension;
                // 2.3 调用改造后的AliOssUtil：传递 MultipartFile 和 objectName（核心修改）
                avatarUrl = aliOssUtil.upload(avatarFile.getBytes(), objectName);

                // 2.4 修复日志：打印实际的上传URL（原代码漏传参数）
                log.info("文件上传成功，URL：{}", avatarUrl);
            }

            // 3. 传递DTO和头像URL给Service层（逻辑不变）
            return userService.registerWithAvatar(userDTO, avatarUrl);

        } catch (IllegalArgumentException e) {
            // 捕获工具类中的参数校验错误（如文件为空、格式不支持、配置缺失）
            log.error("注册失败：{}", e.getMessage());
            return Result.fail(e.getMessage());
        } catch (com.aliyun.oss.OSSException e) {
            // 捕获OSS服务端错误（如AK错误、Bucket不存在、权限不足）
            log.error("OSS上传失败：错误码={}, 错误信息={}", e.getErrorCode(), e.getErrorMessage());
            return Result.fail(UPLOAD_ERROR + "：OSS服务异常");
        } catch (com.aliyun.oss.ClientException e) {
            // 捕获OSS客户端错误（如网络不通、Endpoint错误）
            log.error("OSS上传失败：客户端错误={}", e.getMessage());
            return Result.fail(UPLOAD_ERROR + "：网络异常，请重试");
        } catch (Exception e) {
            // 捕获其他未知错误（如文件转字节流失败）
            log.error("注册失败：", e);
            return Result.fail("注册失败，请联系管理员");
        }
    }
}
