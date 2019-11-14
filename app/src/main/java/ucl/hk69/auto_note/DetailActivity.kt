package ucl.hk69.auto_note

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val picData = Realm.getDefaultInstance().where(PictureData::class.java).equalTo("pass", intent.getStringExtra("uri")).findFirst()

        // Picassoで画像表示
        Picasso.with(this).load(picData.pass.toUri()).fit().centerInside().into(imageView)
        editText.setText(picData.text)

        fab.setOnClickListener {
            Realm.getDefaultInstance().executeTransaction {
                picData.text = editText.text.toString()
            }
        }
    }

}
