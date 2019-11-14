package ucl.hk69.auto_note

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.realm.Realm

private val TAB_TITLES1 = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
    R.string.tab_text_4,
    R.string.tab_text_5
)

private val TAB_TITLES2 = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3,
    R.string.tab_text_4,
    R.string.tab_text_5,
    R.string.tab_text_6
)

private val TAB_TITLES3 = arrayOf(
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
        val realm = Realm.getDefaultInstance()
        val opt = realm.where(OptionData::class.java).equalTo("key", 0).findFirst()
        return when {
            opt.numOfWeek == 5 -> context.resources.getString(TAB_TITLES1[position])
            opt.numOfWeek == 6 -> context.resources.getString(TAB_TITLES2[position])
            opt.numOfWeek == 7 -> context.resources.getString(TAB_TITLES3[position])
            else -> context.resources.getString(TAB_TITLES1[position])
        }
    }

    // 設定に応じて返すサイズを変更
    override fun getCount(): Int {
        val realm = Realm.getDefaultInstance()
        val opt = realm.where(OptionData::class.java).equalTo("key", 0).findFirst()
        return when {
            opt.numOfWeek == 5 -> TAB_TITLES1.size
            opt.numOfWeek == 6 -> TAB_TITLES2.size
            opt.numOfWeek == 7 -> TAB_TITLES3.size
            else -> TAB_TITLES1.size
        }
    }
}