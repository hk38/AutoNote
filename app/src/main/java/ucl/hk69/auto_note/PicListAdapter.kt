package ucl.hk69.auto_note

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class PicListAdapter(private val context: Context,
                     private var picList: OrderedRealmCollection<PictureData>?,
                     private var clickListener: OnItemClickListener,
                     private var longClickListener: OnItemLongClickListener,
                     private var autoUpdate: Boolean):
    RealmRecyclerViewAdapter<PictureData, PicListAdapter.ViewHolder>(picList, autoUpdate) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicListAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_pic_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: PicListAdapter.ViewHolder, position: Int) {
        val item = picList?.get(position) ?: return

        // タップ時の動作を設定
        holder.container.setOnClickListener{
            clickListener.onItemClick(item)
        }
        // 長押し時の動作を設定
        holder.container.setOnLongClickListener {
            longClickListener.onItemLongClick(item)
            true
        }

        // 画像を設定
        holder.imageView.setImageURI(item.pass.toUri())
    }

    // リストの要素数を返す
    override fun getItemCount(): Int = picList?.size ?: 0

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.listItemPicImage)
        val container: CardView = view.findViewById(R.id.cardView)
    }

    interface OnItemClickListener {
        fun onItemClick(item: PictureData)
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(item: PictureData)
    }
}