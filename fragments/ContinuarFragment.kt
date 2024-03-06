package com.carlosvpinto.retoalconocimiento.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.carlosvpinto.retoalconocimiento.R
import com.carlosvpinto.retoalconocimiento.databinding.FragmentContinuarBinding
import com.carlosvpinto.retoalconocimiento.databinding.FragmentPreguntasBinding

private var _binding: FragmentContinuarBinding? = null


private val binding get() = _binding!!
class ContinuarFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentContinuarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnAbandonar.setOnClickListener {
            // Navegar a nav_home desde continuarFragment
            findNavController().navigate(R.id.action_preguntas_to_home)
        }


        return root
    }



    companion object {

    }
}