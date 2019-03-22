package ipg.estg.mcm.instapdm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar profileToolbar;
    //private CircleImageView profileImage;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_profile);

       /* profileImage = findViewById(R.id.profile_image);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //know the version of the sdk, for ask for permission or not > Marsmallow
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    //user dont have permission
                    if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                        //ask the user for the permission to access the storage
                        Toast.makeText(ProfileActivity.this, "Permission denied", Toast.LENGTH_LONG);
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }else{

                        //Toast.makeText(ProfileActivity.this, "You already have permission", Toast.LENGTH_LONG);
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(ProfileActivity.this);



                    }

                }
            }
        });*/
    }
    protected void onStart() {
        super.onStart();

        profileToolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(this.profileToolbar);
        (Objects.requireNonNull(getSupportActionBar())).setTitle("Perfil");
    }
}
