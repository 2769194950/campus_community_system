package com.campus.forum.service;

import com.campus.forum.dal.domain.Message;

import java.util.List;

/**
 * @author Bugar
 * @date 2024/5/16
 */
public interface MessageService {

    List<Message> findConversations(int userId);

    List<Message> findLetters(String conversationId);

    int findLetterUnreadCount(int userId, String conversationId);

    int addMessage(Message message);

    int readMessage(List<Integer> ids);
    
    // find unread notice count for a user
    int findNoticeUnreadCount(int userId, String topic);
}

