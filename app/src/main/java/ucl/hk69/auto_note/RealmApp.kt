package ucl.hk69.auto_note

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class RealmApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}