package ucl.hk69.auto_note

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        val realm = Realm.getDefaultInstance()
        val classData = realm.where(ClassData::class.java).equalTo("id", 0).findFirst()

        classData.pictureData?.forEach { item ->
            val iv = ImageView(this)
            iv.setImageURI(item.pass?.toUri())
            listLL.addView(iv)
        }
    }

}
