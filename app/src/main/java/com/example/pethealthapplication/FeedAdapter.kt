package com.example.pethealthapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class FeedAdapter(context: Context, items: List<FeedItem>) : ArrayAdapter<FeedItem>(context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.feed_list_item, parent, false)
        }

        val item = getItem(position)
        val imageView = view!!.findViewById<ImageView>(R.id.itemImage)
        val nameView = view.findViewById<TextView>(R.id.itemName)
        val priceView = view.findViewById<TextView>(R.id.itemPrice)
        val nutritionView = view.findViewById<TextView>(R.id.itemNutrition)

        item?.let {
            imageView.setImageResource(it.imageResId)
            nameView.text = it.name
            priceView.text = it.price
            nutritionView.text = it.nutrition
        }

        return view
    }
}