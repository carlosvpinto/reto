package com.carlosvpinto.retoalconocimiento.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.carlosvpinto.retoalconocimiento.BaseDatos.AppDataBase
import com.carlosvpinto.retoalconocimiento.R
import com.carlosvpinto.retoalconocimiento.databinding.FragmentGalleryBinding
import com.carlosvpinto.retoalconocimiento.ui.FireworksView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var fireworksView: FireworksView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
     //   cargarDatos()
        binding.btnCargarCsv.setOnClickListener {
            cargarDatosCsv()
        }
        binding.btnVerDatos.setOnClickListener {
            verDatosRoom()
        }
        fireworksView = binding.fireExplosion
        // Mostrar los fuegos artificiales
        fireworksView.visibility = View.VISIBLE
        fireworksView.invalidate()

        return root
    }

    private fun verDatosRoom() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val database = AppDataBase.getInstance(requireContext())
                val datos = withContext(Dispatchers.IO) {
                    // Realiza las operaciones de la base de datos en el hilo de fondo
                    database.mostrarDatosRoom()
                }
                Log.d("BASEDEDATOS", "verDatosRoom:  datos $datos")

                val preguntas = withContext(Dispatchers.IO) {
                    database.preguntasDao.getPreguntasAll()
                }
                preguntas.forEach { pregunta ->
                    binding.data.append("${pregunta.id} ${pregunta.pregunta} ${pregunta.opcionA} ${pregunta.opcionB}  ${pregunta.opcionC} ${pregunta.respuestaC} ${pregunta.explicacionR}\n")
                }
            } catch (e: Exception) {
                Log.e("BASEDEDATOS", "Error al obtener datos de la base de datos: ${e.message}")


            }
        }
    }




    private fun cargarDatosCsv() {
        val database = AppDataBase.getInstance(requireContext())
//        database.cargarDatosDesdeCSV(requireContext())
        database.cargarDatosDesdeCSV(context) {
            // Este bloque se ejecutará cuando se completen las operaciones de carga de datos
            Log.d("BASEDEDATOS", "Carga de datos completada")
            // Puedes realizar cualquier acción adicional aquí
        }
    }

    private fun cargarDatos() {
        try {

            val assetManager = requireContext().assets
            val minput = InputStreamReader(assetManager.open("Category.csv"))
            val reader = BufferedReader(minput)

            var line: String?
            var displayData: String = ""

            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line!!.split(";")

                // Asegúrate de que la línea tenga al menos 6 columnas
                if (row.size >= 6) {
                    // Agregar las 5 columnas adicionales
                    val nuevaLinea = "${row[0]}\t${row[1]}\t${row[2]}\t${row[3]}\t${row[4]}\t${row[5]}\n"
                    displayData += nuevaLinea
                }
            }

            // Asumiendo que 'binding' es la referencia a tu ViewBinding
            var txtData = binding.data
            txtData.text = displayData
        } catch (e: IOException) {
            // Manejar la excepción si ocurre un error al abrir el archivo
            e.printStackTrace()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}