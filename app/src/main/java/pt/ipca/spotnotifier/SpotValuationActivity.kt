package pt.ipca.spotnotifier

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.spotnotifier.databinding.ActivitySpotValuationBinding

class SpotValuationActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var editText: EditText
    private lateinit var binding: ActivitySpotValuationBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySpotValuationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        radioGroup = findViewById(R.id.radio_group)
        editText = findViewById(R.id.edit_text)

        binding.mainBtnSendEvaluation.setOnClickListener { view ->
            onSendButtonClick(view)
        }

    }

    /**
     * Handles the click event on the "Send" button.
     *
     * This method retrieves the selected radio button and its corresponding text (evaluation)
     * from the radio group. It then retrieves the message text from the EditText field. If a radio
     * button
     * is selected (checked), it calls `sendEvaluation` to send the evaluation and message to the
     * database.
     * Otherwise, it displays a toast message indicating that a selection is required.
     *
     * @param view The View clicked (usually the "Send" button).
     */
    private fun onSendButtonClick(view: View) {
        val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        if (selectedRadioButtonId != -1) {
            val selectedRadioButton: RadioButton? = findViewById(selectedRadioButtonId)
            val evaluation = selectedRadioButton?.text.toString()
            val message = binding.editText.text.toString()

            sendEvaluation(evaluation, message)

        } else {
            Toast.makeText(this, "Por favor seleciona uma opção",
                Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Sends the user's evaluation and message to the Firestore database.
     *
     * This method creates a HashMap named `evaluationData` containing the selected evaluation
     * and message.
     * It then adds this data to the "evaluations" collection in Firestore using
     * `db.collection("evaluations").add`.
     * It uses `addOnSuccessListener` to handle successful data addition,
     * displaying a success toast message,
     * navigating to the MapsActivity, and finishing the current activity.
     * It uses `addOnFailureListener` to handle
     * failures, displaying an error toast message.
     *
     * @param evaluation The user's selected evaluation (e.g., "Good", "Bad").
     * @param message The user's message.
     */
     private fun sendEvaluation(evaluation: String, message: String) {
         val evaluationData = hashMapOf(
             "evaluation" to evaluation,
             "message" to message
         )

         db.collection("evaluations")
             .add(evaluationData)
             .addOnSuccessListener { documentReference ->
                 Toast.makeText(baseContext, "Mensagem enviada com sucesso",
                     Toast.LENGTH_SHORT).show()
                 val intent = Intent(this, MapsActivity::class.java)
                 startActivity(intent)
                 finish()
             }
             .addOnFailureListener {
                 Toast.makeText(baseContext, "Erro ao enviar a mensagem",
                     Toast.LENGTH_SHORT).show()
             }
    }
}