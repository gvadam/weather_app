package com.example.weatherapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import java.lang.Math.abs
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    lateinit var background: ScrollView
    private val city: String = "miami"
    private val api: String = "7b40978d6a362fb4cd4768a5bffed1d8" // Use API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        background = findViewById(R.id.background)

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            background.visibility = View.GONE
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
            val response:String? = try{
                URL(
                    "https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${api}&units=imperial")
                    .readText(Charsets.UTF_8)
                } catch (e: Exception){
                    Log.d("TAG", "api not working")
                    null
                }
            val response2:String? = try{
                URL(
                    "https://api.openweathermap.org/data/2.5/forecast?q=${city}&appid=${api}&units=imperial")
                    .readText(Charsets.UTF_8)
                } catch (e: Exception){
                    Log.d("TAG", "api not working")
                    null
                }
            handler.post {
                try {
                    val jsonObj = JSONObject(response)

                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                    val updatedAt:Long = jsonObj.getLong("dt")
                    val updatedAtText = SimpleDateFormat("EEE, hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                    val temp = main.getString("temp")+"°F"
                    val tempMin = "Min: " + main.getString("temp_min") + "°F"
                    val tempMax = "Max: " + main.getString("temp_max") + "°F"
                    val pressure = main.getString("pressure") + " inHG"
                    val humidity = main.getString("humidity") + "%"
                    val feelLike = "Feels like " + main.getString("feels_like") + "°F"

                    val sunrise:Long = sys.getLong("sunrise")
                    val sunset:Long = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed") + " mph"
                    val weatherDescription = weather.getString("description")

                    val address = jsonObj.getString("name")+", "+sys.getString("country")

                    val sunriseTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                    val sunsetTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                    val weatherStr = weather.getString("main")

                    findViewById<TextView>(R.id.address).text = address
                    findViewById<TextView>(R.id.time).text =  updatedAtText
                    findViewById<TextView>(R.id.status).text = weatherDescription
                    findViewById<TextView>(R.id.temp).text = temp
                    findViewById<TextView>(R.id.feels_like).text = feelLike
                    findViewById<TextView>(R.id.temp_min).text = tempMin
                    findViewById<TextView>(R.id.temp_max).text = tempMax
                    findViewById<TextView>(R.id.sunrise).text = sunriseTime
                    findViewById<TextView>(R.id.sunset).text = sunsetTime
                    findViewById<TextView>(R.id.wind).text = windSpeed
                    findViewById<TextView>(R.id.pressure).text = pressure
                    findViewById<TextView>(R.id.humidity).text = humidity

                    val currentTime = SimpleDateFormat("kk", Locale.ENGLISH).format(Date(sunrise*1000)).toInt() * 60 + SimpleDateFormat("mm", Locale.ENGLISH).format(Date(sunrise*1000)).toInt()
                    val sunrise2 = SimpleDateFormat("kk", Locale.ENGLISH).format(Date(sunrise*1000)).toInt() * 60 + SimpleDateFormat("mm", Locale.ENGLISH).format(Date(sunrise*1000)).toInt()
                    val sunset2 = SimpleDateFormat("kk", Locale.ENGLISH).format(Date(sunset*1000)).toInt() * 60 + SimpleDateFormat("mm", Locale.ENGLISH).format(Date(sunset*1000)).toInt()
                    val noon = 720

                    when{
                        weatherStr == "Rain" || weatherStr == "Clouds" || weatherStr == "Drizzle" -> {
                            background.background = resources.getDrawable(R.drawable.cloudy_bg)
                        }
                        currentTime in sunrise2 until noon -> {
                            background.background = resources.getDrawable(R.drawable.morning_bg)
                        }
                        currentTime in noon until sunset2 -> {
                            background.background = resources.getDrawable(R.drawable.afternoon_bg)
                        }
                        abs(currentTime - sunset2) < 60 -> {
                            background.background = resources.getDrawable(R.drawable.sunset_bg)
                        }
                        else -> {
                            background.background = resources.getDrawable(R.drawable.night_bg)
                        }
                    }

                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    background.visibility = View.VISIBLE
                } catch (e: Exception) {
                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
                }

                try {
                    val jsonObject2 = JSONObject(response2)

                    val day1 = jsonObject2.getJSONArray("list").getJSONObject(0).getJSONObject("main")
                    val temp1: String = day1.getLong("temp").toString() + "°F"
                    val date1: Long = jsonObject2.getJSONArray("list").getJSONObject(0).getLong("dt")
                    val date = SimpleDateFormat("EEE", Locale.ENGLISH).format(Date(date1 * 1000))
                    findViewById<TextView>(R.id.forecast_temp1).text = temp1
                    findViewById<TextView>(R.id.forecast_date1).text = date

                    /**
                    var cur: Int = 1
                    val max: Int = 5
                    var i: Int = 1
                    var prevDate = date

                    while (cur != max) {
                        val x = jsonObject2.getJSONArray("list").getJSONObject(i).getLong("dt")
                        val currDate = SimpleDateFormat("EEE", Locale.ENGLISH).format(Date(date1 * 1000))
                        if(prevDate != currDate){
                            prevDate = currDate
                        }
                        i++
                    }*/

                    val day2 = jsonObject2.getJSONArray("list").getJSONObject(3).getJSONObject("main")
                    val temp2: String = day2.getLong("temp").toString() + "°F"
                    findViewById<TextView>(R.id.forecast_temp2).text = temp2
                    val date2: Long  = jsonObject2.getJSONArray("list").getJSONObject(3).getLong("dt")
                    findViewById<TextView>(R.id.forecast_date2).text = SimpleDateFormat("EEE", Locale.ENGLISH).format(Date(date2 * 1000))


                    val day3 = jsonObject2.getJSONArray("list").getJSONObject(10).getJSONObject("main")
                    val temp3: String = day3.getLong("temp").toString() + "°F"
                    findViewById<TextView>(R.id.forecast_temp3).text = temp3
                    val date3: Long  = jsonObject2.getJSONArray("list").getJSONObject(10).getLong("dt")
                    findViewById<TextView>(R.id.forecast_date3).text = SimpleDateFormat("EEE", Locale.ENGLISH).format(Date(date3 * 1000))


                    val day4 = jsonObject2.getJSONArray("list").getJSONObject(18).getJSONObject("main")
                    val temp4: String = day4.getLong("temp").toString() + "°F"
                    findViewById<TextView>(R.id.forecast_temp4).text = temp4
                    val date4: Long  = jsonObject2.getJSONArray("list").getJSONObject(18).getLong("dt")
                    findViewById<TextView>(R.id.forecast_date4).text = SimpleDateFormat("EEE", Locale.ENGLISH).format(Date(date4 * 1000))

                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    background.visibility = View.VISIBLE
                } catch (e: Exception) {
                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
                }

            }
        }

    }

}
