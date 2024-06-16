package pt.ipca.spotnotifier

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RecoveryPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var codeEditText: EditText
    private lateinit var newPasswordEditText: EditText
        override fun onCreate(savedInstanceState: Bundle?){
            super.onCreate(savedInstanceState)
            setContentView(R.layout.recovery_password)

            newPasswordEditText = findViewById(R.id.editTextText3)
            codeEditText = findViewById(R.id.editTextText2)
            auth = FirebaseAuth.getInstance()

            val button = findViewById<ImageButton>(R.id.imageButton)
            button.setOnClickListener {
                // Finish the current activity and go back to the previous one
                finish()
            }

            button.setOnClickListener {
                val code = codeEditText.text.toString().trim()
                val newPassword = newPasswordEditText.text.toString().trim()

                if (code.isEmpty()) {
                    codeEditText.error = "Por favor, insira o código recebido no email"
                    codeEditText.requestFocus()
                    return@setOnClickListener
                }

                if (newPassword.isEmpty()) {
                    newPasswordEditText.error = "Por favor, insira uma nova senha"
                    newPasswordEditText.requestFocus()
                    return@setOnClickListener
                }
                recoveryPassword(code, newPassword)
            }

        }

    fun recoveryPassword(code: String, newPassword: String){
        auth.confirmPasswordReset(code, newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Senha redefinida com sucesso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Erro ao redefinir palavra passe", Toast.LENGTH_SHORT).show()
                }
            }
        Toast.makeText(baseContext, "Botão clicado", Toast.LENGTH_SHORT).show()
    }
}