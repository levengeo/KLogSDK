package we.android.utils.log

class PrinterImpl : Printer {
    private var mKConfig: KConfig
    private lateinit var normalStrategy: NormalStrategy
    
    constructor(kConfig : KConfig) {
        mKConfig = kConfig
        init()
    }

    fun init() {
        normalStrategy = NormalStrategy(mKConfig.getTag(),
                mKConfig.getShowThread(), mKConfig.getShowLocation(),
                mKConfig.getSaveToFile())
    }

    override fun v(msg: String) {
        normalStrategy.log(Utils.VERBOSE, msg)
    }

    override fun d(msg: String) {
        normalStrategy.log(Utils.DEBUG, msg)
    }

    fun d(any: Any) {
        normalStrategy.log(any)
    }

    override fun i(msg: String) {
        normalStrategy.log(Utils.INFO, msg)
    }

    override fun w(msg: String) {
        normalStrategy.log(Utils.WARN, msg)
    }

    override fun e(msg: String) {
        normalStrategy.log(Utils.ERROR, msg)
    }

    override fun json(msg: String) {
        normalStrategy.json(msg)
    }

    override fun xml(msg: String) {
        normalStrategy.xml(msg)
    }
}