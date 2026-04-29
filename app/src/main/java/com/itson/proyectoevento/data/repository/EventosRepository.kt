package com.itson.proyectoevento.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.itson.proyectoevento.data.model.Evento
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EventosRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val coleccion = firestore.collection("eventos")

    fun observarEventos(uid: String, esAdmin: Boolean): Flow<List<Evento>> = callbackFlow {
        val query: Query = if (esAdmin) {
            coleccion
        } else {
            coleccion.whereEqualTo("propietarioUid", uid)
        }

        val listener = query.addSnapshotListener { snapshot: QuerySnapshot?, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val eventos = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Evento::class.java)?.copy(
                    firestoreId = doc.id,
                    id = doc.id.hashCode()
                )
            } ?: emptyList()

            trySend(eventos)
        }

        awaitClose { listener.remove() }
    }

    suspend fun crearEvento(evento: Evento): Result<String> = runCatching {
        val docRef = coleccion.add(evento).await()
        docRef.id
    }

    suspend fun actualizarEvento(evento: Evento): Result<Unit> = runCatching {
        require(evento.firestoreId.isNotEmpty()) {
            "El evento debe tener firestoreId para actualizarse"
        }
        coleccion.document(evento.firestoreId).set(evento).await()
    }

    suspend fun eliminarEvento(firestoreId: String): Result<Unit> = runCatching {
        coleccion.document(firestoreId).delete().await()
    }

    suspend fun obtenerEvento(firestoreId: String): Result<Evento?> = runCatching {
        val snapshot = coleccion.document(firestoreId).get().await()
        snapshot.toObject(Evento::class.java)?.copy(firestoreId = snapshot.id)
    }
}
