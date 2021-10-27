package uz.umarxon.mediaplayer.Modles

import android.net.Uri
import java.io.Serializable

class Music : Serializable {

    var id: Long? = null
    var audioTitle: String? = null
    var audioDuration: String? = null
    var audioArtist: String? = null
    var audioUri: Uri? = null
    var image:String? = null


    constructor()

    constructor(
        id: Long?,
        audioTitle: String?,
        audioDuration: String?,
        audioArtist: String?,
        audioUri: Uri?,
        image: String?,
    ) {
        this.id = id
        this.audioTitle = audioTitle
        this.audioDuration = audioDuration
        this.audioArtist = audioArtist
        this.audioUri = audioUri
        this.image = image
    }

    constructor(
        audioTitle: String?,
        audioDuration: String?,
        audioArtist: String?,
        audioUri: Uri?,
        image: String?,
    ) {
        this.audioTitle = audioTitle
        this.audioDuration = audioDuration
        this.audioArtist = audioArtist
        this.audioUri = audioUri
        this.image = image
    }
}