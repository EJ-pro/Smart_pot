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
import org.w3c.dom.Text

class Mainpage_Fragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var addButton: Button
    private lateinit var addText: TextView
    private lateinit var addImage: ImageView
    private lateinit var indicatorLayout: LinearLayout
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
                // 마지막 페이지에 도달하면 addButton을 표시
                if (position == fragments.size - 1) {
                    addButton.visibility = View.VISIBLE
                    addText.visibility = View.VISIBLE
                    addImage.visibility = View.VISIBLE
                }
            }
        })
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
        if (currentPosition == fragments.size - 1) {
            addButton.visibility = View.GONE
            addText.visibility = View.GONE
        }
    }
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}
