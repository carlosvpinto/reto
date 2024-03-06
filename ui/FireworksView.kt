package com.carlosvpinto.retoalconocimiento.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.Random


class FireworksView(context: Context, attrs: AttributeSet?) :
    View(context, attrs) {

    private val paint = Paint()
    private val particles = mutableListOf<Particle>()
    private val random = Random()

    private val explosionColors = intArrayOf(
        Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Actualizar y dibujar las partículas
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.update()

            paint.color = particle.color
            paint.alpha = particle.alpha
            canvas.drawCircle(particle.x, particle.y, particle.radius, paint)

            if (particle.alpha <= 0) {
                iterator.remove()
            }
        }

        // Agregar nuevas partículas (explosiones)
        if (random.nextFloat() < 0.03f) {
            val explosionX = width.toFloat() * random.nextFloat()
            val explosionY = height.toFloat() * 0.1f // Aparecer en la parte superior
            val explosionColor = explosionColors[random.nextInt(explosionColors.size)]
            createExplosion(explosionX, explosionY, explosionColor)
        }

        // Invalidate the view to force a redraw
        invalidate()
    }

    private fun createExplosion(x: Float, y: Float, color: Int) {
        val numParticles = 30
        for (i in 0 until numParticles) {
            val angle = Math.toRadians(random.nextDouble() * 360).toFloat()
            val speed = random.nextFloat() * 5 + 2
            val particleColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color))
            particles.add(
                Particle(
                    x,
                    y,
                    particleColor,
                    Math.cos(angle.toDouble()).toFloat() * speed,  // VelocityX
                    Math.sin(angle.toDouble()).toFloat() * speed,  // VelocityY
                    random.nextFloat() * 20 + 10
                )
            )
        }
    }

    private data class Particle(
        var x: Float,
        var y: Float,
        var color: Int,
        var velocityX: Float,
        var velocityY: Float,
        var radius: Float,
        var alpha: Int = 255
    ) {

        fun update() {
            x += velocityX
            y += velocityY
            velocityY += 0.2f  // Gravedad

            // Disminuir la velocidad y radio
            velocityX *= 0.98f
            velocityY *= 0.98f
            radius *= 0.95f

            // Disminuir la opacidad
            alpha -= 5
        }
    }
}
