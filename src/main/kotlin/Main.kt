package com.tsenyurt.parser

import com.tsenyurt.parser.Holiday.Companion.ETS_FETCH_URL
import com.tsenyurt.parser.Holiday.Companion.FILTER
import com.tsenyurt.parser.Holiday.Companion.FROM
import com.tsenyurt.parser.Holiday.Companion.OFFSET
import com.tsenyurt.parser.Holiday.Companion.PERSON_COUNT
import com.tsenyurt.parser.Holiday.Companion.SEARCH_ID
import com.tsenyurt.parser.Holiday.Companion.TO
import kotlinx.coroutines.*
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.logging.ConsoleLogger
import me.liuwj.ktorm.logging.LogLevel
import me.liuwj.ktorm.schema.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDate
import java.util.UUID

val database =
    Database.connect(System.getProperty("DB_URL"), user = System.getProperty("DB_USER"), password = System.getProperty("DB_PASS"), logger = ConsoleLogger(threshold = LogLevel.INFO))
suspend fun callApi(url: String, from: String, to: String) {
    val response = withContext(Dispatchers.IO) {
        Jsoup.connect(url).get()
    }
    response.select("div.hotel-row-item").forEachIndexed { index, element ->
        val hotelName = HotelData.HOTEL_NAME.getVal(element)
        val score = HotelData.SCORE.getVal(element,"Double")
        val discount = HotelData.DISCOUNT_RATE.getVal(element,"Int")
        val hotelUrl = Holiday.ETS_BASE_URL + element.select(".ecommerce-push").attr("href")
        val imageUrl = element.select(".destination-image").attr("src")
        val source = "ets"
        val md5String = "${hotelName}${source}${from}${to}"
        val md5 = md5(md5String)

        database.useTransaction {
            val query = database.from(HotelDB).select(HotelDB.id).where  { HotelDB.md5 eq md5 }
            if(query.totalRecords == 0)
            {
                database.insert(HotelDB) {
                    set(it.name, HotelData.HOTEL_NAME.getVal(element))
                    set(it.score, HotelData.SCORE.getVal(element,"Double"))
                    set(it.discount, HotelData.DISCOUNT_RATE.getVal(element,"Int"))
                    set(it.comments_count, HotelData.TOTAL_COMMENTS.getVal(element,"Int"))
                    set(it.worth, HotelData.BASEPRICE.getVal(element,"Double"))
                    set(it.price, HotelData.PRICE.getVal(element,"Double"))
                    set(it.source, "ets")
                    set(it.fromDate, from)
                    set(it.toDate, to)
                    set(it.md5, md5)
                    set(it.dateCreated, LocalDate.now())
                    set(it.hotelUrl, hotelUrl)
                    set(it.imageUrl, imageUrl)
                    set(it.hotelId, HotelData.HOTEL_ID.getVal(element))
                    set(it.campaignName, HotelData.SUGGESTION_NAME.getVal(element))
                    set(it.boardType, HotelData.BOARD_TYPE.getVal(element))
                    set(it.hotelType, HotelData.HOTEL_TYPE.getVal(element))
                    set(it.country, HotelData.COUNTRY.getVal(element))
                    set(it.city, HotelData.CITY.getVal(element))
                    set(it.state, HotelData.STATE.getVal(element))
                    set(it.theme, HotelData.HOTEL_THEMES.getVal(element))
                    set(it.facilities, HotelData.HOTEL_FACILITIES.getVal(element))
                    set(it.lat, HotelData.LAT.getVal(element))
                    set(it.lon, HotelData.LON.getVal(element))
                    println("Hotel[name=$hotelName, score=${score}, discount=${discount}]")
                }
            }

        }
    }
}
suspend fun main(args: Array<String>) {
    runBlocking {
        val maxJobs = 15 // maximum number of concurrent coroutines
        val fromDate = "17.07.2023"
        val toDate = "25.07.2023"
        val filters = HolidayType.values().toList()//listOf(HolidayType.ULTRA_ALL_INCLUSIVE,HolidayType.ALL_INCLUSIVE)
        val jobList = mutableListOf<Job>()

        for (i in 0..6000 step 20) {
            val urlToCall = "$ETS_FETCH_URL"
                .replace(FROM,fromDate)
                .replace(TO,toDate)
                .replace(OFFSET,"$i")
                .replace(FILTER, getFilterStringIfAvailable(filters))
                .replace(SEARCH_ID, UUID.randomUUID().toString())
                .replace(PERSON_COUNT,"2")
            if (jobList.size >= maxJobs) {
                // println("**---${jobList.first()}")
                jobList.first().join()
                jobList.removeAt(0)
            }
            jobList.add(launch {
                // println("   'This thread is running now : ${Thread.currentThread().name}")
                callApi(urlToCall, fromDate, toDate)
            })
        }
        jobList.forEach { it.join() }
    }
}

fun getFilterStringIfAvailable(filters: List<HolidayType>):String {
    if (filters.isEmpty()) return ""
    return "&filters=${HolidayType.getUrlCodes(filters)}"
}

fun HotelData.getVal(element: Element, type: String = ""): Any {
    return when (type) {
        "Int" -> element.attr(this.dataName).ifEmpty { "0" }.toInt()
        "Double" -> element.attr(this.dataName).ifEmpty { "0.0" }.toDouble()
        else -> element.attr(this.dataName)
    }
}
enum class HolidayType(val type: String){

    ULTRA_ALL_INCLUSIVE("UAI"),
    ALL_INCLUSIVE("AI"),
    BED_BREAKFAST("BB"),
    HALF_BOARD("HB"),
    FULL_BOARD("FB"),
    NON_ALCOHOLIC_ALL_INCLUSIVE("AIWA"),
    ROOM_ONLY("RO")
    ;

    companion object {
        const val TYPE_PREFIX = "BoardTypeFilter--"
        fun getUrlCodes(list: List<HolidayType>): String {
            return list.joinToString { it.getUrlCode(it) }.replace(" ", "")
        }
    }

    fun getUrlCode(holidayType: HolidayType): String {
        return "$TYPE_PREFIX${holidayType.type}"
    }
}

class Holiday{
    companion object{
        const val ETS_BASE_URL = "https://www.etstur.com/"
        const val ETS_FETCH_URL = "https://www.etstur.com/ajax/hotel-search-load-more?url=Erken-Rezervasyon-Otelleri&check_in=[FROM]&check_out=[TO]&adult_1=[PERSON_COUNT]&minPrice=0&maxPrice=0&loadMore=true[FILTER]&sortType=popular&sortDirection=desc&limit=20&offset=[OFFSET]&totalHotelsWithPriceOnPage=20&hasBannerAdded=true&searchId=[SEARCH_ID]"
        const val FROM = "[FROM]"
        const val TO = "[TO]"
        const val FILTER = "[FILTER]"
        const val OFFSET = "[OFFSET]"
        const val SEARCH_ID = "[SEARCH_ID]"
        const val PERSON_COUNT = "[PERSON_COUNT]"
    }
}

enum class HotelData(val dataName: String){
    PRICE("data-price") ,
    HOTEL_ID("data-hotelid"),
    HOTEL_NAME("data-hotelname"),
    SUGGESTION_NAME("data-suggestionname"),
    BOARD_TYPE("data-boardtype"),
    HOTEL_TYPE("data-hoteltype"),
    BASEPRICE("data-baseprice"),
    DISCOUNT_RATE("data-discountrate"),
    TOTAL_COMMENTS("data-totalcomments"),
    COUNTRY("data-country"),
    CITY("data-city"),
    STATE("data-state"),
    SCORE("data-score"),
    HOTEL_THEMES("data-hotelthemes"),
    HOTEL_FACILITIES("data-hotelfacilities"),
    LAT("data-lat"),
    LON("data-lon");
}


object HotelDB : Table<Nothing>("hotel") {
    val id = int("id").primaryKey()
    var name= varchar("name")
    var discount= int("discount")
    var score= double("score")
    var comments_count= int("comments_count")
    var worth= double("worth")
    var price= double("price")
    var source= varchar("source")
    var fromDate= varchar("fromDate")
    var toDate= varchar("toDate")
    var md5= varchar("md5")
    var dateCreated= date("dateCreated")
    val hotelUrl = varchar("hotelUrl")
    val imageUrl = varchar("imageUrl")
    val hotelId = varchar("hotelId")
    val campaignName = varchar("campaignName")
    val boardType = varchar("boardType")
    val hotelType = varchar("hotelType")
    val country = varchar("country")
    val city = varchar("city")
    val state = varchar("state")
    val theme = varchar("theme")
    val facilities = varchar("facilities")
    val lat = varchar("lat")
    val lon = varchar("lon")
}

fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

