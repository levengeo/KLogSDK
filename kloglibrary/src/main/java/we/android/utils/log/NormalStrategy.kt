package we.android.utils.log

import android.os.Process
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class NormalStrategy: Strategy {
    private var mTAG: String
    private val JSON_INDENT = 2
    private var mShowTread = false
    private var mShowLocation = false
    private var mSaveToFile = false
    private val TOP_LEFT_CORNER = "┌"
    private val BOTTOM_LEFT_CORNER = "└"
    private val MIDDLE_CORNER = "├"
    private val HORIZONTAL_LINE = "│"
    private val DOUBLE_DIVIDER = "═════════════════════════════════════════════════"
    private val SINGLE_DIVIDER = "─────────────────────────────────────────────────"
    private val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER
    private val BR = System.getProperty("line.separator")
    lateinit var mFileStrategy: FileStrategy

    constructor(tag: String, showTread: Boolean, showLocation: Boolean, saveToFile: Boolean) {
        mTAG = if (tag.isNullOrBlank()) {
            Utils.LOG_TAG
        } else {
            tag
        }
        mShowTread = showTread
        mShowLocation = showLocation
        mSaveToFile = saveToFile

        if (mSaveToFile) {
            mFileStrategy = FileStrategy()
            mFileStrategy.createLogFile(Process.myPid())
        }
    }

    fun log(object1: Any) {
        log(Utils.DEBUG, toString(object1))
    }

    fun toString(any: Any?): String {
        if (any == null) {
            return "null"
        } else if (!any.javaClass.isArray) {
            return any.toString()
        }
        when (any) {
            is BooleanArray ->return Arrays.toString(any)
            is ByteArray ->return Arrays.toString(any)
            is CharArray ->return Arrays.toString(any)
            is ShortArray ->return Arrays.toString(any)
            is IntArray ->return Arrays.toString(any)
            is LongArray ->return Arrays.toString(any)
            is FloatArray ->return Arrays.toString(any)
            is DoubleArray ->return Arrays.toString(any)
            else ->return Arrays.deepToString(any as Array<out Any>?)
        }

        return  "Couldn't find a correct type for the object"
    }
    override fun log(priority: Int, msg: String) {
        logTop(priority)
        logHeader(priority)

        logContent(priority, msg)
        logBottom(priority)
    }

    fun logTop(priority: Int) {
        logChunk(priority, TOP_BORDER)
    }

    private fun logDivider(logType: Int) {
        logChunk(logType, MIDDLE_BORDER)
    }

    fun logHeader(priority: Int) {
        var traces = Thread.currentThread().stackTrace
        if (mShowTread) {
            logChunk(priority, HORIZONTAL_LINE + " Thread: " + Thread.currentThread().name)
            logDivider(priority)
        }
        if (mShowLocation) {
            var offest = getStackOffset(traces) + 1
            val builder = StringBuilder()
            builder.append(HORIZONTAL_LINE)
                    .append(' ')
                    .append(traces[offest].getClassName())
                    .append(".")
                    .append(traces[offest].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(traces[offest].getFileName())
                    .append(":")
                    .append(traces[offest].getLineNumber())
                    .append(")")
            logChunk(priority, builder.toString())
        }
    }

    private fun logBottom(logType: Int) {
        logChunk(logType, BOTTOM_BORDER)
    }

    fun logChunk(priority: Int, message: String) {
        Log.println(priority, mTAG, message)

        if (mSaveToFile) {
            mFileStrategy.writeToFile(priority, mTAG, message)
        }
    }

    private fun logContent(logType: Int, chunk: String) {
        checkNotNull(chunk)

        val lines = chunk.split(BR!!)
        for (line in lines) {
            logChunk(logType, "$HORIZONTAL_LINE $line")
        }
    }

    fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var i = 4
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (name != NormalStrategy::class.java.name && name != PrinterImpl::class.java.name && name != KLog::class.java.name) {
                return --i
            }
            i++
        }
        return -1
    }

    fun json(msg: String) {
        if (msg.isNullOrBlank()) {
            log(Utils.DEBUG, "Empty/Null json content")
            return
        }
        try {
            val json = msg.trim()
            when (json.first().toString()) {
                "{" -> {
                    val jsonObject = JSONObject(json)
                    val message = jsonObject.toString(JSON_INDENT)
                    log(Utils.DEBUG, message)
                    return
                }
                "[" -> {
                    val jsonArray = JSONObject(json)
                    val message = jsonArray.toString(JSON_INDENT)
                    log(Utils.DEBUG, message)
                    return
                }
            }
            log(Utils.ERROR, "Invalid Json")
        } catch (e: JSONException) {
            log(Utils.ERROR, "Invalid Json")
        }
    }

    fun xml(xml: String?) {
        if (xml.isNullOrBlank()) {
            log(Utils.DEBUG, "Empty/Null xml content")
            return
        }
        try {
            val xmlInput = StreamSource(StringReader(xml!!))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            log(Utils.DEBUG, xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n"))
        } catch (e: TransformerException) {
            log(Utils.ERROR, "Invalid xml")
        }
    }

}