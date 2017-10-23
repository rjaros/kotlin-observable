package com.lightningkite.kotlin.collection

/**
 * Created by joseph on 12/14/16.
 */


class CollectionWriteOnlyMapping<S, E>(val source: MutableCollection<S>, val inputMapper: (E) -> S) : MutableCollection<E> {
    val map = HashMap<E, S>()

    override val size: Int get() = source.size

    override fun add(element: E): Boolean {
        val mapped = inputMapper(element)
        map[element] = mapped
        return source.add(mapped)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val mapped = elements.map(inputMapper)
        map.putAll(elements.zip(mapped))
        return source.addAll(mapped)
    }

    override fun remove(element: E): Boolean = source.remove(map.remove(element))
    override fun removeAll(elements: Collection<E>): Boolean = source.removeAll(elements.map { map[it] })
    override fun retainAll(elements: Collection<E>): Boolean = source.retainAll(elements.map { map[it] })

    override fun contains(element: E): Boolean = throw Exception("IllegalAccess")
    override fun containsAll(elements: Collection<E>): Boolean = throw Exception("IllegalAccess")
    override fun isEmpty(): Boolean = throw Exception("IllegalAccess")
    override fun clear() = throw Exception("IllegalAccess")
    override fun iterator(): MutableIterator<E> = throw Exception("IllegalAccess")
}

fun <S, E> MutableCollection<S>.mappingWriteOnly(inputMapper: (E) -> S) = CollectionWriteOnlyMapping(this, inputMapper)