package ru.d3st.travelblogapp.presentation.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.d3st.travelblogapp.databinding.ListOverviewItemBinding
import ru.d3st.travelblogapp.model.domain.BloggerDomain


class OverviewAdapter(
    private val onUserClick: (BloggerDomain) -> Unit
) : ListAdapter<BloggerDomain, OverviewAdapter.UserViewHolder>(USERS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder.create(parent)

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { onUserClick(current) }
        holder.bind(current)
    }

    class UserViewHolder private constructor(private val binding: ListOverviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bloggerDomain: BloggerDomain) {

            binding.blogger = bloggerDomain
            binding.executePendingBindings()

        }

        companion object {
            fun create(parent: ViewGroup): UserViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListOverviewItemBinding.inflate(layoutInflater, parent, false)
                return UserViewHolder(binding)
            }
        }
    }

    companion object {
        private val USERS_COMPARATOR = object : DiffUtil.ItemCallback<BloggerDomain>() {
            override fun areItemsTheSame(oldItem: BloggerDomain, newItem: BloggerDomain): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BloggerDomain,
                newItem: BloggerDomain
            ): Boolean {
                return oldItem.uid == newItem.uid
            }

        }
    }


}
