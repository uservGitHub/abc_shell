package gxd.test

import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import sample.EmptyActivity
import sample.gxd.utils.RxLog
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator on 2018/5/5.
 */

open class TestRxLogActivity: EmptyActivity() {
    /*private val hostName = "TestRingActivity"
    private inline fun manual(msg: Any) = log.manual(hostName, msg)
    private inline fun pilling(msg: Any) = log.pilling(hostName, msg)*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(rxLog.reset()){
            tSwitchMap()
        }

    }

    override fun onResume() {
        super.onResume()
    }
    private val rxLog:RxLog
    init {
        rxLog = RxLog.INSTANCE.apply {
            dumpLines = { Log.v("_TRL", it) }
            isNext = false
        }
    }

    private fun test1(){
        if(rxLog.reset()){
            val items = listOf<Int>(11,13,15)
            val source = Observable
                    .interval(300, TimeUnit.MILLISECONDS)
                    .doOnNext { rxLog.p1() }
                    .map { items[it.toInt()] }
                    .doOnNext { rxLog.p2() }
                    .take(items.size.toLong())
            rxLog.excute(source)
        }
    }
    private fun tSwitchMap(){
        val data = listOf<List<Int>>(listOf(1,2,3,4,5), listOf(6,7,8,9,10))
        val source = Observable
                .fromIterable(data)
                .doOnNext { sleep();rxLog.pill(it) }
                .flatMap {
                    /*Observable.fromIterable(it)
                            .map { sleep(); it }    //全部用一个线程
                            .subscribeOn(Schedulers.io())*/
                    Observable.just(it)
                            .map { sleep(); it }    //各一个线程
                            .subscribeOn(Schedulers.io())
                }
                .doOnNext { rxLog.pill(it) }
                //.subscribeOn(Schedulers.io())

        rxLog.excute(source)
    }
    private fun tConcatMap(){
        val data = listOf<List<Int>>(listOf(1,2,3), listOf(7,8,9))
        val source = Observable
                .fromIterable(data)
                .doOnNext { sleep();rxLog.pill(it) }
                .concatMap { Observable.fromIterable(it) }
                .doOnNext { rxLog.pill(it) }
                .subscribeOn(Schedulers.io())

        rxLog.excute(source)
    }
    private fun tFlatMapIterable(){
        val data = listOf<List<Int>>(listOf(1,2,3), listOf(7,8,9))
        val source = Observable
                .fromIterable(data)
                .doOnNext { sleep();rxLog.pill(it) }
                .flatMapIterable { it }
                .doOnNext { rxLog.pill(it) }
                .subscribeOn(Schedulers.computation())

        rxLog.excute(source)
    }

    private inline fun sleep(millis:Int = 100){
        try {
            Thread.sleep(millis.toLong())
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}