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
        setContentView( R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        registerEmailText =  findViewById(R.id.register_email);
        registerPassText =  findViewById(R.id.register_password);
        repeatPassText =  findViewById(R.id.repeat_password);
        registerButton =  findViewById(R.id.registerButton);
        loginRegisterButton =  findViewById(R.id.registerLogin);
        registerProgressBar =  findViewById(R.id.registerProgressBar);

        loginRegisterButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        registerButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String email = registerEmailText.getText().toString();
                String pass = registerPassText.getText().toString();
                String confirm_pass = repeatPassText.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) & !TextUtils.isEmpty(confirm_pass)) {

                    if (pass.equals(confirm_pass)) {

                        registerProgressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(RegisterActivity.this, "Account created succesfuly",Toast.LENGTH_LONG).show();

                                    //sendToProfile();
                                    sendToMain();


                                } else {
                                    String message = task.getException().getMessage();
                                    Context context = RegisterActivity.this;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Error : ");
                                    stringBuilder.append(message);

                                    Toast.makeText(context, stringBuilder.toString(),Toast.LENGTH_LONG).show();
                                }
                                registerProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                        return;
                    }

                    Toast.makeText(RegisterActivity.this, "Confirm Password and Password Field doesn't match.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
           // sendToProfile();
           sendToMain();
        }
    }

    private void sendToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    private void sendToProfile(){
        startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
        finish();
    }
}
