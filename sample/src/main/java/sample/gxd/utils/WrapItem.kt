package gxd.utils

/**
 * Created by work on 2018/5/2.
 * 包装项
 */

data class WrapItem<T>(val t:T,
                       var selected:Boolean = false)