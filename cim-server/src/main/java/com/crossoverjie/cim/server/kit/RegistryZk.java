package com.crossoverjie.cim.server.kit;

import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/8/24 01:37
 * @since JDK 1.8
 */
@Slf4j
public class RegistryZk implements Runnable {
    private ZkService zkService;
    private AppConfiguration appConfiguration;
    private String ip;
    private int cimServerPort;
    private int httpPort;

    public RegistryZk(String ip, int cimServerPort, int httpPort) {
        this.ip = ip;
        this.cimServerPort = cimServerPort;
        this.httpPort = httpPort;
        zkService = SpringBeanFactory.getBean(ZkService.class);
        appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class);
    }

    @Override
    public void run() {

        //创建父节点
        zkService.createRootNode();

        //是否要将自己注册到 ZK
        if (appConfiguration.isZkSwitch()) {
            String path = String.format("%s/ip-%s:%d:%d", appConfiguration.getZkRoot(), ip, cimServerPort, httpPort);
            zkService.createNode(path);
            log.info("注册 zookeeper 成功，msg=[{}]", path);
        }


    }
}