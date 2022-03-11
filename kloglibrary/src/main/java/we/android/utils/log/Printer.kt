package we.android.utils.log

interface Printer {
    fun v(msg: String)
    fun d(msg: String)
    fun i(msg: String)
    fun w(msg: String)
    fun e(msg: String)
    fun json(msg: String)
    fun xml(msg: String)
}