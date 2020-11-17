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

        // 授業データを取得
        val classData = realm.where(ClassData::class.java).equalTo("id", intent.getIntExtra("ID", 0)).findFirst()
        // アプリデータから背景色を設定
        val bgColor = realm.where(OptionData::class.java).equalTo("key", 0.toInt()).findFirst()?.bgColor ?: "f6ae54"
        listActContainer.setBackgroundColor(Color.parseColor("#$bgColor"))

        // 削除ダイアログを作成
        val builder = AlertDialog.Builder(this).apply {
            setTitle("写真の削除")
            setMessage("アプリから写真を削除しますか？")
            setNegativeButton("No"){_, _ ->}
        }

        adapter = PicListAdapter(this, classData?.pictureData, object: PicListAdapter.OnItemClickListener{
            // アイテムタップ時の操作
            override fun onItemClick(item: PictureData) {
                // 詳細画面に遷移
                intent = Intent(applicationContext, DetailActivity::class.java)
                intent.putExtra("uri", item.pass)
                startActivity(intent)
            }
        }, object: PicListAdapter.OnItemLongClickListener{
            // 長押し時の操作
            override fun onItemLongClick(item: PictureData) {
                // 削除ダイアログを表示し，OKが選択された場合データベースから削除
                builder.setPositiveButton("OK") { _, _ ->
                    realm.executeTransaction {
                        realm.where(PictureData::class.java).equalTo("pass", item.pass).findAll().deleteAllFromRealm()
                    }
                }.show()
            }
        }, true)

        // RecyclerViewにAdapterを設定
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(applicationContext, calcSpan())
        recyclerView.adapter = adapter
    }

    // 画面サイズから一行あたりのアイテム数を算出
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
