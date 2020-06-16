package com.cylin.clutils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri

class FileUtils {

    companion object {

        fun openPdfWith(activity: Activity, data: Uri) {
            val target = Intent(Intent.ACTION_VIEW)
            target.setDataAndType(data, "application/pdf")
            target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

            val intent = Intent.createChooser(target, "Open File")
            try {
                activity.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Instruct the user to install a PDF reader here, or something
            }
        }
    }
}