package ru.d3st.travelblogapp.presentation.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.d3st.travelblogapp.R
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

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoUrlImageView: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        private val countTextView: TextView = itemView.findViewById(R.id.tv_count)

        fun bind(bloggerDomain: BloggerDomain) {
            photoUrlImageView.load(bloggerDomain.photoUrl)
            nameTextView.text = bloggerDomain.name
            countTextView.text = "${bloggerDomain.videos} videos"

        }

        companion object {
            fun create(parent: ViewGroup): UserViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_overview_item, parent, false)
                return UserViewHolder(view)
            }
        }
    }

    companion object {
        private val USERS_COMPARATOR = object : DiffUtil.ItemCallback<BloggerDomain>() {
            override fun areItemsTheSame(oldItem: BloggerDomain, newItem: BloggerDomain): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: BloggerDomain, newItem: BloggerDomain): Boolean {
                return oldItem.uid == newItem.uid
            }

        }
    }


}
