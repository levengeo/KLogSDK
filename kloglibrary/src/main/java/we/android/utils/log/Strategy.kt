package we.android.utils.log

import org.jetbrains.annotations.Nullable

interface Strategy {

    fun log(priority: Int, @Nullable msg: String)
}