package activity.ui.agentshifts;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import System.Service.ShiftSystem;
import System.Service.Objects.Azienda;
import System.Service.Objects.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText editNome;
    private EditText editCognome;
    private EditText editUsername;
    private EditText editPassword;
    private Spinner spinnerAzienda;

    private ShiftSystem shiftSystem;
    private List<Azienda> listaAziende;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        shiftSystem = ShiftSystem.getInstance(this);

        editNome = findViewById(R.id.inputNome);
        editCognome = findViewById(R.id.inputCognome);
        editUsername = findViewById(R.id.inputRegUsername);
        editPassword = findViewById(R.id.inputRegPassword);
        spinnerAzienda = findViewById(R.id.spinnerAzienda);

        loadSpinnerAziende();
    }

    private void loadSpinnerAziende() {
        listaAziende = shiftSystem.getAziendeDisponibili();

        List<String> nomiAziende = new ArrayList<>();
        for (Azienda azienda : listaAziende) {
            nomiAziende.add(azienda.getNome());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nomiAziende
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAzienda.setAdapter(adapter);
    }

    // btnRegistrati
    public void executeRegistration(View v) {
        String nome = editNome.getText().toString().trim();
        String cognome = editCognome.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show();
            return;
        }

        int posizioneSelezionata = spinnerAzienda.getSelectedItemPosition();
        Azienda aziendaScelta = listaAziende.get(posizioneSelezionata);

        User nuovoUtente = new User(
                nome,
                cognome,
                new Date(),
                username,
                "",
                "",
                9.50f,  10.50f, 2.00f,
                12.00f, 2.50f
        );
        nuovoUtente.setAziendaID(aziendaScelta.getId());

        boolean registrazioneOk = shiftSystem.registraUtente(nuovoUtente, password);

        if (registrazioneOk) {
            Toast.makeText(this, "Registrazione completata! Effettua il Login.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Errore: Username già in uso.", Toast.LENGTH_LONG).show();
        }
    }
}