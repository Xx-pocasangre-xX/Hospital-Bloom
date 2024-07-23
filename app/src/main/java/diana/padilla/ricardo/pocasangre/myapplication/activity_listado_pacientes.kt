package diana.padilla.ricardo.pocasangre.myapplication

import Modelo.ClaseConexion
import Modelo.Pacientes
import RecyclerViewHelpers.AdaptadorPacientes
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class activity_listado_pacientes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_pacientes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAgregar = findViewById<ImageView>(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            val pantallaRegistroPacientes = Intent(this, activity_registro_pacientes::class.java)
            startActivity(pantallaRegistroPacientes)
            overridePendingTransition(0, 0)
        }

        val rcvPacientes = findViewById<RecyclerView>(R.id.rcvPacientes)

        rcvPacientes.layoutManager = LinearLayoutManager(this)

        fun obtenerPacientes(): List<Pacientes> {
            val listaPacientes = mutableListOf<Pacientes>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { conn ->
                val statement = conn.createStatement()
                val resultSet = statement?.executeQuery("SELECT * FROM Pacientes")!!
                while (resultSet.next()) {
                    val id_paciente = resultSet.getInt("id_paciente")
                    val nombres = resultSet.getString("nombres")
                    val apellidos = resultSet.getString("apellidos")
                    val edad = resultSet.getInt("edad")
                    val enfermaedad = resultSet.getString("enfermaedad")
                    val numero_habitacion = resultSet.getInt("numero_habitacion")
                    val numero_cama = resultSet.getInt("numero_cama")
                    val fecha_ingreso = resultSet.getDate("fecha_ingreso")
                    val valoresJuntos = Pacientes(
                        id_paciente,
                        nombres,
                        apellidos,
                        edad,
                        enfermaedad,
                        numero_habitacion,
                        numero_cama,
                        fecha_ingreso.toString()
                    )

                    listaPacientes.add(valoresJuntos)
                }
            }

                return listaPacientes

            }


        CoroutineScope(Dispatchers.IO).launch {
            val nuevosPacientes = obtenerPacientes()
            withContext(Dispatchers.IO){
                (rcvPacientes.adapter as? AdaptadorPacientes)?.ActualizarLista(nuevosPacientes)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val pacientesDB = obtenerPacientes()
            withContext(Dispatchers.Main){
                val adapter = AdaptadorPacientes(pacientesDB)
                rcvPacientes.adapter = adapter
            }
        }
    }
}