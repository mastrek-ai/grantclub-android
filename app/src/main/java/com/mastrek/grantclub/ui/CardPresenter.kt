package com.mastrek.grantclub.ui

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mastrek.grantclub.R
import com.mastrek.grantclub.data.Channel

class CardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = ImageCardView(parent.context).apply {
            isFocusable      = true
            isFocusableInTouchMode = true
            setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val card    = viewHolder.view as ImageCardView
        card.titleText    = channel.name
        card.contentText  = channel.group ?: ""

        if (!channel.logoUrl.isNullOrEmpty()) {
            Glide.with(card.context)
                .load(channel.logoUrl)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, t: Transition<in Drawable>?) {
                        card.mainImage = resource
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        card.mainImage = ContextCompat.getDrawable(card.context, R.drawable.ic_channel_default)
                    }
                })
        } else {
            card.mainImage = ContextCompat.getDrawable(card.context, R.drawable.ic_channel_default)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val card = viewHolder.view as ImageCardView
        card.mainImage = null
    }

    companion object {
        private const val CARD_WIDTH  = 313
        private const val CARD_HEIGHT = 176
    }
}
