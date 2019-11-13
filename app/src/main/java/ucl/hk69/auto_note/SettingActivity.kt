package ucl.hk69.auto_note

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*

class SettingActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    var timeSetButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)

        val realm = Realm.getDefaultInstance()
        val startTimeArray = arrayOf(editStart1st, editStart2nd, editStart3rd, editStart4th, editStart5th, editStart6th, editStart7th)
        val endTimeArray = arrayOf(editEnd1st, editEnd2nd, editEnd3rd, editEnd4th, editEnd5th, editEnd6th, editEnd7th)
        val time = realm.where(OptionData::class.java).equalTo("key", 0).findFirst().numOfTime

        if(time > 4) ll5th.visibility = View.VISIBLE
        if(time > 5) ll6th.visibility = View.VISIBLE
        if(time > 6) ll7th.visibility = View.VISIBLE

        for(i in 0 until time){
            val startTime = realm.where(SettingData::class.java).equalTo("id", i*10 + 0).findFirst()
            val endTime = realm.where(SettingData::class.java).equalTo("id", i*10+1).findFirst()

            startTimeArray[i].text = "${startTime.hour}:${startTime.minute}"
            endTimeArray[i].text = "${endTime.hour}:${endTime.minute}"

            startTimeArray[i].setOnClickListener {
                timeSetButton = startTimeArray[i]
                val newFragment = TimePick()
                newFragment.show(supportFragmentManager, "timePicker")
            }

            endTimeArray[i].setOnClickListener {
                timeSetButton = endTimeArray[i]
                val newFragment = TimePick()
                newFragment.show(supportFragmentManager, "timePicker")
            }
        }

        fab.setOnClickListener {
            // TODO オプションの保存周りを追加すること
            realm.executeTransaction {
                for(i in 0 until time){
                    val startTime = realm.where(SettingData::class.java).equalTo("id", i*10 + 0).findFirst()
                    val startText = startTimeArray[i].text.toString().split(":")
                    startTime.hour = startText[0]
                    startTime.minute = startText[1]

                    val endTime = realm.where(SettingData::class.java).equalTo("id", i*10+1).findFirst()
                    val endText = endTimeArray[i].text.toString().split(":")
                    endTime.hour = endText[0]
                    endTime.minute = endText[1]
                }
            }
            finish()
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val str = String.format(Locale.JAPAN, "%02d:%02d", hourOfDay, minute)
        timeSetButton?.text = str
    }
}
