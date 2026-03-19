package com.mastrek.grantclub.mobile.ui

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mastrek.grantclub.mobile.R
import com.mastrek.grantclub.mobile.data.Channel
import com.mastrek.grantclub.mobile.databinding.ItemChannelBinding

class ChannelAdapter(
    private val onClick: (Channel) -> Unit
) : ListAdapter<Channel, ChannelAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ch = getItem(position)
        holder.binding.tvName.text  = ch.name
        holder.binding.tvGroup.text = ch.group ?: ""

        Glide.with(holder.binding.ivLogo)
            .load(ch.logoUrl)
            .placeholder(R.drawable.ic_channel)
            .error(R.drawable.ic_channel)
            .into(holder.binding.ivLogo)

        holder.binding.root.setOnClickListener { onClick(ch) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(a: Channel, b: Channel) = a.id == b.id
            override fun areContentsTheSame(a: Channel, b: Channel) = a == b
        }
    }
}
