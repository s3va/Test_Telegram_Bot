package tk.kvakva.testtelegrambot.ui.slideshow

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import tk.kvakva.testtelegrambot.R
import tk.kvakva.testtelegrambot.ui.text.respgeted
import java.io.InputStream
import java.time.LocalDateTime
import java.util.*

class SlideshowViewModel(private val appl: Application) : AndroidViewModel(appl) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text

    private val _uri = MutableLiveData<Uri>().apply {
        value = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    appl.resources.getResourcePackageName(R.mipmap.internetpicture_foreground) + '/' +
                    appl.resources.getResourceTypeName(R.mipmap.internetpicture_foreground) + '/' +
                    appl.resources.getResourceEntryName(R.mipmap.internetpicture_foreground)
        )
    }
    val uri: LiveData<Uri> = _uri
    fun setUri(u: Uri) {
        _uri.value = u
    }

    private val ti = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().toString()
    } else {
        Date().toString()
    }

    fun upload(inputStream: InputStream) {
        val part1 = MultipartBody.Part.createFormData(
            "photo", ti, inputStream.readBytes()
                .toRequestBody(
                    "image/*".toMediaTypeOrNull()

                )
        )
        val part2 = MultipartBody.Part.createFormData(
            "caption", "Test two ${Date()} \nПроверка связи номер 2!"
        )


        viewModelScope.launch(Dispatchers.IO) {
            val r = PreferenceManager.getDefaultSharedPreferences(appl)
                .getString(appl.resources.getString(R.string.tbtoken), "")?.let { _tb_token ->
                    PreferenceManager.getDefaultSharedPreferences(appl)
                        .getString(appl.resources.getString(R.string.chatId), "")?.let { _chat_id ->
                            tk.kvakva.testtelegrambot.ui.text.retrofitService.sendPhoto(
                                _tb_token, _chat_id, part1, part2
                            )
                        }
                }
            val jrs = r?.let { respgeted(it) }
            _text.postValue(jrs)
        }
    }
}
