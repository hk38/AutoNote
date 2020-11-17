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

        // アプリデータから背景色を取得して設定
        val bgColor = realm.where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()?.bgColor ?: "f6ae54"
        constraintLayout.setBackgroundColor(Color.parseColor("#$bgColor"))

        // 渡されたURIを取得
        uri = intent.getStringExtra("uri")?.toUri()
        // 画像表示
        setPic()

        // 右回転の処理
        rotateRight.setOnClickListener {
            rotate = (rotate + 90f) % 360
            setPic()
        }

        // 左回転の処理
        rotateLeft.setOnClickListener {
            rotate = (rotate - 90f) % 360
            setPic()
        }
    }

    private fun setPic(){
        // 例外処理
        if(uri == null) Picasso.with(applicationContext).load(R.drawable.ic_baseline_image_not_supported_24).fit().centerInside().rotate(rotate).into(imageView)
        // 画像を表示
        else Picasso.with(applicationContext).load(uri).fit().centerInside().rotate(rotate).into(imageView)
    }
}
