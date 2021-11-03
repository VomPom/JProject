package julis.wang.kotlinlearn.jetpack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import julis.wang.kotlinlearn.R
import julis.wang.kotlinlearn.databinding.FragmentOneBinding

/*******************************************************
 *
 * Created by juliswang on 2021/11/03 09:44
 *
 * Description :
 *
 *
 *******************************************************/

class FragmentOne : Fragment(R.layout.fragment_one) {
    // Use the 'by activityViewModels()' Kotlin property delegate
    // from the fragment-ktx artifact
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentOneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentOneBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        dataViewModel.count.observe(viewLifecycleOwner) {
            viewBinding.tvData.text = it.toString()
        }
    }

}
