package we.android.utils.log

class KConfig constructor(builder: Builder) {
    private var mShowThread = false
    private var mShowLocation = false
    private var mSaveToFile = false
    private var mTag = "AspLog"

    fun getShowThread() : Boolean {
        return mShowThread
    }

    fun setShowThead(boolean: Boolean) {
        mShowThread = boolean
    }

    fun getShowLocation() : Boolean {
        return mShowLocation
    }

    fun setShowLocation(boolean: Boolean) {
        mShowLocation = boolean
    }

    fun getSaveToFile() : Boolean {
        return mSaveToFile
    }

    fun setSaveToFile(boolean: Boolean) {
        mSaveToFile = boolean
    }

    fun getTag() : String {
        return mTag
    }

    fun setTag(tag: String) {
        mTag = tag
    }

    companion object Builder {
        private var mShowThread = false
        private var mShowLocation = false
        private var mSaveToFile = false
        private var mTag = Utils.LOG_TAG

        fun Builder() {
        }

        fun getShowThread(): Boolean {
            return mShowThread
        }

        fun setShowThead(boolean: Boolean): Builder {
            mShowThread = boolean
            return this
        }

        fun getShowLocation() : Boolean {
            return mShowLocation
        }

        fun setShowLocation(boolean: Boolean): Builder {
            mShowLocation = boolean
            return this
        }

        fun getSaveToFile(): Boolean {
            return mSaveToFile
        }

        fun setSaveToFile(boolean: Boolean) : Builder {
            mSaveToFile = boolean
            return this
        }

        fun getTag(): String {
            return mTag
        }

        fun setTag(tag: String) : Builder {
            mTag = tag
            return this
        }

        fun build(): KConfig {
            return KConfig(this)
        }
    }

    init {
        mShowThread = Builder.getShowThread()
        mSaveToFile = Builder.getSaveToFile()
        mShowLocation = Builder.getShowLocation()
        mTag = Builder.getTag()
    }

}