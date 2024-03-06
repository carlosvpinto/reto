package com.carlosvpinto.retoalconocimiento.ui.preguntas

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import az.plainpie.PieView
import az.plainpie.animation.PieAngleAnimation
import com.airbnb.lottie.LottieAnimationView
import com.carlosvpinto.retoalconocimiento.BaseDatos.AppDataBase
import com.carlosvpinto.retoalconocimiento.BaseDatos.PreguntasDao
import com.carlosvpinto.retoalconocimiento.PreguntasViewModel
import com.carlosvpinto.retoalconocimiento.R
import com.carlosvpinto.retoalconocimiento.databinding.FragmentPreguntasBinding
import com.carlosvpinto.retoalconocimiento.fragments.AciertosFragment
import com.carlosvpinto.retoalconocimiento.fragments.ContinuarFragment
import com.carlosvpinto.retoalconocimiento.fragments.DescripcionFragment
import com.carlosvpinto.retoalconocimiento.fragments.InterfacePreguntas
import com.carlosvpinto.retoalconocimiento.ui.FireworksView
import com.ekn.gruzer.gaugelibrary.FullGauge
import com.ekn.gruzer.gaugelibrary.Range
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PreguntasFragment : Fragment(), AciertosFragment.OnFragmentBClosedListener,
    DescripcionFragment.DentroFragmentDescripcionListener {
    private var preguntasDao: PreguntasDao?= null
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null
    private lateinit var respuestaCorrecta: String
    private lateinit var DescripcionRespuesta: String

    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var buttons: List<ToggleButton>

    private lateinit var handler: Handler

    private val preguntasViewModel: PreguntasViewModel by viewModels()

    private val TAG= "PREGUNTA"
    var countDownTimer: CountDownTimer? = null


    private var _binding: FragmentPreguntasBinding? = null


    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPreguntasBinding.inflate(inflater, container, false)
        val root: View = binding.root
        configurarGaude()
        activartiempo()
        reproducirSonido(requireContext(), "reloj")

         buttons = listOf(binding.toggleA, binding.toggleB, binding.toggleC, binding.toggleD)
        desaparecerBotones(buttons)


       // animacioBotones()
        lifecycleScope.launch {
            animacioBotones()
        }


        binding.toggleA.setOnClickListener {
            reproducirSonido(requireContext(), "click_button")
            detenerTiempo()
            bloquearBtn(binding.lnerRespuestas)
            verificaRepuesta()

        }

        binding.toggleB.setOnClickListener {
            reproducirSonido(requireContext(), "click_button")
            detenerTiempo()

            bloquearBtn(binding.lnerRespuestas)
            verificaRepuesta()
        }
        binding.toggleC.setOnClickListener {
            reproducirSonido(requireContext(), "click_button")
            detenerTiempo()

            bloquearBtn(binding.lnerRespuestas)
            verificaRepuesta()
        }
        binding.toggleD.setOnClickListener {
            reproducirSonido(requireContext(), "click_button")
            detenerTiempo()
            bloquearBtn(binding.lnerRespuestas)
            verificaRepuesta()
        }

        binding.btn5050.setOnClickListener {
            // Llama a la función para hacer invisibles dos botones aleatorios
            inhabilitarBoton(binding.btn5050)
            hacerInvisiblesBotonesAleatorios(buttons, obtenerToggleRCorrecta(binding.lnerRespuestas))

        }

        binding.btnSaltar.setOnClickListener {
           // desbloquearBtn(binding.lnerRespuestas)
            // animacioBotones()
            desaparecerBotones(buttons)
            lifecycleScope.launch {
                animacioBotones()
            }
            inhabilitarBoton(binding.btnSaltar)
            mostrarPregunta()
        }
        handler = Handler(Looper.getMainLooper())

        // Inicializa el TextView con un texto vacío
        binding.txtPregunta.text = ""



        mostrarPregunta()


        return root
    }


    private fun inhabilitarBoton(boton:ImageButton) {
       boton.isEnabled= false
        reproducirSonido2(requireContext(),"bono")
        val background1 = R.drawable.degradado_fondo_gris
        val backgrounds =
            ContextCompat.getDrawable(requireContext(), background1)
        boton.background = backgrounds

    }
    private fun habilitarBoton(boton:ImageButton) {
        boton.isEnabled= true
        val background1 = R.drawable.degradado_fondo_oscuro
        val backgrounds =
            ContextCompat.getDrawable(requireContext(), background1)
        boton.background = backgrounds

    }



    private fun animarTexto(texto: String) {
        // Restablece el texto del TextView
        binding.txtPregunta.text = ""

        // Obtiene el texto completo
        val textoCompleto = texto.toCharArray()

        // Crea un handler en el hilo principal
        val handler = Handler(Looper.getMainLooper())

        // Itera sobre cada carácter con un retraso
        textoCompleto.forEachIndexed { index, char ->
            handler.postDelayed({
                // Agrega el carácter actual al TextView
                binding.txtPregunta.append(char.toString())

                // Si es el último carácter, puedes realizar alguna acción adicional
                if (index == textoCompleto.size - 1) {
                    // Por ejemplo, puedes llamar a una función cuando se complete la animación
                    // onAnimationComplete()
                }
            }, index * 50L) // Ajusta el retraso según tu preferencia
        }
    }



  fun realizaPregunta() {
      Log.d(TAG, "realizaPregunta: ")
        activartiempo()
        reproducirSonido(requireContext(), "reloj")
        mostrarPregunta()
        detenerTiempo()
        activartiempo()
        limpiarBotones()
    }



private fun mostrarPregunta() {
    binding.lnerRespuestas.visibility= View.VISIBLE
    GlobalScope.launch(Dispatchers.Main) {
        try {
            val database = AppDataBase.getInstance(requireContext())

            val preguntaAleatoria = withContext(Dispatchers.IO) {
                database.preguntasDao.obtenerPreguntaAleatoria()
            }
            binding.relativeLayout.visibility= View.VISIBLE
            // Inicia la animación de la máquina de escribir
            animarTexto(preguntaAleatoria?.pregunta!!)

           // binding.txtPregunta.text = preguntaAleatoria?.pregunta
            respuestaCorrecta= preguntaAleatoria.respuestaC
            DescripcionRespuesta = preguntaAleatoria.explicacionR

            // Lista de ToggleButtons
            val toggleButtons = listOf(binding.toggleA, binding.toggleB, binding.toggleC, binding.toggleD)
            val opciones = listOf(preguntaAleatoria?.opcionA, preguntaAleatoria?.opcionB, preguntaAleatoria?.opcionC, preguntaAleatoria?.opcionD)

            toggleButtons.forEachIndexed { index, toggleButton ->
                val opcion = opciones[index]
                toggleButton.text = opcion
                toggleButton.textOn = opcion
                toggleButton.textOff = opcion
            }

        } catch (e: Exception) {
            Log.e("BASEDEDATOS", "Error al obtener datos de la base de datos: ${e.message}")
        }
    }
}

    private fun limpiarBotones() {
        // Lista de ToggleButtons
        val toggleButtons = listOf(binding.toggleA, binding.toggleB, binding.toggleC, binding.toggleD)

        toggleButtons.forEach { toggleButton ->
            toggleButton.isChecked = true
            toggleButton.background = obtenerFondoToggleButton()
        }
    }

    private fun obtenerFondoToggleButton(): Drawable {
        // Aquí debes devolver la instancia de fondo según tus necesidades
        val background1 = R.drawable.toggle_background
        return ContextCompat.getDrawable(requireContext(), background1)!!
    }


    private fun activartiempo() {
        countDownTimer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundo = (millisUntilFinished / 1000).toInt()
                binding.fullGauge.value = segundo.toDouble()
                binding.txtReloj.text= segundo.toString()

            }

            override fun onFinish() {
                detenerSonido()
                reproducirSonido(requireContext(),"perdida")
            }
        }.start()
    }

    // Función para detener el tiempo
    private fun detenerTiempo() {
        countDownTimer?.cancel()
    }

    //configurar la barra de tiempo Gaude ***yo ******************
    private fun configurarGaude(){
        val range = Range()
        range.color = Color.parseColor("#ce0000")
        range.from = 0.0
        range.to = 4.0

        val range2 = Range()
        range2.color = Color.parseColor("#E3E500")
        range2.from = 4.0
        range2.to = 12.0

        val range3 = Range()
        range3.color = Color.parseColor("#00b20b")
        range3.from = 12.0
        range3.to = 20.0

        binding.fullGauge.minValue = 0.0
        binding.fullGauge.maxValue = 20.0
        binding.fullGauge.value = 20.0

        binding.fullGauge.addRange(range)
        binding.fullGauge.addRange(range2)
        binding.fullGauge.addRange(range3)

        binding.fullGauge.isUseRangeBGColor = true
        binding.fullGauge.isDisplayValuePoint = false


    }
    fun reproducirSonido2(context: Context, nombre: String) {
        // Liberar recursos del MediaPlayer anterior
        mediaPlayer2?.release()

        // Inicializar un nuevo MediaPlayer
        val soundId = context.resources.getIdentifier(nombre, "raw", context.packageName)
        mediaPlayer2 = MediaPlayer.create(context, soundId)

        // Iniciar la reproducción
        mediaPlayer2?.start()

        // Configurar un listener para liberar recursos cuando la reproducción termine
        mediaPlayer2?.setOnCompletionListener {
            mediaPlayer2?.release()
            mediaPlayer2 = null
        }
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

    //verifica Respuesta
    private fun verificaRepuesta() {
        val botonpresionado = obtenerTogglePresionado(binding.lnerRespuestas)
        val botonRCorrecta = obtenerToggleRCorrecta(binding.lnerRespuestas)
        // Añadir un retraso de 1 segundo antes de ejecutar el código
        Handler().postDelayed({

        if (botonRCorrecta!= null){
            cambiarfondo(botonRCorrecta,"presionboton")
        }
            Handler().postDelayed({
                if (botonpresionado?.textOn == respuestaCorrecta) {
                    val botonActivo = botonpresionado.id


                    val cantidadAciertos =  sumaCantRespuesta(preguntasViewModel.CantiPreguntas)

                    //LLAMA AL FRAGMENT DE ACIERRTOS*************************
                    llamarAciertos(cantidadAciertos)

                    Log.d(
                        "RESPUESTA",
                        "verificaRepuesta CORRECTA:cantidadAciertos $cantidadAciertos botonpresionado?.textOn ${botonpresionado?.textOn} respuestaCorrecta $respuestaCorrecta "
                    )
                } else {
                   llamarDescripcion(DescripcionRespuesta)

                    Log.d(
                        "RESPUESTA",
                        "verificaRepuesta INCORRECTA:botonpresionado?.textOn ${botonpresionado?.textOn} respuestaCorrecta $respuestaCorrecta "
                    )
                }
            },500) // 1000 milisegundos = 1 segundo
        },1200) // 1000 milisegundos = 1 segundo
    }
    private fun sumaCantRespuesta(cant:Int):Int{
        var cantidad=0
        Log.d(
            "RESPUESTA",
            "sumaCantRespuesta cant $cant preguntasViewModel.CantiPreguntas ${preguntasViewModel.CantiPreguntas} "
        )
        cantidad= cant+1
        preguntasViewModel.CantiPreguntas= cantidad
        return cantidad
    }





    private fun cambiarfondo(botonActivo:ToggleButton, sonido:String) {
        val background1 = R.drawable.degradado_fondo_oscuro
        val background2 = R.drawable.degradado_fondo_naranja
        val background3 = R.drawable.degradado_fondo_verde
        val backgrounds = arrayOf(
            ContextCompat.getDrawable(requireContext(), background1),
            ContextCompat.getDrawable(requireContext(), background2),
            ContextCompat.getDrawable(requireContext(), background3)
        )
        val botonpresionado = obtenerTogglePresionado(binding.lnerRespuestas)
        if (botonpresionado?.textOn == respuestaCorrecta) {

            reproducirSonido(requireContext(), sonido)

            binding.lottiCelebration.visibility = View.VISIBLE
            lottieAnimationView = binding.lottiCelebration
            // Llamar al método playAnimation para iniciar la animación
            lottieAnimationView.playAnimation()
        }else{
            reproducirSonido(requireContext(), "fallo2")
        }
        botonActivo.background = backgrounds[2]
    }


    private fun obtenerTogglePresionado(layout: ViewGroup): ToggleButton? {
        for (i in 0 until layout.childCount) {
            val view = layout.getChildAt(i)

            if (view is ToggleButton && !view.isChecked) {
                // Retorna el ToggleButton que está presionado
                return view
            }
        }
        return null
    }

    //Optiene El boton con la respuesta Correcta
    private fun obtenerToggleRCorrecta(layout: ViewGroup): ToggleButton? {
        for (i in 0 until layout.childCount) {
            val view = layout.getChildAt(i)

            if (view is ToggleButton && view.textOn==respuestaCorrecta) {
                // Retorna el ToggleButton que está presionado
                return view
            }
        }
        return null
    }


    //bloquear botones
    private fun bloquearBtn(layout: ViewGroup) {
        for (i in 0 until layout.childCount) {
            val view = layout.getChildAt(i)

                // Si el elemento no es un ViewGroup, deshabilita su interactividad
                view.isEnabled = false

        }
    }

    //bloquear botones
    private fun desbloquearBtn(layout: ViewGroup){
        for (i in 0 until layout.childCount) {
            val view = layout.getChildAt(i)

            // Si el elemento no es un ViewGroup, deshabilita su interactividad
            view.isEnabled = true

        }
    }
fun desaparecerBotones(botones: List<ToggleButton>) {
    // Recorre la lista de botones
    binding.relativeLayout.visibility= View.INVISIBLE
    for (boton in botones) {
        boton.visibility = View.INVISIBLE
        // Animación de desvanecimiento
    }
}

    private fun aparecerBotonSolo(boton:ToggleButton){
        boton.visibility= View.VISIBLE
        val animator = AnimatorInflater.loadAnimator(requireContext(), R.animator.aparecer_botones)
        animator.setTarget(boton)
        animator.start()
    }
    private suspend fun animacioBotones() {
        val buttons = listOf(binding.toggleA, binding.toggleB, binding.toggleC, binding.toggleD)
        binding.relativeLayout.visibility= View.VISIBLE
        val animarTxt = AnimatorInflater.loadAnimator(requireContext(),R.animator.aparecer_botones)
        animarTxt.setTarget(binding.txtPregunta)
        animarTxt.start()
        // Usar corutinas para agregar un retraso entre las animaciones
        buttons.forEachIndexed { index, button ->
            delay(500L ) // Esperar 0.5 segundos antes de cada animación
            //animateButtonAppearance(button, index)
            aparecerBotonSolo(button)
        }
    }
    fun desaparecer50x50(botones: List<ToggleButton>) {
        // Recorre la lista de botones
        botones.random()

        binding.relativeLayout.visibility= View.INVISIBLE
        for (boton in botones) {
            boton.visibility = View.INVISIBLE
            // Animación de desvanecimiento
        }
    }


    private fun hacerInvisiblesBotonesAleatorios(botones: List<ToggleButton>, botonRCorrecta: ToggleButton?) {
        // Filtra los botones que no son el correcto
        val botonesNoCorrectos = botones.filter { it != botonRCorrecta }

        // Si hay menos de 2 botones no correctos, no hacemos nada
        if (botonesNoCorrectos.size < 2) return

        // Genera dos índices aleatorios distintos
        val indiceBoton1 = (0 until botonesNoCorrectos.size).random()
        var indiceBoton2: Int

        do {
            indiceBoton2 = (0 until botonesNoCorrectos.size).random()
        } while (indiceBoton1 == indiceBoton2) // Asegura que los índices sean distintos

        // Introduce un retraso de 1 segundo antes de hacer invisibles los botones aleatorios
        Handler(Looper.getMainLooper()).postDelayed({
            // Hace invisible los dos botones aleatorios no correctos después de 1 segundo
            hacerInvisibleBoton(botonesNoCorrectos[indiceBoton1])
            hacerInvisibleBoton(botonesNoCorrectos[indiceBoton2])
        }, 1000)
    }

    // Función para hacer invisible un botón
    private fun hacerInvisibleBoton(boton: ToggleButton) {
        boton.visibility = View.INVISIBLE
    }


    override fun onPause() {
        super.onPause()

        // Pausar o detener la reproducción de sonido
        detenerSonido()
    }



    companion object {

    }



    private fun llamarAciertos(cantAciertos: Int) {
        binding.lnerRespuestas.visibility = View.INVISIBLE
        binding.relativeLayout.visibility = View.INVISIBLE
        binding.frameReloj.visibility= View.VISIBLE

        // Añadir un retraso de 1 segundo antes de ejecutar el código
        Handler().postDelayed({
            val fragmentTag = "AciertosFragment"  // Usar el tag correcto aquí
            val existingFragment = childFragmentManager.findFragmentByTag(fragmentTag)
            if (existingFragment == null) {
                val fragment = AciertosFragment()

                // Crear un Bundle y agregar los datos
                val bundle = Bundle()
                bundle.putInt("cantAciertos", cantAciertos)
                //***************************

                fragment.setOnFragmentBClosedListener(this)


                childFragmentManager.beginTransaction()
                    .replace(R.id.containerFragmentAciertos, fragment, fragmentTag)
                    .commitAllowingStateLoss()


                //****************************

                // Asignar el Bundle al Fragmento B
                fragment.arguments = bundle

                // Esperar a que el fragmento padre haya pasado por onResume()
                childFragmentManager.beginTransaction()
                    .replace(R.id.containerFragmentAciertos, fragment, fragmentTag)
                    .commitAllowingStateLoss()
            }
        }, 1000) // 1000 milisegundos = 1 segundo
    }

    private fun llamarDescripcion(descripcion: String) {
        binding.lnerRespuestas.visibility = View.INVISIBLE
        binding.relativeLayout.visibility = View.INVISIBLE
        binding.frameReloj.visibility = View.INVISIBLE


        val fragmentTag = "Descripcion"
        val existingFragment = childFragmentManager.findFragmentByTag(fragmentTag)
        if (existingFragment == null) {

            val fragment = DescripcionFragment()

            // Crear un Bundle y agregar los datos
            val bundle = Bundle()
            // fragment.setOnFragmentBClosedListener(this)
            bundle.putString("descripcion", descripcion)
            // Asignar el Bundle al Fragmento B
            fragment.arguments = bundle

            fragment.tomardentroFragmentDesClosedListener(this)
            // Esperar a que el fragmento padre haya pasado por onResume()
            childFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, fragment, fragmentTag)
                .commitAllowingStateLoss()
        }
    }
    private fun llamarTercerFragmento(descripcion: String) {
        val fragmentTag = "TercerFragmento"
        val existingFragment = childFragmentManager.findFragmentByTag(fragmentTag)

        if (existingFragment == null) {
            // Crear instancia del tercer fragmento
            val tercerFragmento = ContinuarFragment()

            // Pasar datos al tercer fragmento si es necesario
            val bundle = Bundle()
            bundle.putString("descripcion", descripcion)
            tercerFragmento.arguments = bundle

            // Realizar la transacción para reemplazar el fragmento actual
            childFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, tercerFragmento, fragmentTag)
                .addToBackStack(null) // Opcional, agregar a la pila de retroceso
                .commitAllowingStateLoss()
        }
    }



    private fun llamarContinuarFrag(descripcion: String) {
        val fragmentTag = "TercerFragmento"
        val existingFragment = childFragmentManager.findFragmentByTag(fragmentTag)

        if (existingFragment == null) {
            // Crear instancia del tercer fragmento
            val tercerFragmento = ContinuarFragment()

            // Pasar datos al tercer fragmento si es necesario
            val bundle = Bundle()
            bundle.putString("descripcion", descripcion)
            tercerFragmento.arguments = bundle

            // Realizar la transacción para reemplazar el fragmento actual
            childFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, tercerFragmento, fragmentTag)
                .addToBackStack(null) // Opcional, agregar a la pila de retroceso
                .commitAllowingStateLoss()
        }

        Log.d(TAG, "llamarContinuarFrag: llamarContinuarFrag")
    }
    override fun onFragmentBClosed() {
        // Llamada a la función realizaPregunta() desde el Fragmento A
        Log.d(TAG, "onFragmentBClosed: PreguntaFragment")
        //TEMPORAL
        detenerTiempo()
        realizaPregunta()

        desbloquearBtn(binding.lnerRespuestas)
        // animacioBotones()
        desaparecerBotones(buttons)
        lifecycleScope.launch {
            animacioBotones()
        }
    }

    override fun onFragmentDescripcionClosed() {
        llamarContinuarFrag("nada")

    }


}