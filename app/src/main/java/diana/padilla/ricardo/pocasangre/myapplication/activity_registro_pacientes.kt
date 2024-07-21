package diana.padilla.ricardo.pocasangre.myapplication

import Modelo.ClaseConexion
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class activity_registro_pacientes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_pacientes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnMedicamentos = findViewById<Button>(R.id.btnMedicamentos)
        btnMedicamentos.setOnClickListener {
            val pantallaNuevosMedicamentos = Intent(this, activity_nuevos_medicamentos::class.java)
            startActivity(pantallaNuevosMedicamentos)
            overridePendingTransition(0, 0)
        }

        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnHome.setOnClickListener {
            val pantallaListadoPacientes = Intent(this, activity_listado_pacientes::class.java)
            startActivity(pantallaListadoPacientes)
            overridePendingTransition(0, 0)
        }

        val txtNombrePacientes = findViewById<EditText>(R.id.txtNombrePacientes)
        val txtApellidosPacientes = findViewById<EditText>(R.id.txtApellidosPacientes)
        val txtEdadPacientes = findViewById<EditText>(R.id.txtEdadPacientes)
        val txtEnfermedadPacientes = findViewById<EditText>(R.id.txtEnfermedadPacientes)
        val txtNumeroHabitacion = findViewById<EditText>(R.id.txtNumeroHabitacion)
        val txtNumeroCama = findViewById<EditText>(R.id.txtNumeroCama)
        val txtFechaIngreso = findViewById<EditText>(R.id.txtFechaIngreso)

        txtFechaIngreso.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val calendarioSeleccionado = Calendar.getInstance()
                    calendarioSeleccionado.set(anioSeleccionado, mesSeleccionado, diaSeleccionado)
                        val fechaSeleccionada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                        txtFechaIngreso.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )

            datePickerDialog.show()
        }

        val btnEnviarFormulario = findViewById<Button>(R.id.btnEnviarFormulario)

        btnEnviarFormulario.setOnClickListener {

            val nombre = txtNombrePacientes.text.toString()
            val apellido = txtApellidosPacientes.text.toString()
            val edad = txtEdadPacientes.text.toString()
            val enfermedad = txtEnfermedadPacientes.text.toString()
            val habitacion = txtNumeroHabitacion.text.toString()
            val cama = txtNumeroCama.text.toString()
            val ingreso = txtFechaIngreso.text.toString()

            if(nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || enfermedad.isEmpty() || habitacion.isEmpty() || cama.isEmpty() || ingreso.isEmpty()){
                Toast.makeText(
                    this,
                    "Error, para enviar el registro debes llenar todas las casillas.",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val addPaciente =
                        objConexion?.prepareStatement("INSERT INTO Pacientes(nombres, apellidos, edad, enfermaedad, numero_habitacion, numero_cama, fecha_ingreso) VALUES (?, ?, ?, ?, ?, ?, ?)")!!
                    addPaciente.setString(1, txtNombrePacientes.text.toString())
                    addPaciente.setString(2, txtApellidosPacientes.text.toString())
                    addPaciente.setInt(3, txtEdadPacientes.text.toString().toInt())
                    addPaciente.setString(4, txtEnfermedadPacientes.text.toString())
                    addPaciente.setInt(5, txtNumeroHabitacion.text.toString().toInt())
                    addPaciente.setInt(6, txtNumeroCama.text.toString().toInt())
                    addPaciente.setString(7, txtFechaIngreso.text.toString())
                    addPaciente.executeQuery()

                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@activity_registro_pacientes)
                            .setTitle("Registro de paciente exitoso!")
                            .setMessage("El paciente se ha guardado exitosamente.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                        txtNombrePacientes.setText("")
                        txtApellidosPacientes.setText("")
                        txtEdadPacientes.setText("")
                        txtEnfermedadPacientes.setText("")
                        txtNumeroHabitacion.setText("")
                        txtNumeroCama.setText("")
                        txtFechaIngreso.setText("")
                    }
                }
            }
        }
    }
}