package pt.ipca.spotnotifier

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import pt.ipca.spotnotifier.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var  auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = Firebase.firestore

        auth.currentUser?.let {
            readUserFromDB(it.uid)
        }

        binding.register.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Reads a user's data from the Firestore database.
     *
     * This method retrieves the user document from the "users" collection for the provided user ID.
     * It uses `addSnapshotListener` to listen for changes to the document and logs the retrieved data
     * or an error message to the logcat.
     *
     * @param uid The unique identifier for the user.
     */
    private fun readUserFromDB(uid: String) {
        val docRef = db.collection("users").document("$uid")
        docRef.addSnapshotListener{ snapshot, e ->
            if (e != null) {
                Log.w("MEI", "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val name = snapshot.getString("name")
                Log.d("MEI", "Current data: ${snapshot.data}")
            } else {
                Log.d("MEI", "Current data: null")
            }
        }
    }

    /**
     * Handles user login process.
     *
     * This method retrieves user credentials (email and password) from the UI, calls
     * `authUserInFireBase` to authenticate the user in Firebase Authentication, and displays
     * toast messages
     * based on the login success or failure.
     *
     * @param v The View clicked to trigger login (usually a button).
     */
    fun login(v: View) {
        val email = binding.mainEtName.text.toString()
        val password = binding.mainEtPassword.text.toString()
        authUserInFireBase(email, password)
    }

    /**
     * Initiates password recovery for the user.
     *
     * This method retrieves the user's email address from the UI, validates it for emptiness,
     * and calls
     * `auth.sendPasswordResetEmail` to send a password reset email to the provided address.
     * It then displays
     * a toast message indicating whether the email was sent successfully or not
     * (using the same message
     * for both cases might be confusing - consider revising the message strings).
     *
     * @param view The View clicked to trigger password recovery (usually a button).
     */
    fun recovery(view: View) {
        val email: String = binding.mainEtName.text.toString().trim()

        if (email.isEmpty()) {
            binding.mainEtName.error = getString(R.string.email_input_alert)
            Toast.makeText(this, getString(R.string.email_input_alert),
                Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, getString(R.string.recovery_password_button),
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, getString(R.string.recovery_password_button),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Authenticates the user using Firebase Authentication.
     *
     * This method attempts to sign the user in using the provided email and password with
     * `signInWithEmailAndPassword`. It uses `addOnCompleteListener` to handle the asynchronous task
     * completion, navigating to the MapsActivity and displaying toast messages based on
     * success or failure.
     * Consider improving the toast messages to provide more specific feedback to the user (e.g.,
     * mention invalid password or non-existent email).
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    private fun authUserInFireBase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(
                        baseContext,
                        getString(R.string.authentication_alert),
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.authentication_alert_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}