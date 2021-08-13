package tk.kvakva.testtelegrambot.ui.text

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import tk.kvakva.testtelegrambot.R
import tk.kvakva.testtelegrambot.databinding.FragmentTextBinding
import java.io.InputStream

private const val TAG = "TextFragment"

class TextFragment : Fragment() {

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { textViewModel.setUri(it) }
        //binding.imageView2.setImageURI(uri)
    }

    private val textViewModel: TextViewModel by
         navGraphViewModels(R.id.mobile_navigation)

    //private lateinit var textViewModel: TextViewModel
    private var _binding: FragmentTextBinding? = null



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //textViewModel =
        //    ViewModelProvider(this).get(TextViewModel::class.java)


        _binding = FragmentTextBinding.inflate(inflater, container, false)
        val root: View = binding.root

        textViewModel.text.observe(viewLifecycleOwner, {
            Log.i(TAG, "onCreateView: oberver: $it")
            binding.returnedByTeleText.text = it
        })

        binding.textInLa.endIconMode=END_ICON_CLEAR_TEXT

        binding.bGetMe.setOnClickListener {
            Log.i(TAG, "onCreateView: bGetMe on Click List")
            textViewModel.getMeTextFromTelega()
        }

        binding.bSendMessage.setOnClickListener {
            Log.i(TAG, "onCreateView: ***** bSendMessage  ******")
            textViewModel.sendTextToTelega(binding.textEdit.text.toString())
        }

        binding.imageView3.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.bSndImg.setOnClickListener {

            val t=binding.textEdit.text.toString()
            if(t.isNotBlank()){
                val inputStream: InputStream? =
                    textViewModel.uri.value?.let { it1 ->
                        context?.contentResolver?.openInputStream(
                            it1
                        )
                    }
                inputStream?.let { stream ->
                    textViewModel.upload(stream,t)
                }
            }else {
                val inputStream: InputStream? =
                    textViewModel.uri.value?.let { it1 ->
                        context?.contentResolver?.openInputStream(
                            it1
                        )
                    }
                inputStream?.let { stream ->
                    textViewModel.upload(stream)
                }
            }
        }

       textViewModel.uri.observe(viewLifecycleOwner, {
            binding.imageView3.setImageURI(it)
        })


        binding.imageView3.setOnLongClickListener {
            Log.i(TAG, "**************** onCreateView: URI ${textViewModel.uri.value}")
            if(textViewModel.uri.value.toString().contains("android.resource")){
                return@setOnLongClickListener true
            }
            startActivity(Intent(Intent.ACTION_VIEW,textViewModel.uri.value).apply {
                Log.i(TAG, "**************** onCreateView: URI ${textViewModel.uri.value}")
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