package ucl.hk69.auto_note

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_class_setting.*

class ClassSettingActivity : AppCompatActivity() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_setting)
        setSupportActionBar(toolbar)

        // データIDを取得
        val id = intent.getIntExtra("ID", 0)

        // IDからデータを取得
        val classData = realm.where(ClassData::class.java).equalTo("id", id).findFirst()
        val bgColor = realm.where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()?.bgColor ?: "f6ae54"
        constraintLayout.setBackgroundColor(Color.parseColor("#$bgColor"))

        // Viewに値をセット
        editClassName.setText(classData?.className)
        editPlace.setText(classData?.place)
        editTeacher.setText(classData?.teacherName)
        editMemo.setText(classData?.memo)

        // FABが押されたら保存処理
        fab.setOnClickListener {
            realm.executeTransaction{
                classData?.className = editClassName.text.toString()
                classData?.place = editPlace.text.toString()
                classData?.teacherName = editTeacher.text.toString()
                classData?.memo = editMemo.text.toString()
            }

            val appWM = AppWidgetManager.getInstance(applicationContext)
            val ids = appWM.getAppWidgetIds(ComponentName(applicationContext, TimeTableWidget::class.java))
            ids.forEach { updateAppWidget(applicationContext, appWM, it) }

            val result = Intent()
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}
