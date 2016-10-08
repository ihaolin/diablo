package me.hao0.diablo.server;

import me.hao0.diablo.server.model.App;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.AppService;
import me.hao0.diablo.server.support.Messages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DiabloServerTest.class)
public class AppServiceTests {

    @Autowired
    private Messages messages;

    @Autowired(required = false)
    private AppService appService;

    @Test
    public void testSave(){
        App app = new App();
        app.setAppName("app_test");
        app.setAppKey("123456");
        app.setAppDesc("应用测试");
        Response<Long> saveResp = appService.save(app);
        assertTrue(saveResp.isSuccess());
    }

    @Test
    public void testSaves(){

        for (int i=0; i<105; i++){
            App app = new App();
            app.setAppName("app_test" + i);
            app.setAppKey("123456" + i);
            app.setAppDesc("应用测试" + i);
            Response<Long> saveResp = appService.save(app);
            assertTrue(saveResp.isSuccess());
        }
    }

    @Test
    public void testFindByName() throws IOException {
        Response<App> appResp = appService.findByName("app_test");
        assertTrue(appResp.isSuccess());
        assertNotNull(appResp.getData());
        System.in.read();
    }

    @Test
    public void testDelete(){
        Response<Boolean> delResp = appService.delete("app_test1");
        assertTrue(delResp.isSuccess());
    }

}
