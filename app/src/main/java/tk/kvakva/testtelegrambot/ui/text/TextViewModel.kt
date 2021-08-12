package tk.kvakva.testtelegrambot.ui.text

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import tk.kvakva.testtelegrambot.R
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private const val TAG = "TextViewModel"
private const val TELEGRAM_URL = "https://api.telegram.org"

class TextViewModel(private val appl: Application) : AndroidViewModel(appl) {


    private val _text = MutableLiveData<String>().apply {
        value = "This is text Fragment"
    }
    val text: LiveData<String> = _text

    fun getMeTextFromTelega() {
        Log.i(TAG, "sendTextToTelega")
        viewModelScope.launch(Dispatchers.IO) {
            val r = PreferenceManager.getDefaultSharedPreferences(appl)
                .getString(appl.resources.getString(R.string.tbtoken), "")?.let {
                    retrofitService.getMe(it)
                }
            val jrs = r?.let { respgeted(it) }
            _text.postValue(jrs)
        }
    }

    fun sendTextToTelega(s: String) {
        Log.i(TAG, "sendTextToTelega: $s")
        viewModelScope.launch(Dispatchers.IO) {
            val r = PreferenceManager.getDefaultSharedPreferences(appl)
                .getString(appl.resources.getString(R.string.tbtoken), "")?.let { _token ->
                    PreferenceManager.getDefaultSharedPreferences(appl)
                        .getString(appl.resources.getString(R.string.chatId), "")?.let { _chat_Id ->
                            retrofitService.sendMessageToTlg(
                                _token, _chat_Id, s
                            )
                        }
                }
            val jrs = r?.let { respgeted(it) }
            _text.postValue(jrs)
        }
    }

}

fun respgeted(r: Response<ResponseBody>) :String = if (r.isSuccessful) {
    val b = r.body()?.string() ?: "qweqwe"
    val json = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    val q = try {
        json.toJson(JsonParser.parseString(b))
    } catch (e: JsonSyntaxException) {
        b
    }
    q
} else {
    val b = r.errorBody()?.string() ?: "Error body qweqwe"
    val json = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    val q = try {
        json.toJson(JsonParser.parseString(b))
    } catch (e: JsonSyntaxException) {
        b
    }
    q
}

interface TelegramBotApiService {
    @GET("/bot{token}/getMe")
    suspend fun getMe(
        @Path("token") token: String
    ): Response<ResponseBody>

    @GET("/bot{token}/sendMessage")
    suspend fun sendMessageToTlg(
        @Path("token") token: String,
        @Query("chat_id") chat_Id: String,
        @Query("text") textMess: String
    ): Response<ResponseBody>

    @Multipart
    @POST("/bot{token}/sendPhoto")
    suspend fun sendPhoto(
        @Path("token") token: String,
        @Query("chat_id") chat_Id: String,
        @Part part1: MultipartBody.Part,
        @Part part2: MultipartBody.Part
    ): Response<ResponseBody>

    /*@GET("info/serviceList")
    suspend fun getBeeOptions(@Query("ctn") number: String, @Query("token") token: String):
            BeeOptionsData*/
}

val singlRetrofit: Retrofit by lazy {
    Retrofit.Builder()
        //.addConverterFactory(MoshiConverterFactory.create(moshi))
        //  .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(
            OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .callTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.HEADERS
                    }
                )
                .build()
        )
        .baseUrl(TELEGRAM_URL)
        .build()
}

val retrofitService: TelegramBotApiService by lazy {
    singlRetrofit.create(TelegramBotApiService::class.java)
}
