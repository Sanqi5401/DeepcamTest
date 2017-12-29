package com.deepcam.access;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

/**
 * Created by zsq on 17-12-29.
 */

public class DrawBaseActivity extends BaseActivity {
    protected Toolbar mscToolbar;
    protected NavigationView navigationView;
    protected DrawerLayout drawerLayout;
    protected String titleString;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mscToolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //TODO eventually remove if everything has the bar
        if (mscToolbar != null) {
            setSupportActionBar(mscToolbar);
            getSupportActionBar().setTitle(titleString != null ? titleString : "");
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_menu_white_24dp));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            drawerLayout.closeDrawers();
                            Intent intent = null;
                            switch (menuItem.getItemId()) {
                                case R.id.drawer_register:
                                    if (DrawBaseActivity.this.getClass().equals(RegisterActivity.class))
                                        return true;
                                    intent = new Intent(DrawBaseActivity.this, RegisterActivity.class);
                                    break;
//								case R.id.drawer_account:
//									intent = new Intent(BaseActivity.this, HomeActivity.class);
//									break;
                                case R.id.drawer_recognition:
                                    if (DrawBaseActivity.this.getClass().equals(CameraActivity.class))
                                        return true;
                                    intent = new Intent(DrawBaseActivity.this, CameraActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    break;
                                case R.id.drawer_help:
                                    // TODO: help activity
//                                    if (DrawBaseActivity.this.getClass().equals(ContactFormActivity.class))
//                                        return true;
//                                    intent = new Intent(DrawBaseActivity.this, ContactFormActivity.class);
                                    break;
                                case R.id.drawer_logout:
                                    Log.i("BaseActivity", "logout clicked");
                                    logout();
                                    return true;
                                case R.id.drawer_users:
                                    // todo users activity
//                                    if (DrawBaseActivity.this.getClass().equals(UsersActivity.class))
//                                        return true;
//                                    intent = new Intent(DrawBaseActivity.this, UsersActivity.class);
                                    break;
                            }
                            if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                            }
                            startActivity(intent);
                            //TODO if you leave the activity and come back later, the item is stil selected.
                            //javadoc: Return true to display the item as the selected item
                            return true;
                        }
                    });
        }
    }

    private void logout() {
        // TODO: logout
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
