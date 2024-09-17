package com.example.pethealthapplication

import android.content.Context
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

object CalendarDecorators {

    // 오늘 날짜의 background를 설정하는 클래스
    private class TodayDecorator(private val context: Context) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_today)
        private val date = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date) ?: false
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable!!)
        }
    }

    // 이번 달에 속하지 않지만 캘린더에 보여지는 이전달/다음달의 일부 날짜를 설정하는 클래스
    private class SelectedMonthDecorator(private val selectedMonth: Int, private val context: Context) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.month != selectedMonth
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.gray)))
        }
    }




    // Decorator 객체를 반환하는 메서드
    fun getTodayDecorator(context: Context): DayViewDecorator {
        return TodayDecorator(context)
    }

    fun getSelectedMonthDecorator(selectedMonth: Int, context: Context): DayViewDecorator {
        return SelectedMonthDecorator(selectedMonth, context)
    }
}
