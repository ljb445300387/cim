package com.crossoverjie.cim.server.kit;

import com.crossoverjie.cim.server.config.AppConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Function: Zookeeper 工具
 *
 * @author crossoverJie
 * Date: 2018/8/19 00:33
 * @since JDK 1.8
 */
@Component
@Slf4j
public class ZkService {
    @Autowired
    private ZkClient zkClient;
    @Autowired
    private AppConfiguration conf;

    /**
     * 创建父级节点
     */
    public void createRootNode() {
        if (zkClient.exists(conf.getZkRoot())) {
            return;
        }
        //创建 root
        zkClient.createPersistent(conf.getZkRoot());
    }

    /**
     * 写入指定节点 临时目录
     *
     * @param path
     */
    public void createNode(String path) {
        zkClient.createEphemeral(path);
    }

    public void regist(String ip, int cimServerPort, int httpPort) {
        //创建父节点
        this.createRootNode();
        //是否要将自己注册到 ZK
        if (conf.isZkSwitch()) {
            String path = String.format("%s/ip-%s:%d:%d",
                    conf.getZkRoot(),
                    ip,
                    cimServerPort,
                    httpPort);
            this.createNode(path);
            log.info("注册 zookeeper 成功，msg=[{}]", path);
        }
    }
}
