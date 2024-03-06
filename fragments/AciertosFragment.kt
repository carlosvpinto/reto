package com.carlosvpinto.retoalconocimiento.fragments

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.carlosvpinto.retoalconocimiento.R
import com.carlosvpinto.retoalconocimiento.databinding.FragmentAciertosBinding
import com.carlosvpinto.retoalconocimiento.databinding.FragmentDescripcionBinding
import com.carlosvpinto.retoalconocimiento.ui.preguntas.PreguntasFragment
import java.util.Random


class AciertosFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null

    private var _binding: FragmentAciertosBinding? = null
    private val binding get() = _binding!!
    private lateinit var botonArriba: ToggleButton
    private lateinit var botonAbajo: ToggleButton
    private  var cantAciertos: Int? = 0
    private var onFragmentBClosedListener: OnFragmentBClosedListener? = null
    private val TAG= "PREGUNTA"

    interface OnFragmentBClosedListener {

        fun onFragmentBClosed()

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAciertosBinding.inflate(inflater, container, false)
        val root: View = binding.root

       // cerrarDespuesde5()
        reproducirSonido(requireContext(),"winfantasia")
        binding.relativeLayout.setOnClickListener {

            cerrarFragmentoB()
            //cerrarFragment()
        }

         cantAciertos = arguments?.getInt("cantAciertos")
        val botones = listOf(
            binding.toggle15,
            binding.toggle14,
            binding.toggle13,
            binding.toggle12,
            binding.toggle11,
            binding.toggle10,
            binding.toggle9,
            binding.toggle8,
            binding.toggle7,
            binding.toggle6,
            binding.toggle5,
            binding.toggle4,
            binding.toggle3,
            binding.toggle2,
            binding.toggle1
        )
        if (cantAciertos!=null){
                fondoAciertosBotones(botones,cantAciertos!!)
        }

        return root
    }

    fun setOnFragmentBClosedListener(listener: OnFragmentBClosedListener) {
        onFragmentBClosedListener = listener
    }

    private fun cerrarFragmentoB() {
        // Lógica para cerrar el Fragmento B
        Log.d(TAG, "cerrarFragmentoB: ")
        onFragmentBClosedListener?.onFragmentBClosed()
        val fragmentManager = parentFragmentManager

        // Comprobar si el FragmentManager y el Fragment actual son válidos
        if (fragmentManager != null && isAdded) {
            // Remover el Fragment actual
            fragmentManager.beginTransaction()
                .remove(this)
                .commitAllowingStateLoss()

        }
    }


    fun fondoAciertosBotones(botones: List<ToggleButton>, cant: Int) {

            val background1 = R.drawable.degradado_fondo_claro
            val background2 = R.drawable.degradado_fondo_naranja
            val background3 = R.drawable.degradado_fondo_verde
            val backgrounds = arrayOf(
                ContextCompat.getDrawable(requireContext(), background1),
                ContextCompat.getDrawable(requireContext(), background2),
                ContextCompat.getDrawable(requireContext(), background3)
            )

            // Verificar que cant esté en el rango adecuado
            if (cant in 1..botones.size) {
                if (cant<=1){
                    botonAbajo = botones[cant - 1]
                    botonAbajo.background= backgrounds[1]
                    Log.d("FONDOB", " IF fondoAciertosBotones: botonAbajo ${botonAbajo}")
                }else{

                    botonAbajo = botones[cant - 2]
                    botonAbajo.background= backgrounds[1]
                    Log.d("FONDOB", " ELSE fondoAciertosBotones: botonAbajo ${botonAbajo}")
                }


                botonArriba = botones[cant-1]

                // Agregar animación de parpadeo antes de cambiar el fondo
                val parpadeoAnimacion = AlphaAnimation(1.0f, 0.0f)
                parpadeoAnimacion.duration = 500 // Duración de 500 milisegundos
                parpadeoAnimacion.repeatMode = Animation.REVERSE
                parpadeoAnimacion.repeatCount = 2 // Puedes ajustar la cantidad de repeticiones según sea necesario

                parpadeoAnimacion.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        // Llamado cuando la animación comienza
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        // Llamado cuando la animación termina
                        cambiarFondoDespuesParpadeo(botonAbajo,botonArriba, backgrounds[1], backgrounds[0])
                        if (cantAciertos==15){
                            reproducirSonido(requireContext(),"victoria")
                            //llamar al fragmen de Victoria
                        }


                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        // Llamado cuando la animación se repite
                    }
                })

                botonAbajo.startAnimation(parpadeoAnimacion)
            }


    }


    private fun cambiarFondoDespuesParpadeo(botonAbajo: ToggleButton,botonArriba:ToggleButton, fondoNaranja: Drawable?,fondoAzul:Drawable?) {
        // Esperar un breve periodo antes de cambiar el fondo
        Handler().postDelayed({
            botonAbajo.background=fondoAzul
            botonArriba.background = fondoNaranja
        }, 200) // Ajusta la duración del parpadeo y el tiempo de espera según tus necesidades
    }

    private fun  reproducirSonido(context: Context, nombre: String) {
        // Verificar si ya hay un MediaPlayer en uso
        if (mediaPlayer != null) {
            // Detener la reproducción
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
        // Obtener el ID del recurso de sonido
        val soundId = context.resources.getIdentifier(nombre, "raw", context.packageName)

        if (soundId != 0) {
            // Crear un nuevo MediaPlayer
            mediaPlayer = MediaPlayer.create(context, soundId)

            mediaPlayer?.setVolume(1.0f, 1.0f)

            mediaPlayer?.start()
        } else {
            // El recurso de sonido no fue encontrado
            Log.e("ReproducirSonido", "Recurso de sonido no encontrado para: $nombre")
        }
    }

    // Llamada al método cuando se desea detener el sonido
    private fun detenerSonido() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

//    private fun cerrarDespuesde5(){
//        // Utilizar un Handler para cerrar el fragment después de 5 segundos
//        Handler(Looper.getMainLooper()).postDelayed({
//            cerrarFragment()
//            // Acción a realizar después de 5 segundos (cierra el fragmento)
//           // fragmentManager?.beginTransaction()?.remove(this)?.commit()
//        }, 5000) // 5000 milisegundos = 5 segundos
//    }



    override fun onPause() {
        super.onPause()
        detenerSonido()
    }


    companion object {

    }


}