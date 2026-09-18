package com.phonebench.bench

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Простой GPU-бенчмарк: рендерит тысячи треугольников.
 * FPS измеряется в рендерере.
 */
class GpuBenchRenderer : GLSurfaceView.Renderer {

    var currentFps: Double = 0.0
        private set

    var trianglesCount: Int = 0
        private set

    private var frameCount = 0
    private var lastFpsTime = System.currentTimeMillis()
    private var startTime = 0L
    private var durationMs = 5000L
    private var isRunning = false

    private var vertexShader = 0
    private var fragmentShader = 0
    private var program = 0
    private var positionHandle = 0

    fun startBenchmark(durationMs: Long = 5000) {
        this.durationMs = durationMs
        frameCount = 0
        lastFpsTime = System.currentTimeMillis()
        startTime = System.currentTimeMillis()
        isRunning = true
    }

    fun stopBenchmark() {
        isRunning = false
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        val vertexCode = """
            attribute vec4 aPosition;
            void main() {
                gl_Position = aPosition;
                gl_PointSize = 2.0;
            }
        """.trimIndent()

        val fragmentCode = """
            precision mediump float;
            void main() {
                gl_FragColor = vec4(0.3, 0.9, 0.5, 1.0);
            }
        """.trimIndent()

        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexCode)
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        if (!isRunning) return

        GLES20.glUseProgram(program)
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Генерируем и рисуем много треугольников
        val triangleCount = 5000
        trianglesCount = triangleCount

        val vertices = FloatArray(triangleCount * 9)
        var idx = 0
        for (i in 0 until triangleCount) {
            val angle = (i / triangleCount.toFloat()) * Math.PI.toFloat() * 2
            val r = 0.3f + (i % 100) / 200f

            vertices[idx++] = (Math.cos(angle.toDouble()) * r).toFloat()
            vertices[idx++] = (Math.sin(angle.toDouble()) * r).toFloat()
            vertices[idx++] = 0f

            vertices[idx++] = (Math.cos(angle.toDouble() + 0.1) * r).toFloat()
            vertices[idx++] = (Math.sin(angle.toDouble() + 0.1) * r).toFloat()
            vertices[idx++] = 0f

            vertices[idx++] = (Math.cos(angle.toDouble() + 0.2) * r).toFloat()
            vertices[idx++] = (Math.sin(angle.toDouble() + 0.2) * r).toFloat()
            vertices[idx++] = 0f
        }

        val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        buffer.position(0)

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, buffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, triangleCount * 3)

        GLES20.glDisableVertexAttribArray(positionHandle)

        // Считаем FPS
        frameCount++
        val now = System.currentTimeMillis()
        if (now - lastFpsTime >= 1000) {
            currentFps = frameCount * 1000.0 / (now - lastFpsTime)
            frameCount = 0
            lastFpsTime = now
        }

        if (now - startTime >= durationMs) {
            isRunning = false
        }
    }

    private fun loadShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)
        return shader
    }

    companion object {
        /**
         * Баллы GPU.
         * 60 FPS = 6000 баллов (грубо)
         */
        fun calcScore(fps: Double): Long {
            return (fps * 100).toLong().coerceIn(100, 50_000)
        }
    }
}
