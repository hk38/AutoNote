package ucl.hk69.auto_note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_class_setting.*

class ClassSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_setting)
        setSupportActionBar(toolbar)

        val id = intent.getIntExtra("ID", 0)

        val realm = Realm.getDefaultInstance()
        val classData = realm.where(ClassData::class.java).equalTo("id", id).findFirst()
        editClassName.setText(classData.className)
        editPlace.setText(classData.place)
        editTeacher.setText(classData.teacherName)

        fab.setOnClickListener {
            realm.executeTransaction{
                classData.className = editClassName.text.toString()
                classData.place = editPlace.text.toString()
                classData.teacherName = editTeacher.text.toString()
            }

            val result = Intent()
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

}
