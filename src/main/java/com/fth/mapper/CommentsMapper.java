package com.fth.mapper;

import com.fth.pojo.Comments;
import com.fth.vo.CommentsVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentsMapper {
    @Insert("insert into comments (essay_id, user_id, content,create_time) " +
            "values" +
            " (#{essayId}, #{userId}, #{content}, now()) ")
    void add(Comments com);

    @Select("select c.*,u.username,u.avatar from comments c left join user u " +
            "on c.user_id=u.id " +
            "where c.essay_id=#{essayId} order by c.create_time desc")
    List<CommentsVO> show(Integer essayId);

    @Update("update comments set liked=liked+1 where id=#{id}")
    void incryLikes(Integer id);

    @Update("update comments set liked=liked-1 where id=#{id}")
    void decryLikes(Integer id);
}
