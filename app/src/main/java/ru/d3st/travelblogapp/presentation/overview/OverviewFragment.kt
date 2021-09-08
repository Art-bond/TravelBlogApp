package ru.d3st.travelblogapp.presentation.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import ru.d3st.travelblogapp.databinding.FragmentOverviewBinding

@AndroidEntryPoint
class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentOverviewBinding.inflate(inflater)

        val adapter = OverviewAdapter { user ->

            navigateToDetail(user.uid)

        }
        binding.videoList.adapter = adapter

        viewModel.allUsers.observe(viewLifecycleOwner) { users -> adapter.submitList(users) }

        return binding.root
    }

    private fun navigateToDetail(uid: String) {
        val directions =
            OverviewFragmentDirections.actionOverviewFragmentToProfileFragment(uid)

        findNavController().navigate(directions)
    }
}
