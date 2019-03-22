package ipg.estg.mcm.instapdm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText loginEmailText;
    private EditText loginPassText;
    private ProgressBar loginProgress;
    private Button loginRegister;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView( R.layout.activity_login);
        this.mAuth = FirebaseAuth.getInstance();
        this.loginEmailText = findViewById(R.id.register_email);
        this.loginPassText = findViewById(R.id.repeat_password);
        this.loginButton =  findViewById(R.id.loginButton);
        this.loginRegister = findViewById(R.id.loginRegister);
        this.loginProgress = findViewById(R.id.loginProgressBar);
        this.loginRegister.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        this.loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String email = LoginActivity.this.loginEmailText.getText().toString();
                String pass = LoginActivity.this.loginPassText.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                    LoginActivity.this.loginProgress.setVisibility(View.VISIBLE);
                    LoginActivity.this.mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                LoginActivity.this.sendToMainActivity();
                            } else {
                                String message = task.getException().getMessage();
                                Context context = LoginActivity.this;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(R.string.string_error);
                                stringBuilder.append(message);
                                Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_LONG).show();

                            }
                            LoginActivity.this.loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    protected void onStart() {
        super.onStart();
        if (this.mAuth.getCurrentUser() != null) {
            sendToMainActivity();
        }
    }

    private void sendToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
