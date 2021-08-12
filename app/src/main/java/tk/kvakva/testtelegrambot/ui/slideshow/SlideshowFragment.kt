package tk.kvakva.testtelegrambot.ui.slideshow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import tk.kvakva.testtelegrambot.databinding.FragmentSlideshowBinding
import java.io.InputStream

private const val TAG = "SlideshowFragment"

class SlideshowFragment : Fragment() {

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { slideshowViewModel.setUri(it) }
        //binding.imageView2.setImageURI(uri)
    }

    private lateinit var slideshowViewModel: SlideshowViewModel
    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        binding.bChooseImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.bSendImage.setOnClickListener {
            val inputStream: InputStream? =
                slideshowViewModel.uri.value?.let { it1 ->
                    context?.contentResolver?.openInputStream(
                        it1
                    )
                }
            inputStream?.let { stream ->
                slideshowViewModel.upload(stream)
            }
        }
        slideshowViewModel.uri.observe(viewLifecycleOwner, {
            binding.imageView2.setImageURI(it)
        })
        binding.imageView2.setOnLongClickListener {
            Log.i(TAG, "**************** onCreateView: URI ${slideshowViewModel.uri.value}")
            if(slideshowViewModel.uri.value.toString().contains("android.resource")){
                return@setOnLongClickListener true
            }
            startActivity(Intent(Intent.ACTION_VIEW,slideshowViewModel.uri.value).apply {
                Log.i(TAG, "**************** onCreateView: URI ${slideshowViewModel.uri.value}")
           //     setDataAndType(slideshowViewModel.uri.value,"image/*")
           //     flags = FLAG_GRANT_READ_URI_PERMISSION
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            })

           // startActivity(Intent(Intent.ACTION_VIEW,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
           //     setDataAndType(slideshowViewModel.uri.value,"image/*")
           //     flags = FLAG_GRANT_READ_URI_PERMISSION
           // })
            true
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
