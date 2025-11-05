package com.campus.forum.service;

import com.campus.forum.dal.domain.Message;
import java.util.List;

public interface MessageService {
    Message send(int fromId, int toId, String content);
    List<Message> conversations(int userId);
    List<Message> letters(String conversationId, int limit, int offset);
    int unreadCount(int userId, String conversationId);
    int markRead(int userId, String conversationId);

    static String convId(int a, int b) {
        return (Math.min(a,b)) + "_" + (Math.max(a,b));
    }
}
