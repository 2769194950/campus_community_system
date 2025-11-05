package com.campus.forum.api.controller;

import com.campus.forum.common.Result;
import com.campus.forum.dal.domain.Message;
import com.campus.forum.dal.domain.User;
import com.campus.forum.service.MessageService;
import com.campus.forum.service.UserService;
import com.campus.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired private MessageService messageService;
    @Autowired private UserService userService;
    @Autowired private HostHolder hostHolder;

    /** 会话列表：我参与的所有会话，按最后一条时间倒序 */
    @GetMapping("/conversations")
    public Result conversations() {
        User me = hostHolder.getUser();
        if (me == null) return Result.error(403, "未登录");
        List<Message> rows = messageService.conversations(me.getId());
        // 组装会话对象（含对方用户、未读数）
        List<Map<String,Object>> data = new ArrayList<>();
        for (Message last : rows) {
            int targetId = last.getFromId() == me.getId() ? last.getToId() : last.getFromId();
            Map<String,Object> item = new HashMap<>();
            item.put("last", last);
            item.put("target", userService.findUserById(targetId));
            item.put("conversationId", last.getConversationId());
            item.put("unread", messageService.unreadCount(me.getId(), last.getConversationId()));
            data.add(item);
        }
        return Result.success(data);
    }

    /** 某会话的消息列表，并将发给我且未读的标记为已读 */
    @GetMapping("/letters")
    public Result letters(@RequestParam("conversationId") String conversationId,
                          @RequestParam(name = "limit",  defaultValue = "50") int limit,
                          @RequestParam(name = "offset", defaultValue = "0")  int offset) {
        User me = hostHolder.getUser();
        if (me == null) return Result.error(403, "未登录");
        List<Message> list = messageService.letters(conversationId, limit, offset);
        messageService.markRead(me.getId(), conversationId);
        return Result.success(list);
    }

    /** 发送站内信（保持你原有的路由也能兼容） */
    @PostMapping("/letter/send")
    public Result send(@RequestBody Map<String,String> body) {
        User me = hostHolder.getUser();
        if (me == null) return Result.error(403, "未登录");
        String toName  = body.get("toName");
        String content = body.get("content");
        if (toName == null || content == null || content.trim().isEmpty()) {
            return Result.error(400, "参数不完整");
        }
        User to = userService.findUserByName(toName);
        if (to == null) return Result.error(404, "收信人不存在");

        Message m = messageService.send(me.getId(), to.getId(), content);
        Map<String,Object> resp = new HashMap<>();
        resp.put("message", m);
        resp.put("conversationId", m.getConversationId());
        return Result.success(resp);
    }

    /** 获取总的未读消息数 */
    @GetMapping("/unread-count")
    public Result unreadCount() {
        User me = hostHolder.getUser();
        if (me == null) return Result.success(0);
        
        // 计算所有会话的未读消息总数
        List<Message> conversations = messageService.conversations(me.getId());
        int totalUnread = 0;
        for (Message last : conversations) {
            totalUnread += messageService.unreadCount(me.getId(), last.getConversationId());
        }
        return Result.success(totalUnread);
    }
}
