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



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
