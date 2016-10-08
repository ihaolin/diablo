package me.hao0.diablo.server;

import me.hao0.common.model.Page;
import me.hao0.diablo.server.model.PushLog;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.PushLogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DiabloServerTest.class)
public class PushLogServiceTests {

    @Autowired(required = false)
    private PushLogService pushLogService;

    @Test
    public void testAddPushLog(){
        PushLog log;
        for (int i=0; i<26; i++){
            log = new PushLog();
            log.setAppId(1L);
            log.setServer("127.0.0.1:12345");
            log.setClient("127.0.0.1:3311");
            log.setConfig("test_config"+i);
            pushLogService.add(log);
        }
    }

    @Test
    public void testPagingPushLog(){
        Response<Page<PushLog>> pagingResp = pushLogService.pagingConfigPushLog(1L, 1, 10);
        assertTrue(pagingResp.isSuccess());
        assertEquals(10, pagingResp.getData().getData().size());

        pagingResp = pushLogService.pagingConfigPushLog(1L, 3, 10);
        assertTrue(pagingResp.isSuccess());
        assertEquals(6, pagingResp.getData().getData().size());
    }
}
