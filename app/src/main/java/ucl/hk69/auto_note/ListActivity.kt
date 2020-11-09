package ucl.hk69.auto_note

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    var adapter: PicListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        val classData = realm.where(ClassData::class.java).equalTo("id", intent.getIntExtra("ID", 0)).findFirst()
        val bgColor = realm.where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()?.bgColor ?: "f6ae54"
        listActContainer.setBackgroundColor(Color.parseColor("#$bgColor"))

        val builder = AlertDialog.Builder(this).apply {
            setTitle("写真の削除")
            setMessage("アプリから写真を削除しますか？")
            setNegativeButton("No"){_, _ ->}
        }

        adapter = PicListAdapter(this, classData?.pictureData, object: PicListAdapter.OnItemClickListener{
            override fun onItemClick(item: PictureData) {
                intent = Intent(applicationContext, DetailActivity::class.java)
                intent.putExtra("uri", item.pass)
                startActivity(intent)
            }
        }, object: PicListAdapter.OnItemLongClickListener{
            override fun onItemLongClick(item: PictureData) {
                builder.setPositiveButton("OK") { _, _ ->
                    realm.executeTransaction {
                        realm.where(PictureData::class.java).equalTo("pass", item.pass).findAll().deleteAllFromRealm()
                    }
                }.show()
            }
        }, true)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(applicationContext, calcSpan())
        recyclerView.adapter = adapter
    }



    private fun calcSpan(): Int{
        val dm = resources.displayMetrics
        val width = dm.widthPixels / dm.density

        return (width / 90).toInt()
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}
