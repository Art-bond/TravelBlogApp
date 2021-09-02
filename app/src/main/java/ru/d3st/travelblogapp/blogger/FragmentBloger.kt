package ru.d3st.travelblogapp.blogger

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.d3st.travelblogapp.R

class FragmentBloger : Fragment() {

    companion object {
        fun newInstance() = FragmentBloger()
    }

    private lateinit var viewModel: FragmentBlogerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bloger_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentBlogerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}