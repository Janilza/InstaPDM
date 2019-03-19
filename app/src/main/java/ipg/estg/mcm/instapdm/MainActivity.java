package ipg.estg.mcm.instapdm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mainToolbar;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);
    }

    public void onStart() {
        super.onStart();
        this.mAuth = FirebaseAuth.getInstance();
        this.mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(this.mainToolbar);
        ((ActionBar) Objects.requireNonNull(getSupportActionBar())).setTitle((CharSequence) "My Steps");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.actionLogout) {
            return false;
        }
        logOut();
        return true;
    }

    private void logOut() {
        this.mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
