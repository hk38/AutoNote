package ucl.hk69.auto_note

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        // データを取得
        val realm = Realm.getDefaultInstance()
        val classData = realm.where(ClassData::class.java).equalTo("id", intent.getIntExtra("ID", 0)).findFirst()

        // LinearLayoutに画像を配置
        // 将来的にはGridLayoutに変更
        var i = 0
        var row = makeRowLL()
        classData.pictureData?.forEach { item ->
            if(i%3 == 0){
                row = makeRowLL()
                list.addView(row)
            }

            // 大量の画像を読み込むことになるのでPicassoを利用
            val iv = makeSquareIV()
            Picasso.with(this).load(item.pass.toUri()).fit().centerCrop().into(iv)
            // タップ時詳細画面に遷移
            iv.setOnClickListener{
                intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("uri", item.pass)
                startActivity(intent)
            }

            row.addView(iv)
            i++
        }
    }

    // 行のLinearLayout取得
    fun makeRowLL(): LinearLayout{
        val linearLayout = LinearLayout(this)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        linearLayout.layoutParams = params
        return linearLayout
    }

    // 正方形のImageViewを作成
    fun makeSquareIV(): ImageView{
        val imageView = ImageView(this)
        val params = LinearLayout.LayoutParams(Resources.getSystem().displayMetrics.widthPixels.div(3), Resources.getSystem().displayMetrics.widthPixels.div(3))

        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return imageView
    }

}
