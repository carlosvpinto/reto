package com.carlosvpinto.retoalconocimiento.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.carlosvpinto.retoalconocimiento.R
import com.carlosvpinto.retoalconocimiento.databinding.FragmentDescripcionBinding
import com.carlosvpinto.retoalconocimiento.ui.preguntas.PreguntasFragment


class DescripcionFragment : Fragment() {

    private var dentroFragmentDescripcionListener: DentroFragmentDescripcionListener? = null
    private var _binding: FragmentDescripcionBinding? = null
    private val binding get() = _binding!!

    interface DentroFragmentDescripcionListener {

        fun onFragmentDescripcionClosed()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentDescripcionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val descripcion = arguments?.getString("descripcion")
        // Inflate the layout for this fragment
        binding.txtDescripcion.text= descripcion
        // Utilizar un Handler para cerrar el fragment después de 5 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            // Acción a realizar después de 5 segundos (cierra el fragmento)
            fragmentManager?.beginTransaction()?.remove(this)?.commit()
            dentroFragmentDescripcionListener?.onFragmentDescripcionClosed()
        }, 4000) // 5000 milisegundos = 5 segundos
        binding.txtDescripcion.setOnClickListener {

        }

        return root
    }

    fun tomardentroFragmentDesClosedListener(listener: DentroFragmentDescripcionListener) {
        dentroFragmentDescripcionListener = listener
    }


    companion object {

    }
}