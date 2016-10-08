package me.hao0.diablo.server;

import com.google.common.collect.Lists;
import me.hao0.common.model.Page;
import me.hao0.diablo.server.model.Config;
import me.hao0.diablo.server.model.Response;
import me.hao0.diablo.server.service.ConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DiabloServerTest.class)
public class ConfigServiceTests {

    @Autowired(required = false)
    private ConfigService configService;

    @Test
    public void testSave(){
        Response<Long> saveResp = configService.save(1L, "test", "test_value");
        assertTrue(saveResp.isSuccess());
    }

    @Test
    public void testSaves(){
        for (int i=0; i<100; i++){
            configService.save(1L, "test" + i, "test_value" + i);
        }
    }

    @Test
    public void testFindById() {
        Response<Config> configResp = configService.findById(11L);
        assertTrue(configResp.isSuccess());
        assertNotNull(configResp.getData());

        configResp = configService.findById(404L);
        assertTrue(configResp.isSuccess());
        assertNull(configResp.getData());
    }

    @Test
    public void testFindByName(){
        Response<Config> configResp = configService.findByName(1L, "test");
        assertTrue(configResp.isSuccess());
        assertNotNull(configResp.getData());
        System.err.println(configResp.getData());
    }

    @Test
    public void testFindByNames(){
        List<String> names = Lists.newArrayList("activityNo", "activityStart", "activityChannel", "timeInfo");
        Response<List<Config>> configsResp = configService.findByNames(1L, names);
        assertTrue(configsResp.isSuccess());
        assertTrue(configsResp.getData().size() == 2);
    }

    @Test
    public void testPagingConfig(){
        Response<Page<Config>> pageResp = configService.pagingConfig(1L, null, 1, 10);
        assertTrue(pageResp.isSuccess());
        System.err.println("total countByAppId: " + pageResp.getData().getTotal());
        for (Config config : pageResp.getData().getData()){
            System.err.println(config);
        }
    }

    @Test
    public void testDelete(){
        Response<Boolean> delResp = configService.delete(1L, "test0");
        assertTrue(delResp.isSuccess());
    }
}
