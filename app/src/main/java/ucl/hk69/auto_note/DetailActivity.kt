package ucl.hk69.auto_note

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    var rotate = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        constraintLayout.setBackgroundColor(Color.parseColor("#" + Realm.getDefaultInstance().where(OptionData::class.java).equalTo("key", 0).findFirst().bgColor))

        // 画像表示
        setPic()

        rotateRight.setOnClickListener {
            rotate = (rotate + 90f) % 360
            setPic()
        }

        rotateLeft.setOnClickListener {
            rotate = (rotate - 90f) % 360
            setPic()
        }
    }

    fun setPic(){
        // Picassoで読み込み
        Picasso.with(this).load(intent.getStringExtra("uri").toUri()).fit().centerInside().rotate(rotate).into(imageView)
    }
}
