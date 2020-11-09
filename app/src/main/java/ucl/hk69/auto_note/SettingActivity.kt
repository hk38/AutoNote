package ucl.hk69.auto_note

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*

class SettingActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    var timeSetButton: Button? = null
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)

        // 選択肢のリスト
        val weekList = arrayOf("金曜日まで", "土曜日まで", "日曜日まで")
        val timeList = arrayOf("4限まで", "5限まで", "6限まで", "7限まで")
        // 編集ボタンの配列
        val startTimeArray = arrayOf(editStart1st, editStart2nd, editStart3rd, editStart4th, editStart5th, editStart6th, editStart7th)
        val endTimeArray = arrayOf(editEnd1st, editEnd2nd, editEnd3rd, editEnd4th, editEnd5th, editEnd6th, editEnd7th)
        // seekbarの配列
        val rgbBarArray = arrayOf(seekBarR, seekBarG, seekBarB)
        // rgb文字入力の配列
        val editRgbArray = arrayOf(editR, editG, editB)
        // realmからデータを取得
        val opt = realm.where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()

        constraintLayout.setBackgroundColor(Color.parseColor("#" + (opt?.bgColor ?: "f6ae54") ))
        val chunck = opt?.bgColor?.chunked(2) ?: listOf("f6", "ae", "54")

        for(i in 0..2){
            editRgbArray[i].setText(Integer.parseInt(chunck[i], 16).toString())
            rgbBarArray[i].progress = Integer.parseInt(chunck[i], 16)

            editRgbArray[i].addTextChangedListener {
                when {
                    editRgbArray[i].text.isEmpty() -> editRgbArray[i].setText(0.toString())
                    Integer.parseInt(it.toString()) > 255 -> editRgbArray[i].setText(255.toString())
                    Integer.parseInt(it.toString()) < 0 -> editRgbArray[i].setText(0.toString())
                }
                editRgbArray[i].setSelection(editRgbArray[i].text.length)

                rgbBarArray[i].progress = Integer.parseInt(editRgbArray[i].text.toString())
                constraintLayout.setBackgroundColor(Color.rgb(Integer.parseInt(editR.text.toString()), Integer.parseInt(editG.text.toString()), Integer.parseInt(editB.text.toString())))
            }

            rgbBarArray[i].setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, formUser: Boolean) {
                    editRgbArray[i].setText(progress.toString())
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
        }

        // 現在の設定を取得
        var timeTemp = opt?.numOfTime
        var weekTemp = opt?.numOfWeek

        // 設定に応じてレイアウトを変更
        when(timeTemp){
            4 -> textTimeOfDay.text = timeList[0]
            5 -> {
                ll5th.visibility = View.VISIBLE
                textTimeOfDay.text = timeList[1]
            }
            6 -> {
                ll5th.visibility = View.VISIBLE
                ll6th.visibility = View.VISIBLE
                textTimeOfDay.text = timeList[2]
            }
            7 -> {
                ll5th.visibility = View.VISIBLE
                ll6th.visibility = View.VISIBLE
                ll7th.visibility = View.VISIBLE
                textTimeOfDay.text = timeList[3]
            }
        }

        when(weekTemp){
            5 -> textDayOfWeek.text = weekList[0]
            6 -> textDayOfWeek.text = weekList[1]
            7 -> textDayOfWeek.text = weekList[2]
        }

        // 現在設定の設定とタップ時の処理を記述
        for(i in 0 until (opt?.numOfTime ?: 4) ){
            val startTime = realm.where(SettingData::class.java).equalTo("id", i*10 + 0).findFirst()
            val endTime = realm.where(SettingData::class.java).equalTo("id", i*10+1).findFirst()

            startTimeArray[i].text = "${startTime?.hour}:${startTime?.minute}"
            endTimeArray[i].text = "${endTime?.hour}:${endTime?.minute}"

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

        textDayOfWeek.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("1週間の設定")
                .setItems(weekList) { _, which ->
                    textDayOfWeek.text = weekList[which]
                    when(which){
                        0 -> weekTemp = 5
                        1 -> weekTemp = 6
                        2 -> weekTemp = 7
                    }
                }
                .show()
        }

        textTimeOfDay.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("1日の設定")
                .setItems(timeList) { _, which ->
                    textTimeOfDay.text = timeList[which]
                    when(which){
                        0 -> timeTemp = 4
                        1 -> timeTemp = 5
                        2 -> timeTemp = 6
                        3 -> timeTemp = 7
                    }
                }
                .show()
        }

        // FABタップ時の保存処理
        fab.setOnClickListener {
            realm.executeTransaction {
                for(i in 0 until (opt?.numOfTime ?: 4)){
                    val startTime = realm.where(SettingData::class.java).equalTo("id", i*10 + 0).findFirst()
                    val startText = startTimeArray[i].text.toString().split(":")
                    startTime?.hour = startText[0]
                    startTime?.minute = startText[1]

                    val endTime = realm.where(SettingData::class.java).equalTo("id", i*10+1).findFirst()
                    val endText = endTimeArray[i].text.toString().split(":")
                    endTime?.hour = endText[0]
                    endTime?.minute = endText[1]
                }
            }

            var optCheck = false

            // アプリ設定への変更がある場合は注意を表示
            if(timeTemp != opt?.numOfTime || weekTemp != opt?.numOfWeek){
                Toast.makeText(applicationContext, "再起動後有効になります", Toast.LENGTH_LONG).show()

                realm.executeTransaction{
                    if(timeTemp != null) opt?.numOfTime = timeTemp as Int
                    if(weekTemp != null) opt?.numOfWeek = weekTemp as Int
                }

                optCheck = true
            }

            val bgc = Integer.toHexString((constraintLayout.background as ColorDrawable).color).removePrefix("ff")

            // 背景色変更処理
            if(bgc != opt?.bgColor){
                realm.executeTransaction {
                    opt?.bgColor = bgc
               }
                Toast.makeText(applicationContext, "再起動後有効になります", Toast.LENGTH_LONG).show()
                optCheck = true
            }

            val appWM = AppWidgetManager.getInstance(applicationContext)
            val ids = appWM.getAppWidgetIds(ComponentName(applicationContext, TimeTableWidget::class.java))
            ids.forEach { updateAppWidget(applicationContext, appWM, it) }


            val intent = Intent()
            intent.putExtra("optCheck", optCheck)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val str = String.format(Locale.JAPAN, "%02d:%02d", hourOfDay, minute)
        timeSetButton?.text = str
    }
}

