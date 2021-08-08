package tk.kvakva.testtelegrambot.ui.text

import android.app.Application
import android.content.Context
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
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.http.GET
import tk.kvakva.testtelegrambot.R
import retrofit2.Retrofit
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val TAG = "TextViewModel"
private const val TELEGRAM_URL = "https://api.telegram.org/bot"

class TextViewModel(private val appl: Application) : AndroidViewModel(appl) {


    private val _chatid = MutableLiveData<String>().apply {
        value = PreferenceManager.getDefaultSharedPreferences(appl)
            .getString(appl.resources.getString(R.string.chatId), "")
    }
    private val chatid: LiveData<String> = _chatid

    private val _text = MutableLiveData<String>().apply {
        value = "This is text Fragment"
    }
    val text: LiveData<String> = _text

    init {
        Log.i(TAG, "_tbtoken: ${(appl as App).telBotApi.tbtoken.value} **")
        Log.i(TAG, "_chatid: ${chatid.value} **")

        PreferenceManager.getDefaultSharedPreferences(appl)
            .registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                when (key) {
                    appl.resources.getString(R.string.tbtoken) -> {
                        sharedPreferences.getString(
                            appl.resources.getString(R.string.tbtoken),
                            ""
                        )?.let {
                            appl.telBotApi.setTbtoken(it)
                            appl.telBotApi.setRetr(it)
                        }
                        Log.i(TAG, "prefchlist: _tbtoken = ${appl.telBotApi.tbtoken.value} ")
                    }
                    appl.resources.getString(R.string.chatId) -> {
                        _chatid.value = sharedPreferences.getString(
                            appl.resources.getString(R.string.chatId),
                            ""
                        )
                        Log.i(TAG, "prefchlist: _chatid = ${chatid.value}")
                    }
                    else -> {
                        Log.i(TAG, "OnSharedPreferenceChangeListener : $key ")
                    }
                }
            }

        appl.telBotApi.tbtoken.value?.let {
            appl.telBotApi.setRetr(it)
        }
        // Retrofit.Builder()
        //.addConverterFactory(MoshiConverterFactory.create(moshi))
        //  .addCallAdapterFactory(CoroutineCallAdapterFactory())
//    .client(OkHttpClient.Builder()
//        .connectTimeout(5, TimeUnit.SECONDS)
//        .writeTimeout(5, TimeUnit.SECONDS)
//        .readTimeout(5, TimeUnit.SECONDS)
//        .callTimeout(10, TimeUnit.SECONDS)
//        .addInterceptor(
//            HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY
//            }
//        )
//        .build()
//    )
        //.baseUrl(TELEGRAM_URL + tbtoken.value)
        //.build()
    }

    fun getMeTextFromTelega() {
        Log.i(TAG, "sendTextToTelega")
        viewModelScope.launch(Dispatchers.IO) {
            val r = (appl as App).telBotApi.retrofitService.getMe()
            val jrs = respgeted(r)
            _text.postValue(jrs)
        }
    }

    fun sendTextToTelega(s: String) {
        Log.i(TAG, "sendTextToTelega: $s")
        viewModelScope.launch(Dispatchers.IO) {
            chatid.value?.let { _chat_Id ->
                val r = (appl as App).telBotApi.retrofitService.sendMessageToTlg(_chat_Id, s)
                val jsq = respgeted(r)
                _text.postValue(jsq)
            }
        }
    }

    private fun respgeted(r: Response<ResponseBody>) = if (r.isSuccessful) {
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
}

interface TelegramBotApiService {
    @GET("getMe")
    suspend fun getMe():
    // The Coroutine Call Adapter allows us to return a Deferred, a Job with a result
            Response<ResponseBody>

    @GET("sendMessage")
    suspend fun sendMessageToTlg(@Query("chat_id") chat_Id: String, @Query("text") textMess: String):
            Response<ResponseBody>

    /*@GET("info/serviceList")
    suspend fun getBeeOptions(@Query("ctn") number: String, @Query("token") token: String):
            BeeOptionsData*/
}

class TelegramBotApi(ctx: Context) {

    private val apiTbtoken = MutableLiveData<String>().apply {
        value = PreferenceManager.getDefaultSharedPreferences(ctx)
            .getString(ctx.resources.getString(R.string.tbtoken), "")
    }
    val tbtoken: LiveData<String> = apiTbtoken
    fun setTbtoken(t: String) {
        apiTbtoken.value = t
    }

    private var retrofit: Retrofit = Retrofit.Builder()
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
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                .build()
        )
        .baseUrl("$TELEGRAM_URL$tbtoken/")
        .build()

    fun setRetr(value: String) {
        retrofit = Retrofit.Builder()
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
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                    .build()
            )
            .baseUrl("$TELEGRAM_URL$value/")
            .build()
    }

    val retrofitService: TelegramBotApiService by lazy {
        retrofit.create(TelegramBotApiService::class.java)
    }
}

class App: Application() {
    val telBotApi by lazy { TelegramBotApi(this) }
}