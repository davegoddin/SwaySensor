package net.davegoddin.swaysensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    lateinit var thread1 : Thread
    lateinit var etIP : EditText
    lateinit var etPort : EditText
    lateinit var tvMessages: TextView
    lateinit var btnTransmit : Button
    lateinit var tvAccelX : TextView
    lateinit var tvAccelY : TextView
    lateinit var tvAccelZ : TextView

    lateinit var SERVER_IP : String
    var SERVER_PORT : Int = 11000

    lateinit var accelerometerManager : AccelerometerManager

    var transmitting : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etIP = findViewById(R.id.etIP)
        etPort = findViewById(R.id.etPort)
        tvMessages = findViewById(R.id.tvMessages)
        btnTransmit = findViewById(R.id.btnTransmit)
        tvAccelX = findViewById(R.id.tvAccelX)
        tvAccelY = findViewById(R.id.tvAccelY)
        tvAccelZ = findViewById(R.id.tvAccelZ)
        transmitting = false

        val btnConnect : Button = findViewById(R.id.btnConnect)
        btnConnect.setOnClickListener {
            tvMessages.text = ""
            SERVER_IP = etIP.text.toString().trim()
            SERVER_PORT = etPort.text.toString().trim().toInt()
            thread1 = Thread(InitialiseSocket())
            thread1.start()

        }

        btnTransmit.setOnClickListener {
            transmitting = !transmitting

            if (transmitting)
            {
                btnTransmit.text = "STOP TRANSMITTING"
            }
            else
            {
                btnTransmit.text = "TRANSMIT"
            }
        }

        accelerometerManager = AccelerometerManager(this, ::triggerTransmission)
        accelerometerManager.registerListener()

    }

    lateinit var output : PrintWriter
    lateinit var input : BufferedReader

    fun triggerTransmission (values : FloatArray) {
        if (transmitting)
        {
            val x = values[0].toString()
            val y = values[1].toString()
            val z = values[2].toString()
            val accelJsonString = "{\"x\":\"$x\", \"y\":\"$y\",\"z\":\"$z\"}"
            runOnUiThread { tvMessages.append("$accelJsonString\n") }
            Thread(TransmitData(accelJsonString)).start()
        }
    }



    inner class InitialiseSocket : Runnable
    {
        override fun run() {
            var socket : Socket
            try {
                socket = Socket(SERVER_IP, SERVER_PORT)
                output = PrintWriter(socket.getOutputStream())
                input = BufferedReader(InputStreamReader(socket.getInputStream()))

                runOnUiThread { tvMessages.text = "Connected\n" }
                Thread(ProcessServerComms()).start()

            } catch (e: IOException)
            {
                e.message?.let { Log.e("Thread1", it) }
            }
        }
    }

    inner class ProcessServerComms : Runnable {
        override fun run() {
            while (true) {
                try {
                    val message : String? = input.readLine()
                    if (message != null) {
                        runOnUiThread { tvMessages.append("server: " + message + "\n")}
                    } else {
                        thread1 = Thread(InitialiseSocket())
                        thread1.start()
                        return;
                    }
                } catch (e : IOException)
                {
                    e.message?.let { Log.e("Thread2", it) }
                }

            }
        }
    }

    inner class TransmitData(val message: String) : Runnable {
        override fun run() {
            output.write(message)
            output.flush()
            runOnUiThread{
                tvMessages.append("client: " + message + "\n")
            }

        }
    }

}