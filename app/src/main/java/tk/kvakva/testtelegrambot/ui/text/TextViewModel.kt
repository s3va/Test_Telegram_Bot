package tk.kvakva.testtelegrambot.ui.text

import android.app.Application
import android.content.SharedPreferences
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


    private val _text = MutableLiveData<String>().apply {
        value = "This is text Fragment"
    }
    val text: LiveData<String> = _text

    init {
        Log.i(TAG, "_tbtoken: ${(appl as App).tbtoken.value} **")
        Log.i(TAG, "_chatid: ${appl.chatid.value} **")

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
            val r = (appl as App).retrofitService.getMe()
            val jrs = respgeted(r)
            _text.postValue(jrs)
        }
    }

    fun sendTextToTelega(s: String) {
        Log.i(TAG, "sendTextToTelega: $s")
        viewModelScope.launch(Dispatchers.IO) {
            (appl as App).chatid.value?.let { _chat_Id ->
                val r = appl.retrofitService.sendMessageToTlg(_chat_Id, s)
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
    suspend fun sendMessageToTlg(
        @Query("chat_id") chat_Id: String,
        @Query("text") textMess: String
    ):
            Response<ResponseBody>

    /*@GET("info/serviceList")
    suspend fun getBeeOptions(@Query("ctn") number: String, @Query("token") token: String):
            BeeOptionsData*/
}

// class TelegramBotApi(ctx: Context) {


//}

class App : Application(),SharedPreferences.OnSharedPreferenceChangeListener {
    //val telBotApi by lazy { TelegramBotApi(this) }

    companion object {
        lateinit var retrofit: Retrofit
    }

    private val _chatid = MutableLiveData<String>()
    val chatid: LiveData<String> = _chatid
    private val _apiTbtoken = MutableLiveData<String>()
    val tbtoken: LiveData<String> = _apiTbtoken
    private fun setTbtoken(t: String) {
        _apiTbtoken.value = t
    }

    private fun setChatId(c: String) {
        _chatid.value = c
    }

    override fun onCreate() {
        super.onCreate()

        _chatid.value =
            PreferenceManager.getDefaultSharedPreferences(this)
                .getString(resources.getString(R.string.chatId), "")
        _apiTbtoken.value =
            PreferenceManager.getDefaultSharedPreferences(this@App.applicationContext)
                .getString(resources.getString(R.string.tbtoken), "")

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
            .baseUrl("$TELEGRAM_URL$tbtoken/")
            .build()

        PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
            .registerOnSharedPreferenceChangeListener(this)

        tbtoken.value?.let {
            setRetr(it)
        }
    }

    private fun setRetr(value: String) {
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

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     *
     * This callback will be run on your main thread.
     *
     *
     * *Note: This callback will not be triggered when preferences are cleared
     * via Editor.clear], unless targeting [android.os.Build.VERSION_CODES.R]
     * on devices running OS versions [Android R][android.os.Build.VERSION_CODES.R]
     * or later.*
     *
     * @param sharedPreferences The [SharedPreferences] that received the change.
     * @param key The key of the preference that was changed, added, or removed. Apps targeting
     * [android.os.Build.VERSION_CODES.R] on devices running OS versions
     * [Android R][android.os.Build.VERSION_CODES.R] or later, will receive
     * a `null` value when preferences are cleared.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            resources.getString(R.string.tbtoken) -> {
                sharedPreferences?.getString(
                    key,
                    ""
                )?.let {
                    setTbtoken(it)
                    setRetr(it)
                }
                Log.i(TAG, "prefchlist: _tbtoken = ${tbtoken.value} ")
            }
            resources.getString(R.string.chatId) -> {
                Log.i(TAG, "onCreate in App: on Shar pref chat id !!!!!!!!!!!!!!!!!!!!!!!! ${resources.getString(R.string.chatId)}")
                sharedPreferences?.getString(
                    key,
                    ""
                )?.let {
                    setChatId(it)
                }
                Log.i(TAG, "prefchlist: _chatid = ${chatid.value}")
            }
            else -> {
                Log.i(TAG, "OnSharedPreferenceChangeListener : $key ")
            }
        }
    }

}