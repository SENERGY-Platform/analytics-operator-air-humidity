import org.infai.seits.sepl.operators.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class WeatherTest {

    @Test
    public void run() throws Exception{
        Weather weather = new Weather();
        List<Message> messages = TestMessageProvider.getTestMesssagesSet();
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            weather.config(m);
            weather.run(m);
            Assert.assertTrue(m.getMessageString().contains("deviceTemp"));
            Assert.assertTrue(m.getMessageString().contains("deviceHumidity"));
            Assert.assertTrue(m.getMessageString().contains("webTemp"));
            Assert.assertTrue(m.getMessageString().contains("webHumidity"));
        }

    }

}
