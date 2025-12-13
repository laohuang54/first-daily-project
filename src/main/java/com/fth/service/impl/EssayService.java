package com.fth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fth.dto.EssayDTO;
import com.fth.dto.Result;
import com.fth.mapper.EssayMapper;
import com.fth.pojo.Essay;
import com.fth.service.IEssayService;
import com.fth.utils.UserHolder;
import com.fth.vo.EssayVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fth.constant.KeysConstant.ESSAY_LIKES_KEY;
import static com.fth.constant.KeysConstant.ESSAY_READ_KEY;

@Service
@Slf4j
public class EssayService implements IEssayService {
    @Autowired
    private EssayMapper essayMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void deleteEssay(Integer id) {
        Integer userId=UserHolder.getUserId();
        essayMapper.deleteById(id,userId);
    }

    @Override
    public void deleteEssayAdmin(Integer id) {
        essayMapper.adminDeleteById(id);
    }

    public Result addEssay(EssayDTO essayDTO, String imgUrl) {
        Essay essay= BeanUtil.copyProperties(essayDTO,Essay.class);
        essay.setUserId(UserHolder.getUserId());
        essay.setImg(imgUrl);
        essayMapper.insert(essay);
        return Result.ok();
    }

    @Override
    @Transactional // 数据库事务：保证点赞数修改和Redis操作一致
    public Result likeEssay(Integer id) {
        Integer userId = UserHolder.getUserId();
        String key = ESSAY_LIKES_KEY + id;
        String userStr = userId.toString();

        // 1. 先执行Redis原子操作，根据返回值判断是点赞还是取消点赞（避免并发问题）
        Long isAddSuccess = stringRedisTemplate.opsForSet().add(key, userStr);
        log.info("isAddSuccess: {}", isAddSuccess);

        if (isAddSuccess != null) {
            if (isAddSuccess>=1) {
                // 2. add成功 → 首次点赞 → 数据库点赞数+1
                log.info("点赞成功，liked+1");
                Long l = essayMapper.incryLikes(id);
                log.info("数据库点赞数+1，l: {}", l);
                return Result.ok("点赞成功");
            } else {
                // 3. add失败（元素已存在）→ 取消点赞 → 先移除Redis中的用户ID，再数据库点赞数-1
                Long removeCount = stringRedisTemplate.opsForSet().remove(key, userStr);
                log.info("移除点赞用户");
                if (removeCount != null && removeCount > 0) {
                    log.info("取消点赞成功");
                    essayMapper.decryLikes(id);
                }
                return Result.ok("取消点赞成功");
            }
        } else {
            // 4. Redis操作失败（如服务不可用）→ 抛出异常或降级处理（根据业务需求）
            return Result.fail("点赞失败");
        }
    }

    @Override
    public Result getAllEssay() {
        List<EssayVO> allessay = essayMapper.getAllessay();
        return Result.ok(allessay);
    }

    @Override
    public Essay getSingleEssay(Integer id) { //id:文章id
        log.info("获取文章id为{}的文章",id);
        String key=ESSAY_READ_KEY+id;
        Integer userId=UserHolder.getUserId();
        String userStr=userId.toString();
        Long isRead=stringRedisTemplate.opsForSet().add(key,userStr); //查看用户是否已阅读
        if (isRead!=null&&isRead>=1) {
            log.info("浏览量加1");
            essayMapper.updateView(id); // 更新浏览量
        }
        return essayMapper.getSingleEssay(id);
    }
}
