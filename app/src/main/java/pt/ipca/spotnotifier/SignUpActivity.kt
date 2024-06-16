package pt.ipca.spotnotifier

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import pt.ipca.spotnotifier.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var  auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = Firebase.firestore

        auth.currentUser?.let {
            readUserFromDB(it.uid)
        }
    }

    /**
     * Inserts a new user into the Firestore database.
     *
     * This method creates a new user document in the "users" collection with the provided user ID,
     * name, and email. It uses `addOnCompleteListener` to handle the asynchronous task completion
     * and logs the success or failure to the logcat.
     *
     * @param uid The unique identifier for the user.
     * @param name The user's name.
     * @param email The user's email address.
     */
    private fun insertUserIntoDB(uid: String, name: String, email: String) {
        val user = hashMapOf(
            "uid" to uid,
            "name" to name,
            "email" to email
        )
        db.collection("users").document("$uid")
            .set(user)
            .addOnCompleteListener{ documentReference ->
                Log.d("MEI", "DocumentSnapshot added with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w("MEI", "Error adding document", e)
            }

    }

    /**
     * Reads a user's data from the Firestore database.
     * This method retrieves the user document from the "users" collection for the provided user ID.
     * It uses `addSnapshotListener` to listen for changes to the document and logs the
     * retrieved data or an error message to the logcat.
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
     * Handles user registration process.
     *
     * This method retrieves user information from the UI (email, password, name),
     * shows a toast message
     * indicating successful registration (regardless of actual registration success),
     * and starts the sign-in
     * activity. It then calls `createUserInFirebase` to handle user creation in Firebase
     * Authentication.
     *
     * @param v The View clicked to trigger registration (usually a button).
     */
    fun register(v: View) {
        val email = findViewById<EditText>(R.id.main_et_email).text.toString()
        val password = findViewById<EditText>(R.id.main_et_password).text.toString()
        val name = findViewById<EditText>(R.id.main_et_name).text.toString()
        Toast.makeText(baseContext, getString(R.string.create_account_success), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SignInActivity::class.java)
        createUserInFirebase(name, email, password)
        startActivity(intent)
    }

    /**
     * Creates a new user in Firebase Authentication.
     *
     * This method attempts to create a new user with the provided email and password using
     * `createUserWithEmailAndPassword`. It uses `addOnCompleteListener` to handle
     * the asynchronous task
     * completion, logging success or failure messages and updating the UI accordingly.
     * If successful,
     * it also inserts the user's information into the database using `insertUserIntoDB`.
     *
     * @param name The user's name.
     * @param email The user's email address.
     * @param password The user's password.
     */
    private fun createUserInFirebase(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("ME", "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext,
                        "Authentication Success",
                        Toast.LENGTH_SHORT,
                    ).show()
                    if(user != null) {
                        insertUserIntoDB(user.uid, name, email)
                    }
                } else {
                    Log.w("MEI", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}

