package ru.d3st.travelblogapp.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ru.d3st.travelblogapp.databinding.FragmentBloggerProfileBinding
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {


    private lateinit var binding: FragmentBloggerProfileBinding

    private val args: ProfileFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ProfileViewModelFactory

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModel.provideFactory(viewModelFactory, args.bloggerId)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBloggerProfileBinding.inflate(inflater)

        val adapter = ProfileViewVideoAdapter { video ->
            navigateToShowView(video, args.bloggerId)

        }
        binding.videoList.adapter = adapter

        viewModel.selectedUser.observe(viewLifecycleOwner) { user -> bindUser(user) }
        viewModel.allVideo.observe(viewLifecycleOwner) { videos -> adapter.submitList(videos) }

        return binding.root
    }

    private fun navigateToShowView(video: VideoDomain, bloggerId : String) {
        val directions = ProfileFragmentDirections.actionProfileFragmentToShowFragment(video, bloggerId)
        findNavController().navigate(directions)
    }

    private fun bindUser(bloggerDomain: BloggerDomain) {
        binding.tvName.text = bloggerDomain.name
        binding.ivAvatar.load(bloggerDomain.photoUrl)
    }
}