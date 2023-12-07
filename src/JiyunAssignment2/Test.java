package JiyunAssignment2;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import org.junit.Test.*;
import org.junit.*;
import org.junit.Assert;
public class Test {
    @org.junit.Test
    public void JsonTest() throws IOException {
        String filePath = "src/JiyunAssignment2/test.json";
        String weatherString = ContentSender.convertFileToJSON(filePath);
        WeatherContent weatherContent= JSON.parseObject(weatherString, WeatherContent.class);
        Assert.assertEquals(  "IDS60901",weatherContent .getId());
        Assert.assertEquals(  "Adelaide", weatherContent . getName()) ;
        Assert.assertEquals( "15/04:00pm",weatherContent.getLocal_date_time());
        Assert.assertEquals( "20230715160000",weatherContent.getLocal_date_time_full());
    }
    @org.junit.Test
    public void LamportTest(){
        LamportClock lamportClock=new LamportClock();
        Assert.assertEquals(lamportClock.getTime(),0);
        Assert.assertEquals(lamportClock.tick(),1);
    }
}
