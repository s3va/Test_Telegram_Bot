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





}
