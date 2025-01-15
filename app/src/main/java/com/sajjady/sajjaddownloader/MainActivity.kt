package com.sajjady.sajjaddownloader

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sajjady.sajjaddownloader.downloader.OnCancelListener
import com.sajjady.sajjaddownloader.downloader.OnDownloadListener
import com.sajjady.sajjaddownloader.downloader.PRDownloader
import com.sajjady.sajjaddownloader.downloader.PRDownloaderConfig
import com.sajjady.sajjaddownloader.downloader.Status
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var editTextUrl: EditText
    private var path: String = ""
    private lateinit var file_downloaded_path: TextView
    private lateinit var file_name: TextView
    private lateinit var downloading_percent: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnStart: Button

    private lateinit var btnCancel: Button
    private lateinit var buttonDownload: Button
    private lateinit var details: LinearLayout
    var downloadID: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        /*
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }
        */

        // Initializing PRDownloader library

        path =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/Dans/").path
        PRDownloader.initialize(this);

        PRDownloaderConfig.newBuilder().setDatabaseEnabled(true).build()

        // finding edittext by its id
        editTextUrl = findViewById(R.id.url_etText);


        val hotel =
            "http://480cdn.silingvid.xyz/480p/Movies/2021/M/Monster_Pets_A_Hotel_Transylvania_Short_Film_2021_480p_Web-Dl_Filamingo.mkv"

        val mrbean =
            "http://480cdn.silingvid.xyz/480p/Series/M/Man_Vs_Bee/Man.Vs.Bee.S01E01.480p.WEB-DL.mkv"
        editTextUrl.setText(hotel)

        // finding button by its id
        buttonDownload = findViewById(R.id.btn_download);

        // finding textview by its id
        file_downloaded_path = findViewById(R.id.txt_url);

        // finding textview by its id
        file_name = findViewById(R.id.file_name);

        // finding progressbar by its id
        progressBar = findViewById(R.id.progress_horizontal);

        // finding textview by its id
        downloading_percent = findViewById(R.id.downloading_percentage);

        // finding button by its id
        btnStart = findViewById(R.id.btn_start);

        // finding button by its id
        btnCancel = findViewById(R.id.btn_stop);

        // finding linear layout by its id
        details = findViewById(R.id.details_box);


        buttonDownload.setOnClickListener {
            val url = editTextUrl.getText().toString().trim()
            details.setVisibility(View.VISIBLE);
            downloadFile(url);

        }


    }

    @SuppressLint("SetTextI18n")
    private fun downloadFile(url: String) {
        // handing click event on start button
        // which starts the downloading of the file


        btnStart.setOnClickListener(View.OnClickListener {
            // checks if the process is already running
            if (Status.RUNNING == PRDownloader.getStatus(downloadID)) {
                // pauses the download if
                // user click on pause button
                PRDownloader.pause(downloadID)
                return@OnClickListener
            }


            // enabling the start button
            btnStart.isEnabled = false


            // checks if the status is paused
            if (Status.PAUSED == PRDownloader.getStatus(downloadID)) {
                // resume the download if download is paused
                PRDownloader.resume(downloadID)
                return@OnClickListener
            }


            // getting the filename
            val fileName = URLUtil.guessFileName(url, null, null)


            // setting the file name
            file_name.text = "Downloading $fileName"


            // making the download request
            downloadID = PRDownloader.download(url, path, fileName)
                .build()
                .setOnStartOrResumeListener {
                    progressBar.isIndeterminate = false
                    // enables the start button
                    btnStart.isEnabled = true
                    // setting the text of start button to pause
                    btnStart.text = "Pause"
                    // enabling the stop button
                    btnCancel.isEnabled = true
                    Toast.makeText(
                        this@MainActivity,
                        "Downloading started",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setOnPauseListener { // setting the text of start button to resume
                    // when the download is in paused state
                    btnStart.text = "Resume"
                    Toast.makeText(
                        this@MainActivity,
                        "Downloading Paused",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setOnCancelListener(object : OnCancelListener {
                    override fun onCancel() {
                        // resetting the downloadId when
                        // the download is cancelled
                        downloadID = 0
                        // setting the text of start button to start
                        btnStart.text = "Start"
                        // disabling the cancel button
                        btnCancel.isEnabled = false
                        // resetting the progress bar
                        progressBar.progress = 0
                        // resetting the download percent
                        downloading_percent.text = ""
                        progressBar.isIndeterminate = false
                        Toast.makeText(
                            this@MainActivity,
                            "Downloading Cancelled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                .setOnProgressListener { progress -> // getting the progress of download
                    val progressPer = progress.currentBytes * 100 / progress.totalBytes
                    // setting the progress to progressbar
                    progressBar.progress = progressPer.toInt()
                    // setting the download percent
                    downloading_percent.text =
                        Utils.getProgressDisplayLine(
                            progress.currentBytes,
                            progress.totalBytes
                        )
                    progressBar.isIndeterminate = false
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        // disabling the start button
                        btnStart.isEnabled = false
                        // disabling the cancel button
                        btnCancel.isEnabled = false
                        // setting the text completed to start button
                        btnStart.text = "Completed"
                        // will show the path after the file is downloaded
                        file_downloaded_path.text = "File stored at : $path"
                        Toast.makeText(
                            this@MainActivity,
                            "Downloading Completed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: Error?) {
                        // setting the text start
                        btnStart.setText("Start");
                        // resetting the download percentage
                        downloading_percent.setText("0");
                        // resetting the progressbar
                        progressBar.setProgress(0);
                        // resetting the downloadID
                        downloadID = 0;
                        // enabling the start button
                        btnStart.setEnabled(true);
                        // disabling the cancel button
                        btnCancel.setEnabled(false);
                        progressBar.setIndeterminate(false);
                        Toast.makeText(this@MainActivity, "Error Occurred", Toast.LENGTH_SHORT)
                            .show();

                    }
                })


            // handling click event on cancel button
            btnCancel.setOnClickListener {
                btnStart.text = "Start"
                // cancels the download
                PRDownloader.cancel(downloadID)
            }
        })
    }
}