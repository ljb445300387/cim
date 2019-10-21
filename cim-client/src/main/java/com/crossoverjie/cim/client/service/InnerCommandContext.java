package com.crossoverjie.cim.client.service;

import com.crossoverjie.cim.client.service.impl.command.PrintAllCommand;
import com.crossoverjie.cim.common.enums.SystemCommandEnum;
import com.crossoverjie.cim.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:39
 * @since JDK 1.8
 */
@Component
@Slf4j
public class InnerCommandContext {
    @Autowired
    private List<InnerCommand> innerCommandList;
    @Autowired
    private PrintAllCommand printAllCommand;

    public InnerCommand getCommand(String command) {
        String[] trim = command.trim().split(" ");
        //兼容需要命令后接参数的数据 :q cross
        String clazz = SystemCommandEnum.getAllClazz().get(trim[0]);
        if (StringUtil.isEmpty(clazz)) {
            return printAllCommand;
        }
        return innerCommandList.stream().filter(x -> x.getClass().getName().equals(clazz)).findAny().orElse(null);
    }

}
