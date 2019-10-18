package com.crossoverjie.cim.server;

import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.kit.RegistryZk;
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
    private AppConfiguration appConfiguration;

    @Value("${server.port}")
    private int httpPort;

    public static void main(String[] args) {
        SpringApplication.run(CimServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //获得本机IP
        String addr = InetAddress.getLocalHost().getHostAddress();
        Thread thread = new Thread(new RegistryZk(addr, appConfiguration.getCimServerPort(), httpPort));
        thread.setName("registry-zk");
        thread.start();
    }
}