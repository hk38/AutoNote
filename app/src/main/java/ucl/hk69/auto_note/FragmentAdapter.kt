package ucl.hk69.auto_note

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.realm.Realm
private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
    R.string.tab_text_4,
    R.string.tab_text_5,
    R.string.tab_text_6,
    R.string.tab_text_7
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class FragmentAdapter(private val context: Context, fm: FragmentManager, behavior: Int): FragmentPagerAdapter(fm, behavior){

    // Fragmentの位置に応じてIDを設定
    override fun getItem(position: Int): Fragment {
        val fragment = TimetableFragment()
        fragment.arguments = Bundle().apply {
            putInt("ID", position * 10)
        }
        return fragment
    }

    // 設定に応じて見る配列を変更
    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    // 設定に応じて返すサイズを変更
    override fun getCount(): Int {
        return Realm.getDefaultInstance().where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()?.numOfWeek ?: 5
    }
}