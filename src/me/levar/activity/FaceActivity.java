package me.levar.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import me.levar.R;
import me.levar.actionbar.ActionBarActivity;
import me.levar.fragment.FaceFragment;
import me.levar.slidingmenu.SlideMenu;
import me.levar.slidingmenu.SlideMenuInterface;

public class FaceActivity extends ActionBarActivity implements SlideMenuInterface.OnSlideMenuItemClickListener {

    private FaceFragment faceFragment;
    private SlideMenu slidemenu;
    private final static int MYITEMID = 42;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        slidemenu = new SlideMenu(this, R.menu.slide, this, 333);
        slidemenu.init(this, R.menu.slide, this, 333);

        // set optional header image
        slidemenu.setHeaderImage(getResources().getDrawable(R.drawable.ic_launcher));

        // this demonstrates how to dynamically add menu items
        //SlideMenu.SlideMenuItem item = new SlideMenu.SlideMenuItem();
        //item.id = MYITEMID;
        //item.icon = getResources().getDrawable(R.drawable.ic_launcher);
        //item.label = "Dynamically added item";
        //slidemenu.addMenuItem(item);

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
                slidemenu.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSlideMenuItemClick(int itemId) {

        switch(itemId) {
            case R.id.item_one:
                Toast.makeText(this, "Item one selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_two:
                Toast.makeText(this, "Item two selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_three:
                Toast.makeText(this, "Item three selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_four:
                Toast.makeText(this, "Item four selected", Toast.LENGTH_SHORT).show();
                break;
            case MYITEMID:
                Toast.makeText(this, "Dynamically added item selected", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}