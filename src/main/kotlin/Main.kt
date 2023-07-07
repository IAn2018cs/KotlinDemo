import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis


// -Dkotlinx.coroutines.debug
fun main() = runBlocking {
    test7()
}

fun test1() = runBlocking {
    val job = launch {
        delay(1000L)
    }
    job.log()             // ①
    delay(1500L) // ②
    job.log()             // ③
}


fun test2() = runBlocking {
    //                           变化在这里
    //                               ↓
    val job = launch(start = CoroutineStart.LAZY) {
        logX("Coroutine start!")
        delay(1000L)
    }
    delay(1000L) // ①
    job.log()
    job.start()           // ②
    job.log()
    delay(1500L) // ③
    job.log()
}


fun test3() = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        logX("Coroutine start!")
        delay(1000L)
    }
    delay(500L)
    job.log()
    job.start()
    job.log()
    delay(1100L) // ①
    job.log()
    delay(2000L) // ②
    logX("Process end!")
}

fun test4() = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        logX("Coroutine start!")
        delay(1000L)
        logX("Coroutine end!")
    }
    delay(500L)
    job.log()
    job.start()
    job.log()
    delay(500L)  // ①
    job.cancel()          // ②
    job.log()             // ③
    delay(2000L) // ④
    job.log()             // ⑤
}


fun test5() = runBlocking {
    suspend fun download() {
        // 模拟下载任务
        val time = (Random.nextDouble() * 1000).toLong()
        logX("Delay time: = $time")
        delay(time)
    }

    val job = launch {
        logX("Coroutine start!")
        download()
        logX("Coroutine end!")
    }
    job.log()
    job.join()      // 等待协程执行完毕
    job.log()
}

fun test6() = runBlocking {
    suspend fun download() {
        // 模拟下载任务
        val time = (Random.nextDouble() * 1000).toLong()
        logX("Delay time: = $time")
        delay(time)
    }

    val job = launch {
        logX("Coroutine start!")
        download()
        logX("Coroutine end!")
    }
    job.log()
    job.invokeOnCompletion {
        job.log()   // 协程结束以后就会调用这里的代码
    }
    job.join()
}


fun test7() = runBlocking {
    suspend fun download(): String {
        // 模拟下载任务
        val time = (Random.nextDouble() * 1000).toLong()
        logX("Delay time: = $time")
        delay(time)
        return "download result!"
    }

    val deferred = async {
        logX("Coroutine start!")
        val result = download()
        logX("Coroutine end!")
        return@async result
    }
    val result = deferred.await()
    println("Result = $result")
    logX("Process end!")
}


fun test8() = runBlocking {
    val parentJob: Job
    var job1: Job? = null
    var job2: Job? = null
    var job3: Job? = null

    parentJob = launch {
        job1 = launch {
            delay(1000L)
        }

        job2 = launch {
            delay(3000L)
        }

        job3 = launch {
            delay(5000L)
        }
    }

    delay(500L)

    parentJob.children.forEachIndexed { index, job ->
        when (index) {
            0 -> println("job1 === job is ${job1 === job}")
            1 -> println("job2 === job is ${job2 === job}")
            2 -> println("job3 === job is ${job3 === job}")
        }
    }

    parentJob.join() // 这里会挂起大约5秒钟
    logX("Process end!")
}


fun test9() = runBlocking {
    val parentJob: Job
    var job1: Job? = null
    var job2: Job? = null
    var job3: Job? = null

    parentJob = launch {
        job1 = launch {
            logX("Job1 start!")
            delay(1000L)
            logX("Job1 done!") // ①，不会执行
        }

        job2 = launch {
            logX("Job2 start!")
            delay(3000L)
            logX("Job2 done!") // ②，不会执行
        }

        job3 = launch {
            logX("Job3 start!")
            delay(5000L)
            logX("Job3 done!")// ③，不会执行
        }
    }

    delay(500L)

    parentJob.children.forEachIndexed { index, job ->
        when (index) {
            0 -> println("job1 === job is ${job1 === job}")
            1 -> println("job2 === job is ${job2 === job}")
            2 -> println("job3 === job is ${job3 === job}")
        }
    }

    parentJob.cancel() // 变化在这里
    logX("Process end!")
}


fun test10() = runBlocking {
    suspend fun getResult1(): String {
        delay(1000L) // 模拟耗时操作
        return "Result1"
    }

    suspend fun getResult2(): String {
        delay(1000L) // 模拟耗时操作
        return "Result2"
    }

    suspend fun getResult3(): String {
        delay(1000L) // 模拟耗时操作
        return "Result3"
    }

    val results = mutableListOf<String>()

    val time = measureTimeMillis {
        results.add(getResult1())
        results.add(getResult2())
        results.add(getResult3())
    }
    println("Time: $time")
    println(results)
}


fun test11() = runBlocking {
    suspend fun getResult1(): String {
        delay(1000L) // 模拟耗时操作
        return "Result1"
    }

    suspend fun getResult2(): String {
        delay(1000L) // 模拟耗时操作
        return "Result2"
    }

    suspend fun getResult3(): String {
        delay(1000L) // 模拟耗时操作
        return "Result3"
    }

    val results: List<String>

    val time = measureTimeMillis {
        val result1 = async { getResult1() }
        val result2 = async { getResult2() }
        val result3 = async { getResult3() }

        results = listOf(result1.await(), result2.await(), result3.await())
    }

    println("Time: $time")
    println(results)
}


fun test12() = runBlocking {
    val job = launch {
        logX("First coroutine start!")
        delay(1000L)
        logX("First coroutine end!")
    }

    job.join()
    val job2 = launch(job) {
        logX("Second coroutine start!")
        delay(1000L)
        logX("Second coroutine end!")
    }
    job2.join()
    logX("Process end!")
}