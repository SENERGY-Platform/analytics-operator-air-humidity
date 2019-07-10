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
            Assert.assertTrue(m.getMessageString().contains("device-temp"));
            Assert.assertTrue(m.getMessageString().contains("device-humidity"));
            Assert.assertTrue(m.getMessageString().contains("web-temp"));
            Assert.assertTrue(m.getMessageString().contains("web-humidity"));
        }

    }

}
