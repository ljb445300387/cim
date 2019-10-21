package com.crossoverjie.cim.client;

import com.crossoverjie.cim.client.scanner.Scan;
import com.crossoverjie.cim.client.service.impl.ClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@SpringBootApplication
@Slf4j
public class CimClientApplication implements CommandLineRunner {

    @Autowired
    private Scan scan;

    @Autowired
    private ClientInfo clientInfo;

    public static void main(String[] args) {
        SpringApplication.run(CimClientApplication.class, args);
        log.info("启动 Client 服务成功");
    }

    @Override
    public void run(String... args) {
        scan.run();
        clientInfo.saveStartDate();
    }
}