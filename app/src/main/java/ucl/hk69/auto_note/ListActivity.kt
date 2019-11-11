package ucl.hk69.auto_note

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
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
        val classData = realm.where(ClassData::class.java).equalTo("id", intent.getIntExtra("ID", 0)).findFirst()

        var i = 0
        var row = makeRowLL()
        classData.pictureData?.forEach { item ->
            if(i%3 == 0){
                row = makeRowLL()
                listLL.addView(row)
            }

            val iv = makeSquareIV()
            iv.setImageURI(item.pass?.toUri())
            iv.setOnClickListener{
                intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("uri", item.pass)
                startActivity(intent)
            }

            row.addView(iv)
            i++
        }
    }

    fun makeRowLL(): LinearLayout{
        val linearLayout = LinearLayout(this)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        linearLayout.layoutParams = params
        return linearLayout
    }

    fun makeSquareIV(): ImageView{
        val imageView = ImageView(this)
        val params = LinearLayout.LayoutParams(Resources.getSystem().displayMetrics.widthPixels.div(3), Resources.getSystem().displayMetrics.widthPixels.div(3))

        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return imageView
    }

}
