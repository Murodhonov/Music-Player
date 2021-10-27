package Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.rv_item.view.*
import uz.umarxon.mediaplayer.Modles.Music
import uz.umarxon.mediaplayer.R

class RvAdapter(var context: Context, var list: List<Music>, var rvItemClick: RvItemClick)
    :RecyclerView.Adapter<RvAdapter.Vh>(){

    inner class Vh(itemView:View):RecyclerView.ViewHolder(itemView){
        fun onBind(music: Music, position:Int){
            itemView.author_name.text = music.audioArtist
            itemView.song_name.text = music.audioTitle
            if (music.image!=""){
                val bm = BitmapFactory.decodeFile(music.image)
                itemView.song_image.setImageBitmap(bm)
            }
            itemView.setOnClickListener {
                rvItemClick.itemClick(music, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}
interface RvItemClick{
    fun itemClick(music: Music, position: Int)
}