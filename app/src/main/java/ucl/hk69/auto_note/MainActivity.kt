package ucl.hk69.auto_note

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

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

        if(realm.where(ClassData::class.java).findAll().isEmpty()) setUpClass(realm)

        if(realm.where(SettingData::class.java).findAll().isEmpty()) setUpSetting(realm)

        fab.setOnClickListener { cameraTask() }

        fab.setOnLongClickListener {

            true
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
                class1st.className = ""
                class1st.place = ""
                class1st.teacherName = ""
                class1st.pictureData = null

                val class2nd: ClassData = realm.createObject(ClassData::class.java, i*10+1)
                class2nd.className = ""
                class2nd.place = ""
                class2nd.teacherName = ""
                class2nd.pictureData = null

                val class3rd: ClassData = realm.createObject(ClassData::class.java, i*10+2)
                class3rd.className = ""
                class3rd.place = ""
                class3rd.teacherName = ""
                class3rd.pictureData = null

                val class4th: ClassData = realm.createObject(ClassData::class.java, i*10+3)
                class4th.className = ""
                class4th.place = ""
                class4th.teacherName = ""
                class4th.pictureData = null
            }
        }
    }

    fun setUpSetting(realm: Realm){
        realm.executeTransaction {
            for(i in 0..3){
                val startTime: SettingData = realm.createObject(SettingData::class.java, i*10+0)
                startTime.hour = "00"
                startTime.minute = "00"

                val endTime: SettingData = realm.createObject(SettingData::class.java, i*10+1)
                endTime.hour = "00"
                endTime.minute = "00"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction{
                val classData = realm.where(ClassData::class.java).equalTo("id", getID()).findFirst()
                if(classData != null) {
                    val photoData = realm.createObject(PictureData::class.java)
                    photoData.pass = pictureUri.toString()
                    classData.pictureData?.add(photoData)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION) {
            // requestPermissionsで設定した順番で結果が格納されています。
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 許可されていない
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // すでに１度パーミッションのリクエストが行われていて、
                // ユーザーに「許可しない（二度と表示しないは非チェック）」をされていると
                // この処理が呼ばれます。
                Toast.makeText(this, "パーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                Toast.makeText(this, "カメラのパーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "ストレージ書き込みのパーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()
            } else {
                // パーミッションのリクエストを表示
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION)
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

    fun getID(): Int{
        val cal = Calendar.getInstance()
        val realm = Realm.getDefaultInstance()
        var temp = 9

        for(i in 0..3){
            val startTimeData = realm.where(SettingData::class.java).equalTo("id", i*10+0).findFirst()
            val endTimeData = realm.where(SettingData::class.java).equalTo("id", i*10+1).findFirst()
            val nowTime = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE)
            val startTime = Integer.parseInt("${startTimeData.hour}${startTimeData.minute}")
            val endTime = Integer.parseInt("${endTimeData.hour}${endTimeData.minute}")

            if( nowTime in startTime..endTime){
                temp = i
                break
            }
        }

        return when(cal.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 10
            Calendar.WEDNESDAY -> 20
            Calendar.THURSDAY -> 30
            Calendar.FRIDAY -> 40
            else -> 0
        } + temp
    }

}