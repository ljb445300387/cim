package com.crossoverjie.cim.server.kit;

import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/8/24 01:37
 * @since JDK 1.8
 */
@Slf4j
@Component
public class ZkRegistryService {
    @Autowired
    private ZkService zkService;

}