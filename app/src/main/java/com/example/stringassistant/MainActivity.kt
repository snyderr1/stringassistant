package com.example.stringassistant

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stringassistant.ui.theme.StringAssistantTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.media.AudioRecord
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //create audiController



        enableEdgeToEdge()
        setContent {
            StringAssistantTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeLayout(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeLayout(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var displayValue = remember { mutableStateOf("") }
    val microphonePermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    if (microphonePermissionState.status.isGranted) {
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_8BIT
        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)


        val audioController = AudioReader(AudioRecord(MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize))

        SoundTranscriber(modifier = modifier, audioDevice = audioController, displayValue)
    } else {
        Text("This don't work without microphone")
        Button(onClick = { microphonePermissionState.launchPermissionRequest() }) {
            Text("Give me permission NOW")
        }
    }
}

@Composable
fun SoundTranscriber(modifier: Modifier = Modifier, audioDevice: AudioReader, displayValue: MutableState<String>) {
    val audioInput = remember { audioDevice }
    Column(
        modifier = Modifier.padding(100.dp).fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        RecordButton(
            label = "Start Recording",
            modifier = modifier,
            { audioDevice.startRecording() },
            { audioDevice.stopRecording() })
        Text(
            text = displayValue.value,
            modifier = modifier,
        )

    }
}


@Composable
fun RecordButton(
    label: String,
    modifier: Modifier = Modifier,
    audioInputStart: ()-> Int,
    audioInputStop: ()-> Int) {
    var isRecording = remember {mutableStateOf(false)}
    var audioData = remember {mutableIntStateOf(0)}
    Button(onClick = {
        var checkSuccess = 0
        if(isRecording.value){
            checkSuccess = audioInputStop()
        } else {
            checkSuccess = audioInputStart()
        }
        if(checkSuccess == 0){
            isRecording.value = !(isRecording.value)
        }
    }){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (!isRecording.value) label else "Stop",
                modifier = modifier,
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(id=R.drawable.record_icon),
                "ES A GUITAR MANE",
            )
        }

    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    StringAssistantTheme {
        HomeLayout(modifier = Modifier.fillMaxWidth())
    }
}

