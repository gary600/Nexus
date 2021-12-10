package xyz.gary600.nexusclasses.effects

import org.bukkit.event.Listener
import xyz.gary600.nexusclasses.NexusClasses
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

/**
 * The base class of the various Effects classes. Handles registering the event handler and tasks
 */
abstract class Effects : Listener {
    fun register(plugin: NexusClasses) {
        plugin.server.pluginManager.registerEvents(this, plugin)

        this::class.declaredMemberFunctions
            .map { it to it.findAnnotation<TimerTask>() }
            .filter { (_, ann) -> ann != null }
            .forEach { (fn, ann) ->
            plugin.server.scheduler.runTaskTimer(
                plugin,
                TaskWrapper(this, fn),
                ann!!.delay,
                ann.period
            )
        }
    }

    // Internal wrapper
    private class TaskWrapper(private val fx: Effects, private val fn: KFunction<*>) : Runnable {
        override fun run() {
            fn.call(fx)
        }
    }
}

@Target(AnnotationTarget.FUNCTION)
annotation class TimerTask(val delay: Long, val period: Long)