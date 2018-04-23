package sample.gxd.business

import gxd.business.RxBaseActivity
import java.util.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * Created by work on 2018/4/23.
 */

open class RxActivity:RxBaseActivity() {
    private inline fun trySleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun businessSetup() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private inline fun buildSource() =
            Observable.range(0, ringSourceCount.value!!)

    private inline fun<T> Observable<T>.map1() = map {
        log.pillingThread("T1")
        trySleep(ringT1.value!!)
        "T1:$it"
    }.doOnDispose(log::postBreak)


    private inline fun doDispose(){
        thread(true,true){
            trySleep(ringTBreak.value!!)
            log.disposer?.dispose()
        }
    }

    private inline fun ThreadType.toScheduler(): Scheduler {
        return when(this){
            ThreadType.cpu -> Schedulers.computation()

            else -> Schedulers.single()
        }
    }

    private inline fun build(){
        if (ringMap2.value!!.equals(MapType.none)){
            val title = ""
            val step1 = buildSource().map1().subscribeOn(ringThreadType1.value!!.toScheduler())

            log.reset(title)
            step1.subscribe(log::preNext,log::preError,log::postComplete)
            doDispose()
        }
    }
}