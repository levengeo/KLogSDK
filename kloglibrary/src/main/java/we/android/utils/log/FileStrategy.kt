package we.android.utils.log

import android.os.Environment
import android.os.Process
import android.util.Log
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class FileStrategy {
    val TAG = "FileStrategy"
    var LOG_FILENAME = "asplog"
    var mLogFile: File? = null
    val LOG_FILEEXT = ".txt"
    val TYPE_LOG = "log"
    val DEFAULT_LOG_DIR = "/dev/null"
    val LOG_FILELIMIT = 1000000L
    val DATEFORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE)
    val DATEFORMAT1 = SimpleDateFormat("yyyyMMddHHmmss", Locale.SIMPLIFIED_CHINESE)
    var gLogQueue: LinkedBlockingQueue<LogItem>? = null
    var gWriteLogThread: Thread? = null
    var mWorkingDirectory: String? = null
    val mDirectories = mutableMapOf<String, File>()
    val DIR_ANDROID = "Android"
    val DIR_DATA = "data"
    var mPid = -1
    val LOG_LAST_NAME_WITH_TIME = LOG_FILENAME + "(" + Process.myPid() + ")" + DATEFORMAT1.format(Date()) + LOG_FILEEXT
    val LOG_LAST_NAME_WITHOUT_TIME = LOG_FILENAME + "(" + Process.myPid() + ")" + LOG_FILEEXT

    class LogItem(var level: String?, var tag: String, var msg: String)

    fun createLogFile(pid: Int) {
        synchronized(LOG_FILENAME) {
            if (mLogFile == null) {
                try {
                    if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                        return
                    }
                    getLogFile()
                    Log.d(TAG, "Process.myPid()2:" + mLogFile!!.getAbsolutePath())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                if (Process.myPid() != mPid) {
                    try {
                        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                            return
                        }
                        getLogFile()
                        Log.d(TAG, "Process.myPid()2:" + mLogFile!!.getAbsolutePath())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (mLogFile?.isFile()!!) {
                    if (mLogFile!!.length() > LOG_FILELIMIT) {
                        var sb: StringBuffer? = StringBuffer(getLogDirectory() + File.separator)
                        sb!!.append(LOG_FILENAME + "(" + Process.myPid() + ")" + DATEFORMAT1.format(Date()) + LOG_FILEEXT)
                        mLogFile!!.renameTo(File(sb.toString()))
                        sb = StringBuffer(getLogDirectory() + File.separator)
                        sb!!.append(LOG_FILENAME + "(" + Process.myPid() + ")" + LOG_FILEEXT)
                        mLogFile = File(sb.toString())
                        mPid = Process.myPid()
                        if (!mLogFile!!.exists()) {
                            Log.d(TAG, "2 Create the file:$sb")
                            try {
                                mLogFile!!.createNewFile()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            if (gLogQueue == null && mLogFile != null) {
                gLogQueue = LinkedBlockingQueue()
                gWriteLogThread = WriteLogThread()
                (gWriteLogThread as WriteLogThread).start()
            }
        }
    }

    fun getLogFile() {
        mPid = Process.myPid()
        mLogFile = File(getLogDirectory() + File.separator
                + LOG_FILENAME + "(" + mPid + ")" + LOG_FILEEXT)
        if (!mLogFile?.exists()!!) {
            Log.d(TAG, "1 Create the file:$LOG_FILENAME$mPid")
            mLogFile!!.createNewFile()
        }
    }

    inner class WriteLogThread : Thread() {
        override fun run() {
            if (mLogFile == null || gLogQueue == null) {
                return
            }
            var raf: RandomAccessFile? = null
            try {
                raf = RandomAccessFile(mLogFile, "rw")
                raf!!.seek(mLogFile!!.length())
                val sb = StringBuffer()
                var item: LogItem?
                while (!isInterrupted) {
                    if (gLogQueue!!.size == 0)
                        continue
                    item = gLogQueue!!.take()
                    sb.setLength(0)
                    sb.append(DATEFORMAT.format(Date()))
                            .append(": ").append(item!!.level)
                            .append(": ").append(item.tag)
                            .append(": ").append(item.msg)
                            .append("\n")
                    raf!!.write(sb.toString().toByteArray(charset("UTF-8")))
                    if (raf.length() > LOG_FILELIMIT || Process.myPid() != mPid) {
                        val oldFile = mLogFile
                        createLogFile(Process.myPid())
                        if (oldFile !== mLogFile) {
                            raf.close()
                            raf = RandomAccessFile(mLogFile, "rw")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (raf != null) {
                    try {
                        raf.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun getLogDirectory(): String? {
        val extStorageDirectory = Environment.getExternalStorageDirectory()
        var externalStorageState = Environment.getExternalStorageState()
        return if (extStorageDirectory != null && extStorageDirectory.exists()
                && Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
            mWorkingDirectory = (extStorageDirectory.getAbsolutePath()
                    + File.separator + DIR_ANDROID + File.separator
                    + DIR_DATA)
            getWorkingDirectory(TYPE_LOG)
        } else {
            DEFAULT_LOG_DIR
        }
    }

    fun getWorkingDirectory(type: String): String? {
        var dir: String? = null
        val file = buildWorkingDirectory(type)
        if (file != null) {
            dir = file.getAbsolutePath()
        }
        return dir
    }

    private fun buildWorkingDirectory(type: String): File? {
        var file: File? = null
        if (!type.isNullOrBlank()) {
            val dir = mDirectories[type]
            if (dir != null) {
                file = dir
            } else {
                var dir: String? = null
                dir = mWorkingDirectory + File.separator + type
                ensureDirExists(dir)
                file = File(dir)
                if (file.exists()) {
                    mDirectories[type] = file
                }
            }
        }
        return file
    }

    fun ensureDirExists(dir: String) {
        if (dir.isNullOrBlank()) {
            return
        }
        var file: File? = File(dir)
        if (!file!!.exists()) {
            if (file.isFile) {
                file = file.parentFile
            }
            if (file != null) {
                file.mkdirs()
            }
        }
    }

    fun writeToFile(priority: Int, tag: String, msg: String) {
        if (gLogQueue == null) {
            return
        }
        var MutableMap = mapOf(
            Utils.VERBOSE to "V",
                Utils.DEBUG to "D",
                Utils.INFO to "I",
                Utils.WARN to "W",
                Utils.ERROR to "E")
        try {
            gLogQueue!!.put(LogItem(MutableMap[priority], tag, msg))
        } catch (e: InterruptedException) {
        }
    }

}