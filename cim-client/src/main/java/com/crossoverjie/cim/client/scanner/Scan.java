package com.crossoverjie.cim.client.scanner;

import com.crossoverjie.cim.client.service.EchoService;
import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.service.MsgLogger;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/21 16:44
 * @since JDK 1.8
 */
@Component
public class Scan implements Runnable {

    @Autowired
    private MsgHandle msgHandle;

    @Autowired
    private MsgLogger msgLogger;

    @Autowired
    private EchoService echoService;

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String msg = sc.nextLine();

            //检查消息
            if (msgHandle.checkMsg(msg)) {
                continue;
            }

            //系统内置命令
            if (msgHandle.innerCommand(msg)) {
                continue;
            }

            //真正的发送消息
            msgHandle.sendMsg(msg);

            //写入聊天记录
            msgLogger.log(msg);

            echoService.echo(EmojiParser.parseToUnicode(msg));
        }
    }

}
