import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.smartpot.BlankFragment
import com.example.smartpot.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Mainpage_Fragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var addButton: Button
    private lateinit var addText: TextView
    private lateinit var addImage: ImageView
    private lateinit var indicatorLayout: LinearLayout
    private lateinit var chart: LineChart
    private val fragments = ArrayList<Fragment>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mainpage, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        addButton = view.findViewById(R.id.addButton)
        addText = view.findViewById(R.id.addText)
        addImage = view.findViewById(R.id.addImage)
        indicatorLayout = view.findViewById(R.id.indicatorLayout)

        chart = view.findViewById(R.id.plant_water_chart)

        val pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })
        viewPager.adapter?.notifyItemInserted(0)
        viewPager.setCurrentItem(0, true)

        addButton.setOnClickListener {
            addNewPage()
            addNewPage()
            addButton.visibility = View.GONE
            addText.visibility = View.GONE
            addImage.visibility = View.GONE
            indicatorLayout.visibility = View.VISIBLE
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == fragments.size - 1) {
                    addButton.visibility = View.VISIBLE
                    addText.visibility = View.VISIBLE
                    addImage.visibility = View.VISIBLE
                }
            }
        })

        initChart()
        setChartData()

        return view
    }

    private fun updateIndicators(currentPosition: Int) {
        indicatorLayout.removeAllViews()
        for (i in 0 until fragments.size) {
            val indicator = View(context)
            val indicatorSize = resources.getDimensionPixelSize(R.dimen.indicator_size)
            val indicatorMargin = resources.getDimensionPixelSize(R.dimen.indicator_margin)
            val params = LinearLayout.LayoutParams(indicatorSize, indicatorSize)
            params.setMargins(indicatorMargin, 0, indicatorMargin, 0)
            indicator.layoutParams = params
            indicator.setBackgroundResource(
                if (i == currentPosition) R.drawable.selected_indicator
                else R.drawable.unselected_indiactor
            )
            indicatorLayout.addView(indicator)
        }
    }

    private fun addNewPage() {
        val currentPosition = viewPager.currentItem
        fragments.add(BlankFragment.newInstance(fragments.size + 1))
        viewPager.adapter?.notifyItemInserted(currentPosition + 1)
        viewPager.setCurrentItem(0, true)
        updateIndicators(currentPosition)
        if (currentPosition == fragments.size - 1) {
            addButton.visibility = View.GONE
            addText.visibility = View.GONE
        }
    }

    private fun initChart() {
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val currentDate = getCurrentDate()
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DAY_OF_MONTH, value.toInt() - 6) // -6을 추가하여 역순으로 날짜를 계산
                val formattedDate = SimpleDateFormat("MM/dd", Locale.getDefault()).format(calendar.time)
                return formattedDate
            }
        }
        val leftAxis = chart.axisLeft
        leftAxis.axisMinimum = 1f
        leftAxis.axisMaximum = 7f

        xAxis.setCenterAxisLabels(false)
        leftAxis.setDrawGridLines(false)

        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false
        val xLabels = chart.xAxis
        xLabels.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val currentDate = getCurrentDate()
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DAY_OF_MONTH, value.toInt() - 6)
                val formattedDate = SimpleDateFormat("MM/dd", Locale.getDefault()).format(calendar.time)
                val dayOfWeek = SimpleDateFormat("E", Locale.getDefault()).format(calendar.time)
                return "$formattedDate\n$dayOfWeek"
            }
        }
    }
    private fun getCurrentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }
    private fun setChartData() {
        val entries = mutableListOf<Entry>()

        // 1~7일 동안의 1~7의 값을 가지는 데이터를 entries에 추가
        for (i in 0 until 7) {
            entries.add(Entry(i.toFloat(), (i + 1).toFloat())) // 1~7의 값을 가지는 예시 데이터
        }

        val dataSet = LineDataSet(entries, "Plant Water Data")
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}
