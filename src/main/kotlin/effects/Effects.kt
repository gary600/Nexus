package xyz.gary600.nexusclasses.effects

import org.bukkit.event.Listener
import xyz.gary600.nexusclasses.NexusClasses
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
            plugin.server.scheduler.runTaskTimer(plugin, { fn.call(this) } as Runnable, ann!!.delay, ann.period)
        }
    }
}

@Target(AnnotationTarget.FUNCTION)
annotation class TimerTask(val delay: Long, val period: Long)