package com.boardsportscalifornia.app.data

import android.content.Context
import java.io.File

class FileStorage(context: Context) {

    private val filesDir = context.filesDir

    fun write(filename: String, bytes: ByteArray): File =
        File(filesDir, filename).apply {
            writeBytes(bytes)
        }

    fun read(filename: String): ByteArray = File(filesDir, filename).readBytes()
}
