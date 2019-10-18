package com.crossoverjie.cim.route.kit;

import com.crossoverjie.cim.route.config.AppConfiguration;
import com.crossoverjie.cim.route.util.SpringBeanFactory;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 00:35
 * @since JDK 1.8
 */
public class ServerListListener implements Runnable {
    private ZkKitService zkUtil;
    private AppConfiguration conf;

    public ServerListListener() {
        zkUtil = SpringBeanFactory.getBean(ZkKitService.class);
        conf = SpringBeanFactory.getBean(AppConfiguration.class);
    }

    @Override
    public void run() {
        //注册监听服务
        zkUtil.subscribeEvent(conf.getZkRoot());

    }
}
