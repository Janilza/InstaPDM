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
        setContentView((int) R.layout.activity_login);
        this.mAuth = FirebaseAuth.getInstance();
        this.loginEmailText = (EditText) findViewById(R.id.register_email);
        this.loginPassText = (EditText) findViewById(R.id.repeat_password);
        this.loginButton = (Button) findViewById(R.id.loginButton);
        this.loginRegister = (Button) findViewById(R.id.loginRegister);
        this.loginProgress = (ProgressBar) findViewById(R.id.loginProgressBar);
        this.loginRegister.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        this.loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String obj = LoginActivity.this.loginEmailText.getText().toString();
                String obj2 = LoginActivity.this.loginPassText.getText().toString();
                if (!TextUtils.isEmpty(obj) && !TextUtils.isEmpty(obj2)) {
                    LoginActivity.this.loginProgress.setVisibility(View.VISIBLE);
                    LoginActivity.this.mAuth.signInWithEmailAndPassword(obj, obj2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                LoginActivity.this.sendToMainActivity();
                            } else {
                                String message = task.getException().getMessage();
                                Context context = LoginActivity.this;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Error : ");
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
