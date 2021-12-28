package xyz.gary600.nexus.effects

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import xyz.gary600.nexus.Nexus
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

/**
 * The base class of the various Effects objects. Simplifies registering event handlers and tasks
 */
abstract class Effects : Listener {

    private var registered = false

    /**
     * Register this Effects' event handlers and tasks with Bukkit
     */
    fun register() {
        // Prevent registering multiple times
        if (registered) {
            throw Exception("This Effects class has already been registered")
        }

        // Register event handlers normally
        Bukkit.getServer().pluginManager.registerEvents(this, Nexus.instance)

        // Use reflection to find all annotated functions
        this::class.declaredMemberFunctions // for each declared member function (not static)
            .map { it to it.findAnnotation<TimerTask>() } // get its TimerTask annotation
            .filter { (_, ann) -> ann != null } // skip it if the annotation is not found
            .forEach { (fn, ann) ->
            Bukkit.getServer().scheduler.runTaskTimer( // register the task by wrapping it in a TaskWrapper
                Nexus.instance,
                TaskWrapper(this, fn),
                ann!!.delay,
                ann.period
            )
        }

        registered = true
    }

    // Internal wrapper
    private class TaskWrapper(private val fx: Effects, private val fn: KFunction<*>) : Runnable {
        override fun run() {
            fn.call(fx) // just dispatch out to the actual function
        }
    }
}

/**
 * Marks this function as being a Bukkit task to register with Scheduler.runTaskTimer.
 * Annotated function must be public
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TimerTask(val delay: Long, val period: Long)