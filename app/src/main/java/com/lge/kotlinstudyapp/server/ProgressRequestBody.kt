package com.lge.kotlinstudyapp.server

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class ProgressRequestBody(private val contentType: MediaType, private val file: File, private val listener: ((upsize: Long, totalsize: Long) -> Unit)?) : RequestBody() {
    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }

    override fun contentType(): MediaType = contentType
    override fun contentLength(): Long = file.length()
    override fun writeTo(sink: BufferedSink) {
        val fileLength = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val ins = FileInputStream(file)
        var uploaded: Long = 0
        ins.use { ins ->
            var read: Int
            while (ins.read(buffer).also { read = it } != -1) {
                // update progress on UI thread
                val uploadSize = uploaded
                listener?.invoke(uploadSize, fileLength)
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        }
    }
}