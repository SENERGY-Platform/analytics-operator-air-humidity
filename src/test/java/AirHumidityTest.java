import org.infai.seits.sepl.operators.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AirHumidityTest {

    @Test
    public void run() throws Exception{
        AirHumidity airHumidity = new AirHumidity();
        List<Message> messages = TestMessageProvider.getTestMesssagesSet();
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            airHumidity.config(m);
            airHumidity.run(m);
            System.out.println(m.getMessageString());
            Assert.assertTrue(m.getMessageString().contains("humidityAfterAir"));
            Assert.assertTrue(m.getMessageString().contains("humidityAfterAirTrend"));
            Assert.assertTrue(m.getMessageString().contains("trendDate"));
            Assert.assertTrue(m.getMessageString().contains("insideHumidity"));
        }

    }

}
