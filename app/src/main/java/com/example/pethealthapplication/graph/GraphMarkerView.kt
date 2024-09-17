package com.example.pethealthapplication.graph

import android.content.Context
import android.widget.TextView
import com.example.pethealthapplication.R
import com.example.pethealthapplication.dto.BloodSugarLevelDTO
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class GraphMarkerView(
    context: Context,
    layoutResource: Int,
    private val data: List<BloodSugarLevelDTO>
) : MarkerView(context, layoutResource) {

    private val markerTextView: TextView = findViewById(R.id.markerTextView)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        // Entry 인덱스에 해당하는 recordDate를 가져옵니다.
        val index = e?.x?.toInt() ?: 0
        if (index in data.indices) {
            val recordDate = data[index].recordDate
            markerTextView.text = recordDate
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}