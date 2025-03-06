package com.example.stringassistant

import android.media.AudioRecord
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.IOException

class AudioReader(recorder: AudioRecord) {

    private var audioInputDevice = recorder
    private var audioBuffer = CircularBuffer(3 * 44100)
    private lateinit var currentSession: Job



    public fun startRecording(): Int {
        val audioDevice = this.audioInputDevice

        if (this.audioInputDevice.recordingState == AudioRecord.RECORDSTATE_STOPPED) {
            this.audioInputDevice.startRecording()
            try {
                this.currentSession = CoroutineScope(Dispatchers.IO).launch {
                    audioStreamReader(audioDevice).collect()
                }
                return 0

            } catch (e: IOException) {
                e.message?.let { Log.e("Start Failure", it) }
            }
        }
        return 1
    }


    private fun audioStreamReader(audioDevice: AudioRecord): Flow<ByteArray> = flow{
        var audioData = ByteArray(44100)
        var curr = 0
        var err = 0
        while(err >= 0) {

            err = audioDevice.read(audioData, curr, 44100, AudioRecord.READ_NON_BLOCKING)
            audioBuffer.writeBlock(audioData)
            delay(1000)
            //Log.i("TAG", byteArrayWriter(audioData))

            curr += 1
        }
    }
    

    private fun byteArrayWriter(rawAudioArray: ByteArray): String{
        var stringBase = ""
        for(i in rawAudioArray.indices){
            if(i%8==0){
                stringBase += "["
            }

            stringBase += rawAudioArray[i].toString()
            if(i%8 == 7) {
                stringBase += "]\n"
            } else if(i != rawAudioArray.size-1){
                stringBase += ", "
            }
        }
        return stringBase
    }

    public fun stopRecording(): Int {
        if (this.audioInputDevice.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            try {
                this.audioInputDevice.stop()
                this.currentSession.cancel()
                return 0
            } catch (e: IOException) {
                e.message?.let { Log.e("Stop Failure", it) }
            }
        }
        return 1
    }


}
