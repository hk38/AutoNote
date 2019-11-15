package ucl.hk69.auto_note

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// 設定のデータ
open class SettingData: RealmObject(){
    // 10の位[0~6]:1〜7限を表す．
    //  1の位[0,1]:開始時間か終了時間かを表す
    @PrimaryKey
    open var id: Int = 0
    // 時間を格納
    open var hour: String = ""
    open var minute: String = ""
}

// 写真のデータ
open class PictureData: RealmObject(){
    // パスを保持
    open var pass: String = ""
}

// 授業のデータ
open class ClassData: RealmObject() {
    // 10の位[0~6]:月〜日を表す
    //  1の位[0~6]:1〜7限を表す
    @PrimaryKey
    open var id: Int = 0
    // 科目名
    open var className: String = ""
    // 担当教員名
    open var teacherName: String = ""
    // 場所
    open var place: String = ""
    // メモ
    open var memo: String = ""
    // 写真のデータをリストで保持
    open var pictureData: RealmList<PictureData>? = null
}

// アプリのデータ
open class OptionData: RealmObject() {
    // 0固定
    @PrimaryKey
    open var key: Int = 0
    // 表示する曜日の数
    open var numOfWeek = 5
    // 表示する時間の数
    open var numOfTime = 4
}