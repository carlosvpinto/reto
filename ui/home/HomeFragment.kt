package com.carlosvpinto.retoalconocimiento.ui.home

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.carlosvpinto.retoalconocimiento.BaseDatos.AppDataBase
import com.carlosvpinto.retoalconocimiento.R
import com.carlosvpinto.retoalconocimiento.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var mediaPlayerInicio: MediaPlayer? = null
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

    binding.btnJugar.setOnClickListener {
        detenerSonido()
        findNavController().navigate(R.id.action_fragment1_to_fragment2)
    }
        reproducirSonido(requireContext(),"heroico")
        return root
    }




    private fun reproducirSonido(context: Context, nombre: String) {
        // Verificar si ya hay un MediaPlayer en uso
        if (mediaPlayerInicio != null) {
            // Detener la reproducción
            mediaPlayerInicio?.stop()
            mediaPlayerInicio?.release()
            mediaPlayerInicio = null
        }

        // Obtener el ID del recurso de sonido
        val soundId = context.resources.getIdentifier(nombre, "raw", context.packageName)

        if (soundId != 0) {
            // Crear un nuevo MediaPlayer
            mediaPlayerInicio = MediaPlayer.create(context, soundId)

            mediaPlayerInicio?.setVolume(1.0f, 1.0f)

            // Agregar un listener para reiniciar el sonido cuando termine
            mediaPlayerInicio?.setOnCompletionListener {
                mediaPlayerInicio?.start()
            }

            mediaPlayerInicio?.start()
        } else {
            // El recurso de sonido no fue encontrado
            Log.e("ReproducirSonido", "Recurso de sonido no encontrado para: $nombre")
        }
    }
    // Llamada al método cuando se desea detener el sonido
    private fun detenerSonido() {
        mediaPlayerInicio?.stop()
        mediaPlayerInicio?.release()
        mediaPlayerInicio = null
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}