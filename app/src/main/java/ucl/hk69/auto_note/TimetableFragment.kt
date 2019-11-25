package ucl.hk69.auto_note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class TimetableFragment : Fragment() {
    lateinit var classArray: Array<LinearLayout>
    lateinit var classNameArray: Array<TextView>
    lateinit var placeArray: Array<TextView>
    lateinit var teacherArray: Array<TextView>
    lateinit var memoArray: Array<TextView>
    var fgmID = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 曜日ごとの番号を取得
        fgmID = arguments?.getInt("ID") ?: 0
        // Viewの配列
        classArray = arrayOf(ll1st, ll2nd, ll3rd, ll4th, ll5th, ll6th, ll7th)
        classNameArray = arrayOf(text1stTitle, text2ndTitle, text3rdTitle, text4thTitle, text5thTitle, text6thTitle, text7thTitle)
        placeArray = arrayOf(text1stPlace, text2ndPlace, text3rdPlace, text4thPlace, text5thPlace, text6thPlace, text7thPlace)
        teacherArray = arrayOf(text1stTeacher, text2ndTeacher, text3rdTeacher, text4thTeacher, text5thTeacher, text6thTeacher, text7thTeacher)
        memoArray = arrayOf(text1stMemo, text2ndMemo, text3rdMemo, text4thMemo, text5thMemo, text6thMemo, text7thMemo)

        // 何限まで表示するか設定データを取得
        val time = Realm.getDefaultInstance().where(OptionData::class.java).equalTo("key", 0).findFirst().numOfTime

        // データに応じてViewを表示
        if(time > 4) ll5th.visibility = View.VISIBLE
        if(time > 5) ll6th.visibility = View.VISIBLE
        if(time > 6) ll7th.visibility = View.VISIBLE

        // タップ，長押し時の処理を記述
        for(i in 0 until time){
            setClassData(i)

            classArray[i].setOnClickListener {
                val intent = Intent(context, ListActivity::class.java)
                intent.putExtra("ID", fgmID + i)
                startActivity(intent)
            }

            classArray[i].setOnLongClickListener {
                val intent = Intent(context, ClassSettingActivity::class.java)
                intent.putExtra("ID", fgmID + i)
                startActivityForResult(intent, i)
                true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) setClassData(requestCode)
    }

    // データが変更された場合の処理
    fun setClassData(i: Int){
        val classData = Realm.getDefaultInstance().where(ClassData::class.java).equalTo("id", fgmID + i).findFirst()

        classNameArray[i].text = classData.className
        placeArray[i].text = classData.place
        teacherArray[i].text = classData.teacherName

        if(classData.memo.isNotEmpty()){
            memoArray[i].visibility = View.VISIBLE
            memoArray[i].text = classData.memo
        }else memoArray[i].visibility = View.GONE
    }
}