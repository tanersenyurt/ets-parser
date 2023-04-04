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

#### Sample Data
| id | hotelId | name | discount | score | comments\_count | worth | price | boardType | theme | hotelType | country | city | state | facilities | campaignName | lat | lon | hotelUrl | imageUrl | source | fromDate | toDate | md5 | dateCreated |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 126 | RAMRES | Ramada Resort Lara | 28 | 9.4 | 481 | 53264 | 38350.08 | Ultra Her Şey Dahil | Mavi Bayrak \| Balayı Oteli | TKOY | Türkiye | Antalya | Lara-Kundu |  Çocuk Oyun Parkı \| Bebek Arabası \| Masaj | Erken Rezervasyon Otelleri | 36.857633 | 30.869556 | https://www.etstur.com/Ramada-Resort-Lara?check\_in=17.07.2023&check\_out=25.07.2023&adult\_1=2 | https://images.etstur.com/files/images/hotelImages/TR/51672/m/Ramada-Resort-Lara-Genel-121263.jpg | ets | 17.07.2023 | 25.07.2023 | 18373211c89bfa6115921ba59d548037 | 2023-04-04 00:00:00.000000 |


#### License

This code is released under the MIT license.
