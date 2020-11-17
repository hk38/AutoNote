package ucl.hk69.auto_note

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    val OPTION = 3
    var pictureUri: Uri? = null
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bootFlag = false

        // データがない場合は初回起動時の処理
        if(realm.where(ClassData::class.java).findAll().isEmpty()) bootFlag = setUpClass()
        if(realm.where(SettingData::class.java).findAll().isEmpty()) bootFlag = setUpSetting()
        if(realm.where(OptionData::class.java).findAll().isEmpty()) bootFlag = setUpOption()
        
        if(bootFlag){
            AlertDialog.Builder(this)
                .setTitle("使い方")
                .setMessage("授業をタップするとアルバムが開きます\n" +
                        "長押しすると授業内容が編集できます\n" +
                        "歯車ボタンから設定画面が変更できます\n" +
                        "カメラボタンで写真を撮ります\n" +
                        "詳細な操作方法はストアのアプリ概要をご覧ください")
                .setPositiveButton("OK"){_, _ -> }
                .show()
        }

        // Fragment周りの処理
        val fragmentAdapter = FragmentAdapter(applicationContext, supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        view_pager.adapter = fragmentAdapter
        tabs.setupWithViewPager(view_pager)

        // アプリデータから背景色を取得して設定
        val bgColor = realm.where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()?.bgColor ?: "f6ae54"
        coordinatorLayout.setBackgroundColor(Color.parseColor("#$bgColor"))

        view_pager.currentItem = if(getDayOfWeek() < 7) getDayOfWeek() else 0

        // FABタップ時に写真撮影
        fab.setOnClickListener { cameraTask() }

        // 設定ボタンタップ時設定画面に遷移
        imageButtonOption.setOnClickListener{
            val intent = Intent(applicationContext, SettingActivity::class.java)
            startActivityForResult(intent, OPTION)
        }
    }

    // 空の授業データを作成
    private fun setUpClass(): Boolean{
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
        
        return true
    }

    // 空の時間設定データを作成
    private fun setUpSetting(): Boolean{
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
        
        return true
    }

    // 空のアプリ設定データを作成
    private fun setUpOption(): Boolean{
        realm.executeTransaction{
            val optionData: OptionData = realm.createObject(OptionData::class.java, 0)
            optionData.numOfWeek = 5
            optionData.numOfTime = 4
            optionData.bgColor = "f6ae54"
        }
        
        return true
    }

    // 写真撮影時の結果を処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            // 設定された時間外の写真はアプリに保存しない
            val id = getID()
            if(id > 66) return

            // 保存処理
            realm.executeTransaction{
                val classData = realm.where(ClassData::class.java).equalTo("id", id).findFirst()
                if(classData != null) {
                    val photoData = realm.createObject(PictureData::class.java)
                    photoData.pass = pictureUri.toString()
                    classData.pictureData?.add(photoData)
                }
            }
        }else if(requestCode == OPTION && resultCode == Activity.RESULT_OK){
            // アプリ設定が変更されていた場合，表示を変更するため一度アプリを終了する
            if(data?.getBooleanExtra("optCheck", false) == true) finishAndRemoveTask()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION) {
            // requestPermissionsで設定した結果が格納
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 許可されたので処理を続行
                takePicture()
            } else {
                // パーミッションリクエストが許可されなかった場合に表示
                Toast.makeText(applicationContext, "パーミッションが許可されていません。", Toast.LENGTH_SHORT).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun cameraTask() {
        // カメラの権限の確認
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 許可されていない
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // パーミッションリクエストが拒否されている場合
                Toast.makeText(applicationContext, "パーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                Toast.makeText(applicationContext, "カメラのパーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(applicationContext, "ストレージ書き込みのパーミッションがOFFになっています。", Toast.LENGTH_SHORT).show()
            } else {
                // パーミッションのリクエストを表示
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION)
            }
            return
        }
        // 許可されている，またはAndroid 6.0以前
        takePicture()
    }

    // 写真撮影処理
    private fun takePicture() {
        // ファイル名とURIを作成
        val fileName = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        pictureUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        // カメラを起動
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
        startActivityForResult(intent, CAMERA)
    }

    // IDの取得処理
    private fun getID(): Int{
        val cal = Calendar.getInstance()

        // 設定された時間データから撮影時刻と一致する時間帯を見つける
        for(i in 0 until (realm.where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()?.numOfTime ?: 4) ){
            val startTimeData = realm.where(SettingData::class.java).equalTo("id", i*10+0).findFirst()
            val endTimeData = realm.where(SettingData::class.java).equalTo("id", i*10+1).findFirst()
            val nowTime = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE)
            val startTime = Integer.parseInt("${startTimeData?.hour}${startTimeData?.minute}")
            val endTime = Integer.parseInt("${endTimeData?.hour}${endTimeData?.minute}")

            if( nowTime in startTime..endTime){
                // 曜日を加えてIDを算出
                return getDayOfWeek() * 10 + i
            }
        }

        return 100
    }

    // 曜日の取得処理
    private fun getDayOfWeek(): Int{
        val cal = Calendar.getInstance()
        // 設定データの一週間の設定を取得
        val opt = realm.where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()?.numOfWeek ?: 5

        return when {
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY -> 0
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY -> 1
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY -> 2
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY -> 3
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY -> 4
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && opt > 5 -> 5
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && opt > 6 -> 6
            else -> 7
        }
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}