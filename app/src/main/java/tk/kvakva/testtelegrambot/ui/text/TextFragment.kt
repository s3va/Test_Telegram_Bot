package tk.kvakva.testtelegrambot.ui.text

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import tk.kvakva.testtelegrambot.R
import tk.kvakva.testtelegrambot.databinding.FragmentTextBinding

private const val TAG = "TextFragment"

class TextFragment : Fragment() {

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}