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
                finish()
            }

            button.setOnClickListener {
                val code = codeEditText.text.toString().trim()
                val newPassword = newPasswordEditText.text.toString().trim()

                if (code.isEmpty()) {
                    codeEditText.error = getString(R.string.insert_your_code)
                    codeEditText.requestFocus()
                    return@setOnClickListener
                }

                if (newPassword.isEmpty()) {
                    newPasswordEditText.error = getString(R.string.insert_your_new_password)
                    newPasswordEditText.requestFocus()
                    return@setOnClickListener
                }
                recoveryPassword(code, newPassword)
            }

        }
    /**
     * Resets the password using a confirmation code.
     *
     * This method attempts to reset the user's password using the provided confirmation code
     * and a new password. If successful, a toast message is displayed indicating success
     * and the user is redirected to the sign-in activity. Otherwise, a toast message is
     * displayed indicating an error.
     *
     * @param code The confirmation code sent to the user.
     * @param newPassword The new password to be set.
     */
    fun recoveryPassword(code: String, newPassword: String){
        auth.confirmPasswordReset(code, newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, getString(R.string.new_password_success),
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                } else {
                    Toast.makeText(baseContext, getString(R.string.error_new_password),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
