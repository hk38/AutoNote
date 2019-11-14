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
import androidx.fragment.app.FragmentPagerAdapter
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

        Realm.init(this)
        val realm = Realm.getDefaultInstance()

        // データがない場合作成
        if(realm.where(ClassData::class.java).findAll().isEmpty()) setUpClass(realm)
        if(realm.where(SettingData::class.java).findAll().isEmpty()) setUpSetting(realm)
        if(realm.where(OptionData::class.java).findAll().isEmpty()) setUpOption(realm)

        // Fragment周りの処理
        val fragmentAdapter = FragmentAdapter(this, supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        view_pager.adapter = fragmentAdapter
        tabs.setupWithViewPager(view_pager)

        // FABタップ時に写真撮影
        fab.setOnClickListener { cameraTask() }

        fab.setOnLongClickListener {
            // ラズパイ経由での写真取得処理を記述

            true
        }

        // 設定ボタンタップ時設定画面に遷移
        imageButtonOption.setOnClickListener{
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    // 空の授業データを作成
    fun setUpClass(realm: Realm){
        realm.executeTransaction {
            for(i in 0..6){
                for(j in 0..6){
                    val classData: ClassData = realm.createObject(ClassData::class.java, i*10+j)
                    classData.className = ""
                    classData.place = ""
                    classData.teacherName = ""
                    classData.pictureData = null
                }
            }
        }
    }

    // 空の時間設定データを作成
    fun setUpSetting(realm: Realm){
        realm.executeTransaction {
            for(i in 0..6){
                val startTime: SettingData = realm.createObject(SettingData::class.java, i*10+0)
                startTime.hour = "00"
                startTime.minute = "00"

                val endTime: SettingData = realm.createObject(SettingData::class.java, i*10+1)
                endTime.hour = "00"
                endTime.minute = "00"
            }
        }
    }

    // 空のアプリ設定データを作成
    fun setUpOption(realm: Realm){
        realm.executeTransaction{
            val optionData: OptionData = realm.createObject(OptionData::class.java, 0)
            optionData.numOfWeek = 5
            optionData.numOfTime = 4
        }
    }

    // 写真撮影時の結果を処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            // 設定された時間外の写真はアプリに保存しない
            val id = getID()
            if(id > 66) return

            // 画像内文字の識別を実行
            val text = ""
            // 保存処理
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction{
                val classData = realm.where(ClassData::class.java).equalTo("id", id).findFirst()
                if(classData != null) {
                    val photoData = realm.createObject(PictureData::class.java)
                    photoData.pass = pictureUri.toString()
                    photoData.text = text
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

    // 写真撮影処理
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

    // IDの取得処理
    fun getID(): Int{
        val cal = Calendar.getInstance()
        val realm = Realm.getDefaultInstance()
        val opt = realm.where(OptionData::class.java).equalTo("key", 0).findFirst().numOfWeek
        var temp = 100

        // 設定された時間データから撮影時刻と一致する時間帯を見つける
        for(i in 0 until realm.where(OptionData::class.java).equalTo("key", 0).findFirst().numOfTime ){
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

        // 曜日を加えてIDを算出
        return when {
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY -> 0
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY -> 10
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY -> 20
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY -> 30
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY -> 40
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && opt > 5 -> 50
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && opt > 6 -> 60
            else -> 70
        } + temp
    }

}