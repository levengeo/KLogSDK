package we.android.utils.log

import android.content.Context
import java.lang.ref.WeakReference

object KLog {
    private lateinit var mContext: WeakReference<Context>
    @JvmStatic private lateinit var mPrinterImpl: PrinterImpl

    enum class Type {
        D, E, I, V, W, JSON, XML
    }

    @JvmStatic
    fun init(context: Context, kConfig: KConfig) {
        mContext = WeakReference(context)
        mPrinterImpl = PrinterImpl(kConfig)
    }

    @JvmStatic
    fun v(msg: String) {
        print(Type.V, msg)
    }

    @JvmStatic
    fun d(msg: String) {
        print(Type.D, msg)
    }

    @JvmStatic
    fun d(object1: Object) {
        mPrinterImpl.d(object1)
    }

    @JvmStatic
    fun i(msg: String) {
        print(Type.I, msg)
    }

    @JvmStatic
    fun w(msg: String) {
        print(Type.W, msg)
    }

    @JvmStatic
    fun e(msg: String) {
        print(Type.E, msg)
    }

    @JvmStatic
    fun json(msg: String) {
        print(Type.JSON, msg)
    }

    @JvmStatic
    fun xml(msg: String) {
        print(Type.XML, msg)
    }

    @JvmStatic
    private fun print(type: Type, msg: String) {
        when (type) {
            Type.D -> mPrinterImpl.d(msg)
            Type.E -> mPrinterImpl.e(msg)
            Type.I -> mPrinterImpl.i(msg)
            Type.V -> mPrinterImpl.v(msg)
            Type.W -> mPrinterImpl.w(msg)
            Type.JSON -> mPrinterImpl.json(msg)
            Type.XML -> mPrinterImpl.xml(msg)
        }
    }
}