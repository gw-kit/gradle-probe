package io.github.gwkit.gradleprobe.lib.reflect

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal inline fun <reified A : Annotation, reified V> Any.injectProperty(
    valueToInject: V,
) {
    val thisInstance = this
    thisInstance::class
        .memberProperties
        .asSequence()
        .filter { property -> property.hasAnnotation<A>() }
        .filter { property -> property.returnType.classifier == V::class }
        .filter { property -> property.isLateinit }
        .onEach { property -> property.isAccessible = true }
        .mapNotNull { property -> property as? KMutableProperty<*> }
        .forEach { property -> property.setter.call(thisInstance, valueToInject) }
}

internal inline fun <reified A : Annotation, reified V> Any.injectProperty(
    crossinline valueProvider: A.() -> V,
) {
    val thisInstance = this
    thisInstance::class
        .memberProperties
        .asSequence()
        .mapNotNull { property ->
            property.findAnnotation<A>()?.let { annotation -> annotation to property }
        }
        .filter { (_, property) -> property.returnType.classifier == V::class }
        .filter { (_, property) -> property.isLateinit }
        .onEach { (_, property) -> property.isAccessible = true }
        .mapNotNull { (annotation, property) ->
            (property as? KMutableProperty<*>)?.let { mutableProperty ->
                annotation.valueProvider() to mutableProperty
            }
        }
        .forEach { (valueToInject, property) -> property.setter.call(thisInstance, valueToInject) }
}
