package ucl.hk69.auto_note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
    var fgmID = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fgmID = arguments?.getInt("ID") ?: 0
        classArray = arrayOf(ll1st, ll2nd, ll3rd, ll4th)
        classNameArray = arrayOf(text1stTitle, text2ndTitle, text3rdTitle, text4thTitle)
        placeArray = arrayOf(text1stPlace, text2ndPlace, text3rdPlace, text4thPlace)
        teacherArray = arrayOf(text1stTeacher, text2ndTeacher, text3rdTeacher, text4thTeacher)

        for(i in classArray.indices){
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

    fun setClassData(i: Int){
        val realm = Realm.getDefaultInstance()
        val classData = realm.where(ClassData::class.java).equalTo("id", fgmID + i).findFirst()

        classNameArray[i].text = classData.className
        placeArray[i].text = classData.place
        teacherArray[i].text = classData.teacherName
    }
}