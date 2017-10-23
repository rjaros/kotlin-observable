package com.lightningkite.kotlin.observable.property

/**
 *
 * Created by jivie on 2/22/16.
 */
class ObservablePropertySubObservable<A, B>(
        val owningObservable: ObservableProperty<A>,
        val getter: (A) -> ObservableProperty<B>
) : EnablingMutableCollection<(B) -> Unit>(), MutableObservableProperty<B> {

    var currentSub: ObservableProperty<B>? = null

    override var value: B
        get() = owningObservable.value.let(getter).value
        set(value) {
            val currentSub = owningObservable.value.let(getter)
            if (currentSub is MutableObservableProperty<B>) {
                currentSub.value = value
            } else throw Exception("ObservableProperty is not mutable")
        }

    val outerCallback = { a: A ->
        update()
        resub()
    }
    val innerCallback = { b: B -> update() }

    override fun enable() {
        owningObservable.add(outerCallback)
        resub()
    }

    override fun disable() {
        owningObservable.remove(outerCallback)
        unsub()
    }

    private fun resub() {
        unsub()
        val sub = owningObservable.value.let(getter)
        sub += innerCallback
        currentSub = sub
    }

    private fun unsub() {
        currentSub?.remove(innerCallback)
        currentSub = null
    }
}

fun <A, B> ObservableProperty<A>.subObs(getterFun: (A) -> ObservableProperty<B>) = ObservablePropertySubObservable(this, getterFun)