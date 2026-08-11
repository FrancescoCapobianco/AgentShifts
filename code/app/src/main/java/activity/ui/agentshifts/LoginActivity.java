package activity.ui.agentshifts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.SharedPreferences;

import System.Service.ShiftSystem;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button btnLogin;
    private TextView textRegistrati;

    private ShiftSystem shiftSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("AgentShiftsPrefs", MODE_PRIVATE);
        
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);
        if (isDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        shiftSystem = ShiftSystem.getInstance(this);

        String savedUser = prefs.getString("username", null);
        if (savedUser != null && shiftSystem.autoLogin(savedUser)) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.inputUsername);
        editPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textRegistrati = findViewById(R.id.textRegistrati);
    }

    // btnLogin
    public void loginVerify(View v) {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Inserisci Username e Password", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean loginOk = shiftSystem.login(username, password);

        if (loginOk) {
            SharedPreferences.Editor editor = getSharedPreferences("AgentShiftsPrefs", MODE_PRIVATE).edit();
            editor.putString("username", username);
            editor.apply();

            Toast.makeText(this, "Accesso confermato!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        } else
            Toast.makeText(this, "Credenziali errate! Riprova!", Toast.LENGTH_LONG).show();
    }

    // textRegistrati
    public void RegistrationProcedure(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

}