package ucl.hk69.auto_note

import android.app.TimePickerDialog
import android.os.Bundle
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
        val startTimeArray = arrayOf(editStart1st, editStart2nd, editStart3rd, editStart4th)
        val endTimeArray = arrayOf(editEnd1st, editEnd2nd, editEnd3rd, editEnd4th)

        for(i in 0..3){
            val startTime = realm.where(SettingData::class.java).equalTo("id", i*10 + 0).findFirst()
            val endTime = realm.where(SettingData::class.java).equalTo("id", i*10+1).findFirst()

            if(startTime.time != null) startTimeArray[i].text = startTime.time
            if(endTime.time != null) endTimeArray[i].text = endTime.time

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
            realm.executeTransaction {
                for(i in 0..3){
                    val startTime = realm.where(SettingData::class.java).equalTo("id", i*10 + 0).findFirst()
                    startTime.time = startTimeArray[i].text.toString()

                    val endTime = realm.where(SettingData::class.java).equalTo("id", i*10+1).findFirst()
                    endTime.time = endTimeArray[i].text.toString()
                }
            }
            finish()
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val str = String.format(Locale.JAPAN, "%d:%d", hourOfDay, minute)
        // use the plug in of Kotlin Android Extensions
        timeSetButton?.text = str
    }
}
