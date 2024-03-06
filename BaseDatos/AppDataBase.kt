package com.carlosvpinto.retoalconocimiento.BaseDatos

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
//import org.apache.poi.hssf.usermodel.HSSFWorkbook
//import org.apache.poi.ss.usermodel.Workbook
//import org.apache.poi.ss.usermodel.WorkbookFactory
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader


@Database(entities = [PreguntasEntity::class], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {

    abstract val preguntasDao: PreguntasDao



    fun cargarDatosDesdeCSV(context: Context?, onComplete: () -> Unit) {
        try {
            GlobalScope.launch(Dispatchers.IO) { // Ejecutar en un hilo de fondo
                val dao = preguntasDao // Reemplázalo con tu DAO
                dao.borrarTodo() // Borra los datos actuales si es necesario

                val assetManager = context?.assets
                val minput = InputStreamReader(assetManager?.open("Category.csv"))
                val reader = BufferedReader(minput)

                var line: String?
                var displayData: String = ""

                while (reader.readLine().also { line = it } != null) {
                    val row: List<String> = line!!.split(";")

                    // Asegúrate de que la línea tenga al menos 6 columnas
                    if (row.size >= 7) {
                        // Eliminar espacios en blanco al final de cada elemento
                        val trimmedRow = row.map { it.trim() }

                        // Agregar las 8 columnas adicionales
                        val nuevaLinea = "${trimmedRow[0]}\t${trimmedRow[1]}\t${trimmedRow[2]}\t${trimmedRow[3]}\t${trimmedRow[4]}\t${trimmedRow[5]}\t${trimmedRow[6]}\t${trimmedRow[7]}\n"

                        val nivel = trimmedRow[0]
                        val pregunta = trimmedRow[1]
                        val opcionA = trimmedRow[2]
                        val opcionB = trimmedRow[3]
                        val opcionC = trimmedRow[4]
                        val opcionD = trimmedRow[5]
                        val respuestaC = trimmedRow[6]
                        val explicacionR = trimmedRow[7]

                        val preguntasEntity = PreguntasEntity(
                            nivel = nivel,
                            pregunta = pregunta,
                            opcionA = opcionA,
                            opcionB = opcionB,
                            opcionC = opcionC,
                            opcionD = opcionD,
                            respuestaC = respuestaC,
                            explicacionR = explicacionR,
                            timestamp = 123456
                        )
                        dao.insert(preguntasEntity)
                        Log.d("BASEDEDATOS", "cargarDatosDesdeCSV: datos de carga dentro del for preguntasEntity $preguntasEntity")

                        displayData += nuevaLinea
                    }
                }

                // Llamada a la función onComplete después de cargar los datos
                onComplete.invoke()
            }
        } catch (e: Exception) {
            Log.e("BASEDEDATOS", "Error al cargar el archivo Csv: ${e.message}")
        }
    }

    fun mostrarDatosRoom(){
        try {
            GlobalScope.launch {

                val dao = preguntasDao // Reemplázalo con tu DAO
                val datosRoom=  dao.getPreguntasAll() // Borra los datos actuales si es necesario
                Log.d("BASEDEDATOS", "mostrarDatosRoom:  datosRoom $datosRoom")


            }
        }catch (e: Exception){
            Log.e("BASEDEDATOS", "Error al cargar Mostrar Datos de Room: ${e.message}")
        }
    }


    companion object{
        private const val DATABASE_NAME = "db_preguntas"
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            synchronized(this) {
                return INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDataBase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                DATABASE_NAME
            ).build()
        }
    }


}