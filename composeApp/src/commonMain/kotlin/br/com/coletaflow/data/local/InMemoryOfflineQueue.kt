package br.com.coletaflow.data.local

class InMemoryOfflineQueue : OfflineQueue {

    private val queue = mutableListOf<PendingEvent>()

    override suspend fun enqueue(event: PendingEvent) {
        queue.add(event)
    }

    override suspend fun dequeueAll(): List<PendingEvent> {
        val all = queue.toList()
        queue.clear()
        return all
    }

    override suspend fun clear() {
        queue.clear()
    }

    override suspend fun size(): Int = queue.size
}
