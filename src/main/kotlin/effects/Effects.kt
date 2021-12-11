package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import xyz.gary600.nexusclasses.NexusClasses
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

/**
 * The base class of the various Effects classes. Simplifies registering event handlers and tasks
 */
abstract class Effects : Listener {
    /**
     * Register this Effects' event handlers and tasks with the Bukkit scheduler
     */
    fun register() {
        // Register event handlers normally
        Bukkit.getServer().pluginManager.registerEvents(this, NexusClasses.instance!!)

        this::class.declaredMemberFunctions // for each declared member function (not static)
            .map { it to it.findAnnotation<TimerTask>() } // get its TimerTask annotation
            .filter { (_, ann) -> ann != null } // skip it if the annotation is not found
            .forEach { (fn, ann) ->
            Bukkit.getServer().scheduler.runTaskTimer( // register the task by wrapping it in a TaskWrapper
                NexusClasses.instance!!,
                TaskWrapper(this, fn),
                ann!!.delay,
                ann.period
            )
        }
    }

    // Internal wrapper
    private class TaskWrapper(private val fx: Effects, private val fn: KFunction<*>) : Runnable {
        override fun run() {
            fn.call(fx) // just dispatch out to the actual function
        }
    }
}

/**
 * Marks this function as being a Bukkit task to register with Scheduler.runTaskTimer
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TimerTask(val delay: Long, val period: Long)