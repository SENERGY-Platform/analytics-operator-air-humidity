# operator-weather

Takes temperature and humidity readings from an inside sensor and adds outside data from openweathermap.
Calculates the theoretic inside humidity after full air exchange for now and a future date.

## Inputs

* temp (float): Reading from an inside temperature sensor
* humidity (float): Reading from an inside humidity (rel.) sensor

## Outputs

* humidityAfterAir (float): Theoretic rel. humidity of the air after complete change of air in the room and
reheating/cooling the air to inside temperature
* humidityAfterAirTrend (float): Same as humidityAfterAir, but calculated based on a weather forecast.
* trendDate (string): Timestamp of the humidityAfterAirTrend
* insideHumidity (float): As read

## Configs

* units (String): Either 'metric' or 'imperial', should match input data, default: 'metric'
* city (String): City, where input device is located. Can be extended by ISO 3166 country code, default: 'Leipzig'

## Build/Run

To build the Docker container and run the program you need to add a file called 'api-key' to the project root.
The first line of that file will be read and should only contain a valid openweathermap API key, which you can request 
free of charge at https://home.openweathermap.org/users/sign_up
