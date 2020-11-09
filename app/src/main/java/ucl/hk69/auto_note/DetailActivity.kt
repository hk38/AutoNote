package ucl.hk69.auto_note

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }
    var rotate = 0f
    var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val bgColor = realm.where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()?.bgColor ?: "f6ae54"
        constraintLayout.setBackgroundColor(Color.parseColor("#$bgColor"))
        uri = intent.getStringExtra("uri")?.toUri()
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

    private fun setPic(){
        if(uri == null) Picasso.with(applicationContext).load(R.drawable.ic_baseline_image_not_supported_24).fit().centerInside().rotate(rotate).into(imageView)
        else Picasso.with(applicationContext).load(uri).fit().centerInside().rotate(rotate).into(imageView)
        // Picassoで読み込み
    }
}
