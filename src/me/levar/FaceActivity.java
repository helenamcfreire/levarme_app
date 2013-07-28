package me.levar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import me.levar.actionbar.ActionBarActivity;

public class FaceActivity extends ActionBarActivity {

    private FaceFragment faceFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            faceFragment = new FaceFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, faceFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            faceFragment = (FaceFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;

            case R.id.menu_chat:
                Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}