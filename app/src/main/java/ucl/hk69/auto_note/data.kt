package ucl.hk69.auto_note

import android.net.Uri
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SettingData : RealmObject(){
    @PrimaryKey
    open var id: Int = 0
    open var time: String? = null
}

open class PictureData : RealmObject(){
    open var text: String? = null
    open var pass: String? = null
}

open class ClassData : RealmObject() {
    @PrimaryKey
    open var id: Int = 0
    open var className: String? = null
    open var teacherName: String? = null
    open var place: String? = null
    open var pictureData: RealmList<PictureData>? = null
}