package com.crossoverjie.cim.server;

import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.kit.ZkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;

/**
 * @author crossoverJie
 */
@Slf4j
@SpringBootApplication
public class CimServerApplication implements CommandLineRunner {
    @Autowired
    private AppConfiguration conf;
    @Autowired
    private ZkService zkService;

    @Value("${server.port}")
    private int httpPort;

    public static void main(String[] args) {
        SpringApplication.run(CimServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String addr = InetAddress.getLocalHost().getHostAddress();
        zkService.regist(addr, conf.getCimServerPort(), httpPort);
    }
}