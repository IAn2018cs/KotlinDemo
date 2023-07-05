import kotlinx.coroutines.Job


/**
 * 打印Job的状态信息
 */
fun Job.log() {
    logX(
        """
        isActive = $isActive
        isCancelled = $isCancelled
        isCompleted = $isCompleted
    """.trimIndent()
    )
}

/**
 * 控制台输出带协程信息的log
 */
fun logX(any: Any?) {
    println(
        """
================================
$any
Thread:${Thread.currentThread().name}
================================""".trimIndent()
    )
}
