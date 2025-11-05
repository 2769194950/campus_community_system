package com.campus.forum.dal.mapper;

import com.campus.forum.dal.domain.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

    int insert(Message m);

    /** 我参与的会话（每个会话取最新一条消息） */
    List<Message> selectConversations(@Param("userId") int userId);

    /** 某个会话的消息列表（按时间升序） */
    List<Message> selectLetters(@Param("conversationId") String conversationId,
                                @Param("limit") int limit,
                                @Param("offset") int offset);

    /** 某会话中发给我的未读数 */
    int countUnread(@Param("userId") int userId, @Param("conversationId") String conversationId);

    /** 把某会话中发给我的消息设为已读 */
    int markRead(@Param("userId") int userId, @Param("conversationId") String conversationId);

    /** 最近一条，用于返回新发消息 */
    Message selectById(@Param("id") int id);
}
