package ucl.hk69.auto_note

import android.graphics.Color
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

        constraintLayout.setBackgroundColor(Color.parseColor("#" + Realm.getDefaultInstance().where(OptionData::class.java).equalTo("key", 0).findFirst().bgColor))

        // Picassoで画像表示
        Picasso.with(this).load(intent.getStringExtra("uri").toUri()).fit().centerInside().into(imageView)

        rotateRight.setOnClickListener {
            imageView.rotation += 90f
        }

        rotateLeft.setOnClickListener {
            imageView.rotation -= 90f
        }
    }
}
