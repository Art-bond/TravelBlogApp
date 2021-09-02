package ru.d3st.travelblogapp.consumer

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.d3st.travelblogapp.R

class FragmentConsumer : Fragment() {

    companion object {
        fun newInstance() = FragmentConsumer()
    }

    private lateinit var viewModel: FragmentConsumerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consumer_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentConsumerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}