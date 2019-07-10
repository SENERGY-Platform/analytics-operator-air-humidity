# operator-weather

Takes temperature and humidity readings from an inside sensor and adds outside data from openweathermap.

## Inputs

* temp (float): Reading from an inside temperature sensor
* humidity (float): Reading from an inside humidity (rel.) sensor

## Outputs

* deviceTemp (float): As read from input
* deviceHumidity (float): As read from input
* webTemp (float): Current temperature of specified location
* webHumidity (float): Current humidity (rel.) of specified location

## Configs

* units (String): Either 'metric' or 'imperial', should match input data, default: 'metric'
* city (String): City, where input device is located. Can be extended by ISO 3166 country code, default: 'Leipzig'

## Build/Run

To build the Docker container and run the program you need to add a file called 'api-key' to the project root.
The first line of that file will be read and should only contain a valid openweathermap API key, which you can request 
free of charge at https://home.openweathermap.org/users/sign_up
