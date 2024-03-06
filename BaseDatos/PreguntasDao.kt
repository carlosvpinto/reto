package com.carlosvpinto.retoalconocimiento.BaseDatos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface PreguntasDao {

    @Insert
    fun insert(preguntas: PreguntasEntity)

    @Query("SELECT * FROM preguntas ORDER BY timestamp DESC ")
    fun getPreguntasAll(): MutableList<PreguntasEntity>

    @Query("SELECT * FROM preguntas ORDER BY RANDOM() LIMIT 1")
    fun obtenerPreguntaAleatoria(): PreguntasEntity

    @Delete
    fun borrarPregunta(pregunta: PreguntasEntity)

    @Query("DELETE FROM preguntas")
    fun borrarTodo()
}

