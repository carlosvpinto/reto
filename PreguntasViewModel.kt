package com.carlosvpinto.retoalconocimiento

import androidx.lifecycle.ViewModel

class PreguntasViewModel : ViewModel() {
    // Datos que deseas persistir
    var CantiPreguntas: Int = 0
    var editTextValues1: Array<String?> = arrayOfNulls(16)
    var editTextValues2: Array<String?> = arrayOfNulls(16)
    var linearLayoutVisibilities: BooleanArray = booleanArrayOf()

}