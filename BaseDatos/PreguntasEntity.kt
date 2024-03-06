package com.carlosvpinto.retoalconocimiento.BaseDatos

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "preguntas")
data class PreguntasEntity (

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Long = 0,

        @ColumnInfo(name = "nivel")
        @NonNull
        var nivel: String,

        @ColumnInfo(name = "pregunta")
        @NonNull
        var pregunta: String,
        @ColumnInfo(name = "opcionA")
        @NonNull
        var opcionA: String,

        @ColumnInfo(name = "opcionB")
        @NonNull
        var opcionB: String,

        @ColumnInfo(name = "opcionC")
        @NonNull
        var opcionC: String,

        @ColumnInfo(name = "opcionD")
        @NonNull
        var opcionD: String,


        @ColumnInfo(name = "respuestaC")
        @NonNull
        var respuestaC: String,

        @ColumnInfo(name = "explicacionR")
        @NonNull
        var explicacionR: String,

        @ColumnInfo(name = "timestamp")

        var timestamp: Long

)
