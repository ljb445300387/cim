package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.client.CimClient;
import com.crossoverjie.cim.client.service.EchoService;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.MsgLogger;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.service.ShutDownMsg;
import com.crossoverjie.cim.common.data.construct.RingBufferWheel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:28
 * @since JDK 1.8
 */
@Service
public class ShutDownCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShutDownCommand.class);

    @Autowired
    private RouteRequest routeRequest ;

    @Autowired
    private CimClient cimClient;

    @Autowired
    private MsgLogger msgLogger;

    @Resource(name = "callBackThreadPool")
    private ThreadPoolExecutor executor;

    @Autowired
    private EchoService echoService ;


    @Autowired
    private ShutDownMsg shutDownMsg ;

    @Autowired
    private RingBufferWheel ringBufferWheel ;

    @Override
    public void process(String msg) {
        echoService.echo("cim client closing...");
        shutDownMsg.shutdown();
        routeRequest.offLine();
        msgLogger.stop();
        executor.shutdown();
        ringBufferWheel.stop(false);
        try {
            while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                echoService.echo("thread pool closing");
            }
            cimClient.close();
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException", e);
        }
        echoService.echo("cim close success!");
        System.exit(0);
    }
}
