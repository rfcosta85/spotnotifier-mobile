package pt.ipca.spotnotifier

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var  auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val button = findViewById<ImageButton>(R.id.imageButtontrass)
        button.setOnClickListener {
            // Finish the current activity and go back to the previous one
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        db = Firebase.firestore

        auth.currentUser?.let {
            readUserFromDB(it.uid)
        }
    }

    private fun readUserFromDB(uid: String) {
        val docRef = db.collection("users").document("$uid")
        docRef.addSnapshotListener{ snapshot, e ->
            if (e != null) {
                Log.w("MEI", "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val name = snapshot.getString("name")
                val email = snapshot.getString("email")
                Toast.makeText( baseContext, "${name}", Toast.LENGTH_SHORT).show()
                val txt_nome = findViewById<TextView>(R.id.textViewNome)
                val txt_email = findViewById<TextView>(R.id.textView12)
                txt_nome.setText(name)
                txt_email.setText(email)
//                val tv = findViewById<TextView>(R.id.main_tv_username)
//                tv.text = name
                Log.d("MEI", "Current data: ${snapshot.data}")
            } else {
                Log.d("MEI", "Current data: null")
            }
        }
    }

    fun onAboutClick(view: View) {
        val intent = Intent(this, About::class.java)
        startActivity(intent)
    }

     fun onContactsClick(view: View) {
         val intent = Intent(this, Contacts::class.java)
         startActivity(intent)
     }

    fun onUpdateClick(view: View) {
        val intent = Intent(this, UpdateProfile::class.java)
        startActivity(intent)
    }



}
