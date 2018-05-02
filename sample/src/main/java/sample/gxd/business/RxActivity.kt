package sample.gxd.business

import android.util.Log
import gxd.business.RxBaseActivity
import java.util.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ExecutorService
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
    //更新开关，从UI设置中更新
    private val updateSwitch:()->Unit = {
        log.switch(isFlow, isLogv, isPill)
    }
    //自定义调度器
    private var customScheduler1: ExecutorService? = null
    private var customScheduler2:ExecutorService? = null
    //清空自定义调度器的钩子
    private var clearSchedulerHook1:(()->Unit)? = null
    private var clearSchedulerHook2:(()->Unit)? = null

    override fun mapToMap() {
        customScheduler1 = null
        customScheduler2 = null
        clearSchedulerHook1 = null
        clearSchedulerHook2 = null

        //事件产生、事件加工、事件消费
        val title = "${ringThreadType1.value}_${ringMap1.value}_${ringThreadType2.value}_${ringMap2.value}_${ringTBreak.value}"

        val source = createSource()
                .apply {
                    subscribeOn(ringThreadType1.value!!.toScheduler1())
                }
                //.subscribeOn(ringThreadType1.value!!.toScheduler1())
                .map {
                    log.pillingThread("T1")
                    trySleep(ringT1.value!!)
                    "T1:$it"
                }
                .observeOn(ringThreadType2.value!!.toScheduler2())
                .map {
                    log.pillingThread("T2")
                    trySleep(ringT2.value!!)
                    "T2:$it"
                }
        updateSwitch.invoke()
        log.reset(title){
            clearSchedulerHook1?.invoke()
            clearSchedulerHook2?.invoke()
        }
        source.subscribe(log::preNext,log::preError,log::postComplete,log::preSubscribe)
        //region    doBrak(中断时间大于0才有效)
        if (ringTBreak.value!! > 0){
            doBreak()
        }
        //endregion
    }

    override fun flatmapToFlatmap() {
        customScheduler1 = null
        customScheduler2 = null
        clearSchedulerHook1 = null
        clearSchedulerHook2 = null

        //事件产生、事件加工、事件消费
        val title = "${ringThreadType1.value}_${ringMap1.value}_${ringThreadType2.value}_${ringMap2.value}_${ringTBreak.value}"

        /*val source = createSource()
                .flatMap1()
                .subscribeOn(ringThreadType1.value!!.toScheduler1())
                .observeOn(ringThreadType2.value!!.toScheduler2())
                .flatMap2()
                .doOnDispose(log::postBreak)*/
        val source = createSource()
                .flatMap { v->
                    Observable.just(v).map {
                        log.pillingThread("T1")
                        trySleep(ringT1.value!!)
                        "T1:$v"
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { v->
                    Observable.just(v).map {
                        log.pillingThread("T2")
                        trySleep(ringT2.value!!)
                        "T2:$v"
                    }
                }
                .doOnDispose(log::postBreak)

        updateSwitch.invoke()
        log.reset(title){
            clearSchedulerHook1?.invoke()
            clearSchedulerHook2?.invoke()
        }
        source.subscribe(log::preNext,log::preError,log::postComplete,log::preSubscribe)
        //region    doBrak(中断时间大于0才有效)
        if (ringTBreak.value!! > 0){
            doBreak()
        }
        //endregion
    }

    override fun businessSetup() {
        //clearDump()   可以对比
        excuteBusiness()
    }

    private inline fun excuteBusiness(){
        customScheduler1 = null
        customScheduler2 = null
        clearSchedulerHook1 = null
        clearSchedulerHook2 = null

        //事件产生、事件加工、事件消费
        /**
         * .subscribeOn 不能放apply中
         * .observeOn 不能放apply中
         * .doOnDispose 被动操作，等待被触发
         */
        val source = when{
            ringMap1.value!! == MapType.map && ringMap2.value!! == MapType.map ->{
                createSource()
                        .subscribeOn(ringThreadType1.value!!.toScheduler1())
                        .doOnDispose(log::postBreak)
                        .map1()
                        .observeOn(ringThreadType2.value!!.toScheduler2())
                        .map2()
            }
            ringMap1.value!! == MapType.flatMap && ringMap2.value!! == MapType.flatMap ->{
                createSource()
                        .subscribeOn(ringThreadType1.value!!.toScheduler1())
                        .doOnDispose(log::postBreak)
                        .flatMap1()
                        .observeOn(ringThreadType2.value!!.toScheduler2())
                        .flatMap2()
            }
            ringMap1.value!! == MapType.map && ringMap2.value!! == MapType.flatMap ->{
                createSource()
                        .subscribeOn(ringThreadType1.value!!.toScheduler1())
                        .doOnDispose(log::postBreak)
                        .map1()
                        .observeOn(ringThreadType2.value!!.toScheduler2())
                        .flatMap2()
            }
            ringMap1.value!! == MapType.flatMap && ringMap2.value!! == MapType.map ->{
                createSource()
                        .subscribeOn(ringThreadType1.value!!.toScheduler1())
                        .doOnDispose(log::postBreak)
                        .flatMap1()
                        .observeOn(ringThreadType2.value!!.toScheduler2())
                        .map2()
            }
            ringMap1.value!! == MapType.none && ringMap2.value!! == MapType.map ->{
                createSource()
                        .doOnDispose(log::postBreak)
                        .observeOn(ringThreadType2.value!!.toScheduler2())
                        .map2()
            }
            ringMap1.value!! == MapType.none && ringMap2.value!! == MapType.flatMap ->{
                createSource()
                        .doOnDispose(log::postBreak)
                        .observeOn(ringThreadType2.value!!.toScheduler2())
                        .flatMap2()
            }
            ringMap2.value!! == MapType.none && ringMap1.value!! == MapType.map ->{
                createSource()
                        .subscribeOn(ringThreadType1.value!!.toScheduler1())
                        .doOnDispose(log::postBreak)
                        .map1()
            }
            ringMap2.value!! == MapType.none && ringMap1.value!! == MapType.flatMap ->{
                createSource()
                        .flatMap1()
                        .subscribeOn(ringThreadType1.value!!.toScheduler1())
                        .doOnDispose(log::postBreak)

            }
            else ->{
                createSource().doOnDispose(log::postBreak)
            }
        }

        val title = "${ringThreadType1.value}_${ringMap1.value}_${ringThreadType2.value}_${ringMap2.value}_${ringTBreak.value}"

        updateSwitch.invoke()
        log.reset(title) {
            clearSchedulerHook1?.invoke()
            clearSchedulerHook2?.invoke()
        }
        source.subscribe(log::preNext, log::preError, log::postComplete, log::preSubscribe)
        //region    doBrak(中断时间大于0才有效)
        if (ringTBreak.value!! > 0) {
            doBreak()
        }
        //endregion
    }

    private inline fun createSource() =
            Observable.range(0, ringSourceCount.value!!)

    //region    map and flatMap
    private inline fun<T> Observable<T>.map1() = map {
        log.pillingThread("T1")
        trySleep(ringT1.value!!)
        "T1:$it"
    }//.doOnDispose(log::postBreak)
    private inline fun<T> Observable<T>.map2() = map {
        log.pillingThread("T2")
        trySleep(ringT2.value!!)
        "T2:$it"
    }
    private inline fun<T> Observable<T>.flatMap1() = flatMap {
        Observable.just(it).map {
            log.pillingThread("T1")
            trySleep(ringT1.value!!)
            "T1:$it"
        }
    }
    private inline fun<T> Observable<T>.flatMap2() = flatMap {
        Observable.just(it).map {
            log.pillingThread("T2")
            trySleep(ringT2.value!!)
            "T2:$it"
        }
    }
    //endregion

    //region    toScheduler
    private inline fun ThreadType.toScheduler1(): Scheduler {
        return when (this) {
            ThreadType.cpu -> Schedulers.computation()  //处理器数量
            ThreadType.io -> Schedulers.io()    //无限个，默认使用缓存调度器
            ThreadType.single -> Schedulers.single()    //一个线程
            ThreadType.main -> AndroidSchedulers.mainThread()   //main
            ThreadType.cache -> {
                Schedulers.from(Executors.newCachedThreadPool().apply {
                    customScheduler1 = this
                    clearSchedulerHook1 = {
                        customScheduler1?.shutdownNow()
                    }
                })
            }
            ThreadType.fixed -> {
                Schedulers.from(Executors.newFixedThreadPool(ringThreadCount1.value!!).apply {
                    customScheduler1 = this
                    clearSchedulerHook1 = {
                        customScheduler1?.shutdownNow()
                    }
                })
            }
            ThreadType.schedule -> {
                Schedulers.from(Executors.newScheduledThreadPool(ringThreadCount1.value!!).apply {
                    customScheduler1 = this
                    clearSchedulerHook1 = {
                        customScheduler1?.shutdownNow()
                    }
                })
            }

            else -> throw IllegalArgumentException("无效的调度器类型：$this")
        }
    }
    private inline fun ThreadType.toScheduler2(): Scheduler {
        return when (this) {
            ThreadType.cpu -> Schedulers.computation()  //处理器数量
            ThreadType.io -> Schedulers.io()    //无限个，默认使用缓存调度器
            ThreadType.single -> Schedulers.single()    //一个线程
            ThreadType.main -> AndroidSchedulers.mainThread()   //main
            ThreadType.cache -> {
                Schedulers.from(Executors.newCachedThreadPool().apply {
                    customScheduler2 = this
                    clearSchedulerHook2 = {
                        customScheduler2?.shutdownNow()
                    }
                })
            }
            ThreadType.fixed -> {
                Schedulers.from(Executors.newFixedThreadPool(ringThreadCount2.value!!).apply {
                    customScheduler2 = this
                    clearSchedulerHook2 = {
                        customScheduler2?.shutdownNow()
                    }
                })
            }
            ThreadType.schedule -> {
                Schedulers.from(Executors.newScheduledThreadPool(ringThreadCount2.value!!).apply {
                    customScheduler2 = this
                    clearSchedulerHook2 = {
                        customScheduler2?.shutdownNow()
                    }
                })
            }

            else -> throw IllegalArgumentException("无效的调度器类型：$this")
        }
    }
    //endregion

    private inline fun doBreak(){
        thread(true,true){
            trySleep(ringTBreak.value!!)
            log.disposer?.dispose()
        }
    }

}