package kittoku.opensstpclient

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText


internal enum class PreferenceKey(val value: String) {
    HOST("HOST"),
    USERNAME("USERNAME"),
    PASSWORD("PASSWORD"),
    SUBNET("SUBNET"),
    ROUTE("ROUTE")
}

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val host = findViewById<EditText>(R.id.host)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val buttonConnect = findViewById<Button>(R.id.connect)
        val buttonDisconnect = findViewById<Button>(R.id.discoonect)

        host.setText(prefs.getString(PreferenceKey.HOST.value, null))
        username.setText(prefs.getString(PreferenceKey.USERNAME.value, null))
        password.setText(prefs.getString(PreferenceKey.PASSWORD.value, null))

        buttonConnect.setOnClickListener {
            val editor = prefs.edit()
            editor.putString(PreferenceKey.HOST.value, host.text.toString())
            editor.putString(PreferenceKey.USERNAME.value, username.text.toString())
            editor.putString(PreferenceKey.PASSWORD.value, password.text.toString())
            editor.apply()

            val intent = VpnService.prepare(applicationContext)
            if (intent == null) onActivityResult(0, Activity.RESULT_OK, null)
            else startActivityForResult(intent, 0)
        }

        buttonDisconnect.setOnClickListener { startService(getServiceIntent().setAction(VpnAction.ACTION_DISCONNECT.value)) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) { startService(getServiceIntent().setAction(VpnAction.ACTION_CONNECT.value)) }
    }

    private fun getServiceIntent(): Intent {
        return Intent(this, SstpVpnService::class.java)
    }
}
