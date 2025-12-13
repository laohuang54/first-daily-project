package com.fth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fth.dto.CommentsDTO;
import com.fth.dto.Result;
import com.fth.mapper.CommentsMapper;
import com.fth.pojo.Comments;
import com.fth.service.ICommentsService;
import com.fth.utils.UserHolder;
import com.fth.vo.CommentsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fth.constant.KeysConstant.ESSAY_LIKES_KEY;

@Slf4j
@Service
public class CommentsService implements ICommentsService {
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result add(CommentsDTO commentsDTO) {
        log.info("评论发布开始"+commentsDTO);
        Comments com=new Comments();
        BeanUtil.copyProperties(commentsDTO,com);
        Integer userId= UserHolder.getUserId();
        com.setUserId(userId);
        //保存评论
        commentsMapper.add(com);
        log.info("评论发布成功");
        return Result.ok("评论发布成功");
    }

    @Override
    public Result show(Integer essayId) {
        log.info("根据文章id查询评论：{}",essayId);
        List<CommentsVO> show = commentsMapper.show(essayId);
        return Result.ok(show);
    }

    @Override
    public Result likeComments(Integer id) {
        Integer userId = UserHolder.getUserId();
        String key = ESSAY_LIKES_KEY + id;
        String userStr = userId.toString();

        // 1. 先执行Redis原子操作，根据返回值判断是点赞还是取消点赞（避免并发问题）
        Long isAddSuccess = stringRedisTemplate.opsForSet().add(key, userStr);

        if (isAddSuccess != null) {
            if (isAddSuccess>=1) {
                // 2. add成功 → 首次点赞 → 数据库点赞数+1
                log.info("点赞成功");
                commentsMapper.incryLikes(id);
                return Result.ok("点赞成功");
            } else {
                // 3. add失败（元素已存在）→ 取消点赞 → 先移除Redis中的用户ID，再数据库点赞数-1
                Long removeCount = stringRedisTemplate.opsForSet().remove(key, userStr);
                log.info("移除点赞用户");
                if (removeCount != null && removeCount > 0) {
                    log.info("取消点赞成功");
                    commentsMapper.decryLikes(id);
                }
                return Result.ok("取消点赞成功");
            }
        } else {
            // 4. Redis操作失败（如服务不可用）→ 抛出异常或降级处理（根据业务需求）
            return Result.fail("点赞失败");
        }
    }
}