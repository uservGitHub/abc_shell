package ring

import android.os.Bundle
import sample.EmptyActivity

/**
 * Created by Administrator on 2018/4/22.
 */

open class TestRingActivity:EmptyActivity() {
    private val hostName = "TestRingActivity"

    private inline fun manual(msg: Any) = log.manual(hostName, msg)
    private inline fun pilling(msg: Any) = log.pilling(hostName, msg)

    override fun onCreate(savedInstanceState: Bundle?) {
        manual("preOnCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        manual("preOnResume")
        super.onResume()
    }
}