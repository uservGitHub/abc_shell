package sample.gxd.utils

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription
import java.util.*

/**
 * Created by Administrator on 2018/5/5.
 */

/**
 * RxJava日志（输出行信息的回调）
 * 多次调用不需要重新设置
 * 每次start(是一个过程，结束时执行异步回调)后清理打印信息
 */
class RxLog private constructor() {
    companion object {
        val INSTANCE: RxLog by lazy {
            RxLog()
        }
    }
    /**
     * 调用方式：
     * 1. 重置（开关及回调操作）
     * 2. 加入桩信息
     * 3. 执行（启动Rx，可自定义订阅）
     */
    //region    具体接口
    /**
     * 重置，某些情况下无法重置
     * 1. 未执行完毕
     * 2. 无输出回调
     */
    fun reset(shareDumpLines: ((String) -> Unit)? = null):Boolean {
        //false表示正在执行中
        if (isEnd != false && (dumpLines != null || shareDumpLines != null)) {
            isEnd = null
            //不为空时覆盖
            shareDumpLines?.let { dumpLines = it }
            return true
        }
        return false
    }
    /**
     * 执行，执行以下步骤
     * 1. 确定执行起点（清理）
     * 2. 过程（第一次，异常，结束）
     * 3. 结束回调（清理）
     */
    fun excute(source:Observable<out Any>) {
        //设置启动标志
        if (isEnd != null) {
            lineError("无法开始（未重置或无输出）！")
            dumpLines?.invoke(sb.toString())
            return
        }

        //初始化
        initialize()

        //启动
        start()

        innerExcute(source)
    }

    fun p1() = pN(1)
    fun p2() = pN(2)
    fun p3() = pN(3)
    fun p4() = pN(4)
    fun p5() = pN(5)
    fun p6() = pN(6)
    fun p7() = pN(7)
    fun p8() = pN(8)
    fun pill(tag:Any){
        sb.append("${startDT()} P::$tag ${threadName()}\n")
    }
    private inline fun pN(id:Int){
        if (flowPArray[id] != null) return
        flowPArray[id] = "${startDT()} P$id:${threadName()}\n"
        sb.append(flowPArray[id])
    }

    @Volatile
    var dumpLines: ((String) -> Unit)?=null

    private inline fun innerExcute(source:Observable<out Any>){
        source
                .doOnDispose { postBreak() }
                .subscribe(::preNext,::preError,::postComplete,::preSubscribe)
    }

    @Volatile
    private var isEnd:Boolean? = true
    @Volatile
    var isNext:Boolean = true
    private inline fun lineError(error:String){
        sb.append("StartError:$error\n")
        /*sb.append("StartError:")
        sb.append(error)
        sb.append('\n')*/
    }

    private inline fun initialize(){
        sb.delete(0, sb.length)
        isEnd = false

        for (i in 0 until flowPArray.size){
            flowPArray[i] = null
        }
        nextCount = 0
        tick = 0
        startTick = 0
    }
    //8个特定桩
    @Volatile
    private var flowPArray = arrayOf<String?>(null,null,null,null,null,null,null,null)

    private inline fun end(){
        if (isEnd == false) {
            isEnd = true
            dumpLines!!.invoke(sb.toString())
            //不清理sb，方便后续使用
            subscriptor = null
            disposer = null
        }
    }
    private inline fun start(){
        sb.append("启动时间：${Date()}\n")
        tick = System.currentTimeMillis()
    }
    @Volatile
    private var sb = StringBuilder(10*1024)

    var disposer: Disposable? = null
        private set
    var subscriptor: Subscription? = null
        private set
    @Volatile
    private var tick = 0L
    @Volatile
    private var startTick = 0L  //第一次动作时的时刻
    @Volatile
    private var nextCount = 0

    private inline fun startDT() = (System.currentTimeMillis()-startTick).no4()
    private inline fun threadName():String {
        val name = Thread.currentThread().name
        if (name.startsWith("Rx")) {
            return name.substring(2, 4) + name.substring(name.length - 2)
        }
        return name
    }

    //region    右对齐整数或序号的格式化
    private inline fun Int.no2() = when {
        this in 0..9 -> " $this"
        else -> "$this"
    }

    private inline fun Long.no4() = when (this) {
        in 0..9 -> "   $this"
        in 10..99 -> "  $this"
        in 100..999 -> " $this"
        else -> "$this"
    }
    //endregion

    //region    标准打印信息
    private inline fun postBreak(){
        val flow = "${startDT()} Bk($nextCount)\n"
        sb.append(flow)
        end()
    }

    private inline fun preNext(t:Any){
        if (isNext) {
            val flow = "${startDT()} Nt[${nextCount.no2()}]\n"
            sb.append(flow)
        }
        nextCount++
    }
    private inline fun preError(t:Throwable){
        t.printStackTrace()
        val flow = "${startDT()} Er($nextCount)\n"
        sb.append(flow)
        end()
    }
    private inline fun postComplete(){
        val  flow = "${startDT()} Ce($nextCount)\n"
        sb.append(flow)
        end()
    }
    //endregion

    /**
     * 前置Subscribe，引用Dispable
     */
    private inline fun preSubscribe(t: Disposable) {
        disposer = t
        startTick = System.currentTimeMillis()
    }

    /**
     * 前置Subscribe，引用Subscription
     */
    private inline fun preSubscribe(t: Subscription) {
        subscriptor = t
        startTick = System.currentTimeMillis()
    }
}