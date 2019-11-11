package ucl.hk69.auto_note

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        imageView.setImageURI(intent.getStringExtra("uri").toUri())
    }

}
