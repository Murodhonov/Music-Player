package uz.umarxon.mediaplayer

import Adapter.RvAdapter
import Adapter.RvItemClick
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.florent37.runtimepermission.kotlin.askPermission
import kotlinx.android.synthetic.main.fragment_home.view.*
import uz.umarxon.mediaplayer.Modles.Music
import android.content.ContentResolver
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    lateinit var root: View
    private var audioArrayList = ArrayList<Music>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false)

        askPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
            //all permissions already granted or just granted
            getAudioFiles()
            val musicList = audioArrayList
            MyData.list = audioArrayList
            val adapter = RvAdapter(root.context,musicList, object : RvItemClick {
                override fun itemClick(music: Music, position: Int) {
                    findNavController().navigate(R.id.playerFragment,
                        bundleOf("music" to music, "position" to position))
                }
            })
            root.rv.adapter = adapter

        }.onDeclined { e ->
            if (e.hasDenied()) {

                Toast.makeText(context, "You must accept permission elsewhere app can't get songs from your device !", Toast.LENGTH_LONG).show()
                e.askAgain()
            }

            if (e.hasForeverDenied()) {
                //the list of forever denied permissions, user has check 'never ask again'

                // you need to open setting manually if you really need it
                e.goToSettings();
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(root.context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            getAudioFiles()
            val adapter = RvAdapter(root.context,audioArrayList, object : RvItemClick {
                override fun itemClick(music: Music, position: Int) {
                    findNavController().navigate(R.id.playerFragment,
                        bundleOf("music" to music, "position" to position))
                }
            })
            root.rv.adapter = adapter
        }
    }

    //fetch the audio files from storage
    @SuppressLint("Range", "Recycle")
    fun getAudioFiles() {
        audioArrayList = ArrayList()
        val contentResolver: ContentResolver = activity?.contentResolver!!
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val imageId: Int = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                var image = ""
                if (imageId != -1) {
                    image = cursor.getString(imageId)
                }
                val modelAudio = Music()
                modelAudio.audioTitle = title
                modelAudio.audioArtist = artist
                modelAudio.audioUri = Uri.parse(url)
                modelAudio.audioDuration = duration
                modelAudio.image = image
                audioArrayList.add(modelAudio)
            } while (cursor.moveToNext())
        }
        root.rv.adapter = RvAdapter(root.context,audioArrayList,object : RvItemClick{
            override fun itemClick(music: Music, position: Int) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

object MyData {
    var list: ArrayList<Music> = ArrayList()
}