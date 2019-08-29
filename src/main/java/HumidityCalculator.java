import static java.lang.Math.exp;

public class HumidityCalculator {
    public static double calculateHumidity(double temp1, double hum1, double temp2, double hum2) {
        return calculateHumidity(temp1, temp2, hum2, false);
    }

    public static double calculateHumidity(double temp1, double temp2, double hum2, boolean needsConversion) {

        if(needsConversion) {
            temp1 = metric(temp1);
            temp2 = metric(temp2);
        }

        if(hum2 > 1) {
            //Expect reading in range 0-100 instead 0-1
            hum2 /= 100;
        }

        double e_satInside = e_sat(temp1);
        double e_satOutside = e_sat(temp2);

        double eOutside = e(hum2, e_satOutside);

        double p_dExpected = p_d(eOutside, temp1);
        double p_d_maxInside = p_d(e_satInside, temp1);

        double expected = p_dExpected / p_d_maxInside;

        return expected;
    }



    protected static double e_sat(double temp) {
        return 611200 * exp((17.62 * temp) / (243.12 + temp));
    }

    protected static double e(double humidity, double e_sat) {
        return humidity * e_sat;
    }

    protected static double p_d(double e, double temp){
        return e / (461.51 * (temp + 273.15));
    }

    protected static double metric(double imperial) {
        return (imperial - 32) * 5 / 9;
    }
}
