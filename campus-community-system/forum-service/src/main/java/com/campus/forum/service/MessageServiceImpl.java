package com.campus.forum.service;

import com.campus.forum.dal.domain.Message;
import com.campus.forum.dal.mapper.MessageMapper;
import com.campus.forum.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Message send(int fromId, int toId, String content) {
        Message m = new Message();
        m.setFromId(fromId);
        m.setToId(toId);
        m.setConversationId(MessageService.convId(fromId,toId));
        m.setContent(StringUtils.trimWhitespace(content));
        m.setStatus(0);
        messageMapper.insert(m);
        return messageMapper.selectById(m.getId());
    }

    @Override
    public List<Message> conversations(int userId) {
        return messageMapper.selectConversations(userId);
    }

    @Override
    public List<Message> letters(String conversationId, int limit, int offset) {
        return messageMapper.selectLetters(conversationId, limit, offset);
    }

    @Override
    public int unreadCount(int userId, String conversationId) {
        return messageMapper.countUnread(userId, conversationId);
    }

    @Override
    public int markRead(int userId, String conversationId) {
        return messageMapper.markRead(userId, conversationId);
    }
}
