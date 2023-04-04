# NOTES for DEVs
Purpose of the project selecting Holiday easily with sql like

`SELECT t.*
FROM holiday.hotel t
WHERE score>8.7 and price<35000  and discount>18 and comments_count>300
ORDER BY price DESC;`

### Description
This is a Kotlin code that connects to a URL using JSoup, fetches data, and inserts it into a database table. The code uses coroutines to make multiple requests concurrently. It fetches data for a specific date range and filters for a specific type of holiday.

The Holiday class contains constant URLs, and HolidayType is an enum class that represents different types of holidays. The callApi function is used to make the API call and parse the response. It then inserts the parsed data into a SQLite database using the HotelDB class.

The main function launches a coroutine for each request, limiting the number of concurrent requests to a maximum of 15.

#### Before Running
The code requires mariaDB (if you want another db just update the dependency) and passing three VM parameter like
`-DDB_USER="user"
-DDB_PASS="pass"
-DDB_URL="jdbc:mariadb://xxx/holiday"`

than for creating required tables flyway migration should be run like below

     gradle flywayMigrate

after migration done, you are good to go, you can fetch hotel information

#### Dependencies

JSoup to parse the HTML response from the server.
Kotlin Coroutines to make asynchronous requests.

#### Usage

To use this code, you need to replace the FROM and TO dates in the main function with the desired dates. You can also specify the type of holiday by modifying the filters list.

This code can be used as a starting point for building an application that fetches and stores data from the ETS website. You will need to modify the code to suit your specific requirements.

![queried_hotels.png](src%2Fmain%2Fresources%2Fassets%2Fqueried_hotels.png)

#### License

This code is released under the MIT license.
