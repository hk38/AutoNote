package ucl.hk69.auto_note

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {


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

            if(startTime != null) startTimeArray[i].setText(startTime.time)
            if(endTime != null) endTimeArray[i].setText(endTime.time)
        }

        fab.setOnClickListener { view ->
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
}
