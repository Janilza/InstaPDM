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

public class RegisterActivity extends AppCompatActivity {
    private Button loginRegisterButton;
    private FirebaseAuth mAuth;
    private Button registerButton;
    private EditText registerEmailText;
    private EditText registerPassText;
    private ProgressBar registerProgressBar;
    private EditText repeatPassText;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_register);
        this.mAuth = FirebaseAuth.getInstance();
        this.registerEmailText = (EditText) findViewById(R.id.register_email);
        this.registerPassText = (EditText) findViewById(R.id.register_password);
        this.repeatPassText = (EditText) findViewById(R.id.repeat_password);
        this.registerButton = (Button) findViewById(R.id.registerButton);
        this.loginRegisterButton = (Button) findViewById(R.id.registerLogin);
        this.registerProgressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        this.loginRegisterButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                RegisterActivity.this.finish();
            }
        });
        this.registerButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String email = RegisterActivity.this.registerEmailText.getText().toString();
                String pass= RegisterActivity.this.registerPassText.getText().toString();
                String confirm_pass = RegisterActivity.this.repeatPassText.getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) & !TextUtils.isEmpty(confirm_pass)) {
                    if (pass.equals(confirm_pass)) {
                        RegisterActivity.this.registerButton.setVisibility(View.VISIBLE);
                        RegisterActivity.this.mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    RegisterActivity.this.finish();
                                } else {
                                    String message = task.getException().getMessage();
                                    Context context = RegisterActivity.this;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Error : ");
                                    stringBuilder.append(message);
                                    Toast.makeText(context, stringBuilder.toString(),Toast.LENGTH_LONG).show();
                                }
                                RegisterActivity.this.registerProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                        return;
                    }
                    Toast.makeText(RegisterActivity.this, "Confirm Password and Password Field doesn't match.", 1).show();
                }
            }
        });
    }

    protected void onStart() {
        super.onStart();
        if (this.mAuth.getCurrentUser() != null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
