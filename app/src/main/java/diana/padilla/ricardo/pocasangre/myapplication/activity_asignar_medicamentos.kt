package diana.padilla.ricardo.pocasangre.myapplication

import Modelo.ClaseConexion
import Modelo.Pacientes
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.ResultSet

class activity_asignar_medicamentos : AppCompatActivity() {

    private lateinit var txtHoraAplicacion: EditText
    private lateinit var spPacientes: Spinner
    private lateinit var spMedicamentos: Spinner
    private lateinit var btnAplicarMedicamento: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_asignar_medicamentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegresar = findViewById<ConstraintLayout>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            val pantallaNuevosMedicamentos = Intent(this, activity_nuevos_medicamentos::class.java)
            startActivity(pantallaNuevosMedicamentos)
            overridePendingTransition(0, 0)
        }

        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnHome.setOnClickListener {
            val pantallaHome = Intent(this, activity_listado_pacientes::class.java)
            startActivity(pantallaHome)
            overridePendingTransition(0, 0)
        }

        txtHoraAplicacion = findViewById(R.id.txtHoraAplicacion)
        spPacientes = findViewById(R.id.spPacientes)
        spMedicamentos = findViewById(R.id.spMedicamentos)
        btnAplicarMedicamento = findViewById(R.id.btnAplicarMedicamento)

        loadPacientes()
        loadMedicamentos()

        btnAplicarMedicamento.setOnClickListener {
            val horaAplicacion = txtHoraAplicacion.text.toString()
            val pacienteSeleccionado = spPacientes.selectedItem.toString()
            val medicamentoSeleccionado = spMedicamentos.selectedItem.toString()

            CoroutineScope(Dispatchers.Main).launch {
                val idPaciente = getIdPaciente(pacienteSeleccionado)
                val idMedicamento = getIdMedicamento(medicamentoSeleccionado)

                if (idPaciente != null && idMedicamento != null) {
                    val result =
                        saveAplicacionMedicamento(horaAplicacion, idPaciente, idMedicamento)
                    if (result) {
                        Toast.makeText(
                            this@activity_asignar_medicamentos,
                            "Asignación guardada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        txtHoraAplicacion.setText("")
                    } else {
                        Toast.makeText(
                            this@activity_asignar_medicamentos,
                            "Error al guardar la asignación",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@activity_asignar_medicamentos,
                        "Error: No se pudo obtener los IDs",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

        private fun loadPacientes() {
            CoroutineScope(Dispatchers.IO).launch {
                val pacientes = fetchPacientesFromDB()
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@activity_asignar_medicamentos,
                        android.R.layout.simple_spinner_item,
                        pacientes
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spPacientes.adapter = adapter
                }
            }
        }

        private fun loadMedicamentos() {
            CoroutineScope(Dispatchers.IO).launch {
                val medicamentos = fetchMedicamentosFromDB()
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@activity_asignar_medicamentos,
                        android.R.layout.simple_spinner_item,
                        medicamentos
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spMedicamentos.adapter = adapter
                }
            }
        }

        private suspend fun fetchPacientesFromDB(): List<String> = withContext(Dispatchers.IO) {
            val pacientes = mutableListOf<String>()
            val query = "SELECT nombres, apellidos FROM Pacientes"
            val objConexion = ClaseConexion().cadenaConexion()

            objConexion?.let {
                try {
                    val statement = it.createStatement()
                    val resultSet = statement.executeQuery(query)

                    while (resultSet.next()) {
                        val nombre = resultSet.getString("nombres")
                        val apellido = resultSet.getString("apellidos")
                        pacientes.add("$nombre $apellido")
                    }

                    resultSet.close()
                    statement.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    it.close()
                }
            }

            pacientes
        }

        private suspend fun fetchMedicamentosFromDB(): List<String> = withContext(Dispatchers.IO) {
            val medicamentos = mutableListOf<String>()
            val query = "SELECT nombre_medicamento FROM Medicamentos"
            val objConexion = ClaseConexion().cadenaConexion()

            objConexion?.let {
                try {
                    val statement = it.createStatement()
                    val resultSet = statement.executeQuery(query)

                    while (resultSet.next()) {
                        val nombre_medicamento = resultSet.getString("nombre_medicamento")
                        medicamentos.add(nombre_medicamento)
                    }

                    resultSet.close()
                    statement.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    it.close()
                }
            }

            medicamentos
        }

        private suspend fun getIdPaciente(nombreCompleto: String): Int? =
            withContext(Dispatchers.IO) {
                val query =
                    "SELECT id_paciente FROM Pacientes WHERE (nombres || ' ' || apellidos) =  ?"
                val objConexion = ClaseConexion().cadenaConexion()

                objConexion?.let {
                    try {
                        val statement = it.prepareStatement(query)
                        statement.setString(1, nombreCompleto)
                        val resultSet = statement.executeQuery()

                        if (resultSet.next()) {
                            val idPaciente = resultSet.getInt("id_paciente")
                            resultSet.close()
                            return@withContext idPaciente
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        it.close()
                    }

                }
                null
            }

        private suspend fun getIdMedicamento(nombreMedicamento: String): Int? =
            withContext(Dispatchers.IO) {
                val query = "SELECT id_medicamento FROM Medicamentos WHERE nombre_medicamento = ?"
                val objConexion = ClaseConexion().cadenaConexion()

                objConexion?.let {
                    try {
                        val statement = it.prepareStatement(query)
                        statement.setString(1, nombreMedicamento)
                        val resultSet = statement.executeQuery()

                        if (resultSet.next()) {
                            val idMedicamento = resultSet.getInt("id_medicamento")
                            resultSet.close()
                            return@withContext idMedicamento
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        it.close()
                    }
                }

                null
            }

        private suspend fun saveAplicacionMedicamento(
            horaAplicacion: String,
            idPaciente: Int,
            idMedicamento: Int
        ): Boolean = withContext(Dispatchers.IO) {
            val query =
                "INSERT INTO Aplicacion_Medicamentos (hora_aplicaciones, id_paciente, id_medicamento) VALUES (?, ?, ?)"
            val objConexion = ClaseConexion().cadenaConexion()

            objConexion?.let {
                try {
                    val statement = it.prepareStatement(query)
                    statement.setString(1, horaAplicacion)
                    statement.setInt(2, idPaciente)
                    statement.setInt(3, idMedicamento)
                    statement.executeUpdate()
                    statement.close()
                    it.close()
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                } finally {
                    it.close()
                }
            } ?: false
        }
    }