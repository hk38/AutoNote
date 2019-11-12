package ucl.hk69.auto_note

import android.net.Uri
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SettingData: RealmObject(){
    @PrimaryKey
    open var id: Int = 0
    open var hour: String = ""
    open var minute: String = ""
}

open class PictureData: RealmObject(){
    open var text: String = ""
    open var pass: String = ""
}

open class ClassData: RealmObject() {
    @PrimaryKey
    open var id: Int = 0
    open var className: String = ""
    open var teacherName: String = ""
    open var place: String = ""
    open var pictureData: RealmList<PictureData>? = null
}

open class OptionData: RealmObject() {
    @PrimaryKey
    open var key: Int = 0
    open var numOfWeek = 5
    open var numOfTime = 4
}