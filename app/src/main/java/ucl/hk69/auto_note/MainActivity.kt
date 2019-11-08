package ucl.hk69.auto_note

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val CAMERA = 1
    val PERMISSION = 2
    var pictureUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentAdapter = FragmentAdapter(this, supportFragmentManager)
        view_pager.adapter = fragmentAdapter
        tabs.setupWithViewPager(view_pager)

        Realm.init(this)
        val realm = Realm.getDefaultInstance()

        val resultClass = realm.where(ClassData::class.java).findAll()
        if(resultClass.isEmpty()) setUpClass(realm)

        val resultSetting = realm.where(SettingData::class.java).findAll()
        if(resultSetting.isEmpty()) setUpSetting(realm)

        fab.setOnClickListener { view ->
            cameraTask()
        }

        imageButtonOption.setOnClickListener{
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    fun setUpClass(realm: Realm){
        realm.executeTransaction {
            for(i in 0..4){
                val class1st: ClassData = realm.createObject(ClassData::class.java, i*10+0)
                class1st.className = "授業名${i*10+0}"
                class1st.place = "場所"
                class1st.teacherName = "担当教員名"
                class1st.pictureData = null

                val class2nd: ClassData = realm.createObject(ClassData::class.java, i*10+1)
                class2nd.className = "授業名${i*10+1}"
                class2nd.place = "場所"
                class2nd.teacherName = "担当教員名"
                class2nd.pictureData = null

                val class3rd: ClassData = realm.createObject(ClassData::class.java, i*10+2)
                class3rd.className = "授業名${i*10+2}"
                class3rd.place = "場所"
                class3rd.teacherName = "担当教員名"
                class3rd.pictureData = null

                val class4th: ClassData = realm.createObject(ClassData::class.java, i*10+3)
                class4th.className = "授業名${i*10+3}"
                class4th.place = "場所"
                class4th.teacherName = "担当教員名"
                class4th.pictureData = null
            }
        }
    }

    fun setUpSetting(realm: Realm){
        realm.executeTransaction {
            for(i in 0..3){
                val startTime: SettingData = realm.createObject(SettingData::class.java, i*10+0)
                startTime.time = null

                val endTime: SettingData = realm.createObject(SettingData::class.java, i*10+1)
                endTime.time = null
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
//            photoImageView.setImageURI(pictureUri)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION) {
            // requestPermissionsで設定した順番で結果が格納されています。
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 許可されたので処理を続行
                takePicture()
            } else {
                // パーミッションのリクエストに対して「許可しない」
                // または以前のリクエストで「二度と表示しない」にチェックを入れられた状態で
                // 「許可しない」を押されていると、必ずここに呼び出されます。
                Toast.makeText(this, "パーミッションが許可されていません。", Toast.LENGTH_SHORT).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun cameraTask() {
        // カメラの権限の確認
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 許可されていない
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                // すでに１度パーミッションのリクエストが行われていて、
                // ユーザーに「許可しない（二度と表示しないは非チェック）」をされていると
                // この処理が呼ばれます。
                Toast.makeText(this, "パーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()
            } else {
                // パーミッションのリクエストを表示
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION
                )
            }
            return
        }
        // 許可されている、またはAndroid 6.0以前
        takePicture()
    }

    fun takePicture() {
        val fileName: String = "${System.currentTimeMillis()}.jpg"
        val contentValues: ContentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        pictureUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
        startActivityForResult(intent, CAMERA)
    }

}