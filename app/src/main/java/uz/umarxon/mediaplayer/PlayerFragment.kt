package uz.umarxon.mediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gauravk.audiovisualizer.visualizer.WaveVisualizer
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import uz.umarxon.mediaplayer.Modles.Music
import uz.umarxon.mediaplayer.MyData.list

class PlayerFragment : Fragment() {

    lateinit var root: View
    lateinit var music: Music
    var position: Int = 0
    var mediaPlayer: MediaPlayer? = null
    lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        root = inflater.inflate(R.layout.fragment_player, container, false)

        position = arguments?.getInt("position", -1)!!
        music = arguments?.getSerializable("music") as Music

        root.current_music_name.isSelected = true

        root.menu_back123.setOnClickListener {
            findNavController().popBackStack()
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        if (position != -1) {
            mediaPlayer = null
            mediaPlayer = MediaPlayer.create(context, list[position].audioUri)
            mediaPlayer?.start()

            root.pause.background = resources.getDrawable(R.drawable.pause)
            root.song_progress.max = mediaPlayer?.duration!!
            root.sound_progress.progressMax = mediaPlayer?.duration!!.toFloat()
            handler = Handler(activity?.mainLooper!!)

            root.all_song_size.text = list.size.toString()
            root.current_music_position.text = (position + 1).toString()

            root.current_author_name.text = list[position].audioArtist
            root.current_music_name.text = list[position].audioTitle

            root.current_music_max_time.text = milliSecondsToTimer(mediaPlayer?.duration!!.toLong())
        }
        if (mediaPlayer?.isPlaying!!) {
            handler.postDelayed(runnable, 100)
        }
        var pos:Int? = null

        root.song_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    pos = progress
                }
                root.sound_progress.progress = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer?.seekTo(pos!!)
            }
        })

        root.minus_30.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition!!.minus(30000))
        }
        root.plus_30.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition!!.plus(30000))
        }


        root.pause.setOnClickListener {
            if (mediaPlayer?.isPlaying!!) {
                mediaPlayer?.pause()
                root.pause.background = resources.getDrawable(R.drawable.play)
            } else {
                mediaPlayer?.start()
                root.pause.background = resources.getDrawable(R.drawable.pause)
            }
        }
        root.next.setOnClickListener {
            if (++position < list.size) {
                releaseMP()
                onResume()
            } else {
                position = 0
                releaseMP()
                onResume()
            }
        }
        root.prev.setOnClickListener {
            if (--position >= 0) {
                releaseMP()
                onResume()
            } else {
                position = list.size - 1
                releaseMP()
                onResume()
            }
        }
    }

    //app stop when music stop
    private fun releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    override fun onDetach() {
        super.onDetach()
        if(mediaPlayer != null) releaseMP()
    }

    override fun onPause() {
        super.onPause()
        if(mediaPlayer != null) {
            mediaPlayer!!.pause()
        }
    }

    private var runnable = object : Runnable {
        override fun run() {

            if (mediaPlayer != null) {
                root.song_progress.progress = mediaPlayer?.currentPosition!!
                root.current_time_music.text =
                    milliSecondsToTimer(mediaPlayer?.currentPosition!!.toLong())
                if (root.current_time_music.text.toString() == root.current_music_max_time.text.toString()) {
                    releaseMP()
                    if (++position < list.size) {
                        releaseMP()
                        onResume()
                    } else {
                        position = 0
                        releaseMP()
                        onResume()
                    }
                }
                handler.postDelayed(this, 100)
            }
        }
    }

    fun milliSecondsToTimer(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // return timer string
        return finalTimerString
    }


}