package com.crossoverjie.cim.client.config;

import com.crossoverjie.cim.client.handler.MsgHandleCaller;
import com.crossoverjie.cim.client.service.impl.MsgCallBackListener;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.data.construct.RingBufferWheel;
import com.crossoverjie.cim.common.protocol.CimRequestProto.CimReqProtocol;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * Function:bean 配置
 *
 * @author crossoverJie
 *         Date: 24/05/2018 15:55
 * @since JDK 1.8
 */
@Configuration
public class BeanConfig {
    @Value("${cim.user.id}")
    private long userId;

    @Value("${cim.callback.thread.queue.size}")
    private int queueSize;

    @Value("${cim.callback.thread.pool.size}")
    private int poolSize;


    /**
     * 创建心跳单例
     * @return
     */
    @Bean(value = "heartBeat")
    public CimReqProtocol heartBeat() {
        return CimReqProtocol.newBuilder()
                .setRequestId(userId)
                .setReqMsg("ping")
                .setType(Constants.CommandType.PING)
                .build();
    }


    /**
     * http client
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }


    /**
     * 创建回调线程池
     * @return
     */
    @Bean("callBackThreadPool")
    public ThreadPoolExecutor buildCallerThread(){
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue(queueSize);
        ThreadFactory product = new ThreadFactoryBuilder()
                .setNameFormat("msg-callback-%d")
                .setDaemon(true)
                .build();
        return new ThreadPoolExecutor(poolSize, poolSize, 1, TimeUnit.MILLISECONDS, queue,product);
    }


    @Bean("scheduledTask")
    public ScheduledExecutorService buildSchedule(){
        ThreadFactory sche = new ThreadFactoryBuilder()
                .setNameFormat("scheduled-%d")
                .setDaemon(true)
                .build();
        return new ScheduledThreadPoolExecutor(1,sche);
    }

    @Bean
    public RingBufferWheel bufferWheel(){
        ExecutorService executorService = Executors.newFixedThreadPool(2) ;
        return new RingBufferWheel(executorService) ;
    }

}
