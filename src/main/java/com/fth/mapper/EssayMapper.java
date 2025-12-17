package com.fth.mapper;

import com.fth.pojo.Essay;
import com.fth.vo.EssayVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EssayMapper {

    void insert(Essay essay);

    @Delete("delete from essay where id=#{id} and user_id=#{userId}")
    void deleteById(Integer id, Integer userId);

    @Delete("delete from essay where id=#{id}")
    void adminDeleteById(Integer id);

/**
 * 更新文章点赞数的接口方法
 * 使用@Update注解定义SQL更新语句
 *
 * @param id 文章的唯一标识符
 */
    @Update("update essay set liked=liked+1 where id=#{id}")
    Long incryLikes(Integer id);  // 方法名意为"increase likes"，即增加点赞数

    @Update("update essay set liked=liked-1 where id=#{id}")
    Long decryLikes(Integer id);

    @Select("select e.*,u.username,u.avatar from essay e " +
            "left join user u on e.user_id=u.id " +
            "order by e.create_time desc")
    List<EssayVO> getAllessay();

    @Select("select * from essay where id=#{id}")
    Essay getSingleEssay(Integer id);

    @Update("update essay set `read`=`read`+1 where id=#{id}")
    void updateView(Integer id);

    @Update("update essay set `comments`=`comments`+1 where id=#{essayId}")
    void updateCommentNum(Integer essayId);
}
