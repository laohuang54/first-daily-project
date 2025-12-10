//package com.fth.controller.FuncController;
//
//import com.fth.dto.CommentsDTO;
//import com.fth.dto.Result;
//import com.fth.service.impl.CommentsService;
//import com.fth.service.impl.SignService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/common")
//public class CommonController {
//    @Autowired
//    private SignService signService;
//    @Autowired
//    private CommentsService commentsService;
//
//    @GetMapping("/comments/show/{essay_id}")
//    public Result showComments(@PathVariable Integer essay_id){
//        return commentsService.show(essay_id);
//    }
//
//    // 发表评论
//    @PostMapping("/comments/add")
//    public Result addComments(@RequestBody CommentsDTO commentsDTO) {
//        // TODO 实现多级评论功能
//        return commentsService.add(commentsDTO);
//    }
//
//    @PutMapping("/signin") //用户签到
//    public Result signIn() {
//        return signService.sign();
//    }
//
//    @GetMapping("/sign/show")
//    public Result showSign(String time) { //time ：几几年几月份
//        //TODO 显示其他年/月份的签到记录
//        return signService.showSign(time);
//    }
//}
