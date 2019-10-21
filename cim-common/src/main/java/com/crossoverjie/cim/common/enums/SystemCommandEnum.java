package com.crossoverjie.cim.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/26 18:38
 * @since JDK 1.8
 */
@AllArgsConstructor
public enum SystemCommandEnum {

    ALL(":all       ", "获取所有命令", "PrintAllCommand"),
    ONLINE_USER(":olu       ", "获取所有在线用户", "PrintOnlineUsersCommand"),
    QUIT(":q!        ", "退出程序", "ShutDownCommand"),
    QUERY(":q         ", "【:q 关键字】查询聊天记录", "QueryHistoryCommand"),
    AI(":ai        ", "开启 AI 模式", "OpenAIModelCommand"),
    QAI(":qai       ", "关闭 AI 模式", "CloseAIModelCommand"),
    PREFIX(":pu        ", "模糊匹配用户", "PrefixSearchCommand"),
    EMOJI(":emoji     ", "emoji 表情列表", "EmojiCommand"),
    INFO(":info      ", "获取客户端信息", "EchoInfoCommand"),
    DELAY_MSG(":delay     ", "delay message, :delay [msg] [delayTime]", "DelayMsgCommand");
    @Setter
    @Getter
    private String commandType;
    @Setter
    @Getter
    private String desc;
    @Setter
    @Getter
    private String clazz;

    public static Map<String, String> getAllStatusCode() {
        Map<String, String> map = new HashMap<String, String>(16);
        for (SystemCommandEnum status : values()) {
            map.put(status.getCommandType(), status.getDesc());
        }
        return map;
    }

    public static Map<String, String> getAllClazz() {
        Map<String, String> map = new HashMap<String, String>(16);
        for (SystemCommandEnum status : values()) {
            map.put(status.getCommandType().trim(), "com.crossoverjie.cim.client.service.impl.command." + status.getClazz());
        }
        return map;
    }

}