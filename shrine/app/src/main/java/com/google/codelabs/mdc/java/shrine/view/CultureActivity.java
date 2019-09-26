package com.google.codelabs.mdc.java.shrine.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.model.CultureEntry;
import com.google.codelabs.mdc.java.shrine.database.DatabaseManager;
import com.google.codelabs.mdc.java.shrine.model.ImageEntry;
import com.google.codelabs.mdc.java.shrine.model.InfoEntry;
import com.google.codelabs.mdc.java.shrine.model.ProductEntry;
import com.google.codelabs.mdc.java.shrine.utils.ImageRequester;
import com.google.codelabs.mdc.java.shrine.utils.ServerComunication;
import com.google.codelabs.mdc.java.shrine.utils.adapters.CultureCardRecyclerViewAdapter;
import com.google.codelabs.mdc.java.shrine.utils.adapters.InfoRecyclerViewAdapter;
import com.google.codelabs.mdc.java.shrine.utils.adapters.ProductCardRecyclerViewAdapter;
import com.google.codelabs.mdc.java.shrine.utils.decoration.GridItemDecoration;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class CultureActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    String productName = null;
    final Context context = this;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private GridItemDecoration cardDecoration;
    private GridItemDecoration infoDecoration;
    private CultureCardRecyclerViewAdapter cultureAdapter;
    private ProductCardRecyclerViewAdapter productAdapter;
    private InfoRecyclerViewAdapter infoAdapter;
    private ArrayList<InfoEntry> infoList;
    private ArrayList<CultureEntry> cultureList;
    private ArrayList<ProductEntry> productFavoriteList;
    private boolean wasNeverOpenedBefore = true;
    private MenuItem actualItemMenu;
    private MenuItem searchItemMenu;
    private SearchView searchView;
    private BottomNavigationView navBar;
    private DatabaseManager db;

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        // Branch init
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    productName = referringParams.optString("productName");
                    if (productName != null && !productName.equals("")) {
                        openProductActivity(null, productName, false);
                    }
                } else {
                    System.out.println("BRANCH SDK ERROR - " + error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_culture_activity);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        this.initDatabase();
        this.configLayout();
    }

    private void initDatabase() {
        DatabaseManager.init(this);
        db = DatabaseManager.getInstance();
        File dir = new File(ImageRequester.IMAGESPATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<ProductEntry> products = db.findAllProducts();
        //List<ProductEntry> newProducts = ServerComunication.getInstance().requestProductList(null);
        if (products == null || products.isEmpty()) {
            products = ProductEntry.getInitialProductList(this.getResources());
            for (ProductEntry product : products) {
                db.addProduct(product);
                if (product.getInformations() != null) {
                    for (InfoEntry info : product.getInformations()) {
                        info.setProduct(product);
                        db.addInfo(info);
                    }
                }
                if (product.getImages() != null) {
                    for (ImageEntry image : product.getImages()) {
                        image.setProduct(product);
                        db.addImage(image);
                    }
                }
            }
        }

        final ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Carregando...");
        progress.setCancelable(false);
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cultureList = db.findAllCultures();
                ArrayList<CultureEntry> newCultures = ServerComunication.getInstance().requestCultureList();
                System.out.println("newCultures --> " + newCultures);
                if ((newCultures == null || newCultures.isEmpty()) && (cultureList == null || cultureList.isEmpty())) {
                    newCultures = new ArrayList<>(CultureEntry.getInitialCultureList(getResources()));
                }
                if (newCultures != null && !newCultures.isEmpty()) {
                    db.deleteCultures(cultureList);
                    for (CultureEntry culture : newCultures) {
                        db.addCulture(culture);
                        if (culture.getImage() != null) {
                            ImageEntry image = culture.getImage();
                            image.setProduct(null);
                            db.addImage(image);
                        }
                    }
                    cultureList = newCultures;
                }

                infoList = db.findGeneralInfos(null);
                ArrayList<InfoEntry> newInfoList = ServerComunication.getInstance().requestInfoList();
                System.out.println("newInfoList --> " + newInfoList);
                if ((newInfoList == null || newInfoList.isEmpty()) && (infoList == null || !infoList.isEmpty())) {
                    newInfoList = InfoEntry.getInitialGeneralInfoList(getResources());
                    System.out.println("000000000000");
                }
                if (newInfoList != null && !newInfoList.isEmpty()) {
                    db.deleteGeneralInfos();
                    for (InfoEntry info : newInfoList) {
                        info.setProduct(null);
                        db.addInfo(info);
                    }
                    infoList = db.findGeneralInfos(null);
                }
                progress.dismiss();
            }
        }).start();

        productFavoriteList = db.findAllFavoriteProducts();
    }

    public void configLayout() {
        if (wasNeverOpenedBefore) {
            setContentView(R.layout.shr_product_grid_fragment);
            navBar = findViewById(R.id.bottom_navigation);
            FloatingActionButton cameraButton = findViewById(R.id.camera_buttom);

            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            cardDecoration = new GridItemDecoration(16, 4);
            infoDecoration = new GridItemDecoration(0, 0);
            cultureAdapter = new CultureCardRecyclerViewAdapter(cultureList);
            productAdapter = new ProductCardRecyclerViewAdapter(productFavoriteList);
            infoAdapter = new InfoRecyclerViewAdapter(this, infoList);

            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                    builder.setTitle("Fale conosco").setMessage(getString(R.string.shr_contact_label)).setNegativeButton("Cancelar", null).setPositiveButton("Próximo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openProductActivity(null, null, true);
                        }
                    });
                    builder.create().show();
                }
            });

            navBar.setOnNavigationItemSelectedListener(this);
            setUpToolbar();

            this.recyclerView = findViewById(R.id.recycler_view);
            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.addItemDecoration(cardDecoration, 0);
            MenuItem menuItem = navBar.getMenu().getItem(1);
            this.onNavigationItemSelected(menuItem);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                findViewById(R.id.product_grid).setBackground(this.getDrawable(R.drawable.shr_product_grid_background_shape));
            }

            this.wasNeverOpenedBefore = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shr_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu);

        searchItemMenu = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItemMenu);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //private MenuItem actualNavBarItem;
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String query = s.toLowerCase();
                if (actualItemMenu.getItemId() == R.id.info) {
                    ArrayList<InfoEntry> filteredList = new ArrayList<>();
                    for (InfoEntry info : infoList) {
                        if (info.getTitle().toLowerCase().contains(query)) {
                            filteredList.add(info);
                        }
                    }
                    for (InfoEntry info : infoList) {
                        if (info.getDescription().toLowerCase().contains(query) && !filteredList.contains(info)) {
                            filteredList.add(info);
                        }
                    }
                    recyclerView.setAdapter(infoAdapter.setList(filteredList));
                } else if (actualItemMenu.getItemId() == R.id.home) {
                    ArrayList<CultureEntry> filteredList = new ArrayList<>();
                    for (CultureEntry culture : cultureList) {
                        if (culture.getName().toLowerCase().contains(query)) {
                            filteredList.add(culture);
                        }
                    }
                    cultureAdapter.setList(filteredList);
                    recyclerView.setAdapter(cultureAdapter);
                } else if (actualItemMenu.getItemId() == R.id.favorite) {
                    ArrayList<ProductEntry> filteredList = new ArrayList<>();
                    for (ProductEntry product : productFavoriteList) {
                        if (product.getTitle().toLowerCase().contains(query)) {
                            filteredList.add(product);
                        }
                    }
                    for (ProductEntry product : productFavoriteList) {
                        Iterator<InfoEntry> it = product.getInformations().iterator();
                        while (it.hasNext()) {
                            InfoEntry info = it.next();
                            if (info.getDescription().toLowerCase().contains(query)) {
                                if (!filteredList.contains(product)) {
                                    filteredList.add(product);
                                }
                            }
                        }
                    }
                    recyclerView.setAdapter(productAdapter.setList(filteredList));
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (this.searchView != null)
            this.searchView.setIconified(true);
        //this.navigationIconClickListener.closeBackdrop();

        if (menuItem.getItemId() == R.id.info) {
            menuItem = navBar.getMenu().getItem(0);
            //this.toolbar.setNavigationOnClickListener(null);
            this.recyclerView.setLayoutManager(linearLayoutManager);
            this.recyclerView.setAdapter(infoAdapter.setList(infoList));
            this.recyclerView.removeItemDecorationAt(0);
            this.recyclerView.addItemDecoration(infoDecoration, 0);
        } else if (menuItem.getItemId() == R.id.home) {
            menuItem = navBar.getMenu().getItem(1);
            //this.toolbar.setNavigationOnClickListener(null);
            this.recyclerView.setLayoutManager(gridLayoutManager);
            this.recyclerView.setAdapter(cultureAdapter.setList(this.cultureList));
            this.recyclerView.removeItemDecorationAt(0);
            this.recyclerView.addItemDecoration(cardDecoration, 0);
        } else if (menuItem.getItemId() == R.id.favorite) {
            menuItem = navBar.getMenu().getItem(2);
            //this.toolbar.setNavigationOnClickListener(navigationIconClickListener);
            this.recyclerView.setLayoutManager(gridLayoutManager);
            this.recyclerView.setAdapter(productAdapter.setList(this.productFavoriteList));
            this.recyclerView.removeItemDecorationAt(0);
            this.recyclerView.addItemDecoration(cardDecoration, 0);
        }
        if (searchItemMenu != null) {
            MenuItemCompat.collapseActionView(searchItemMenu);
        }
        this.actualItemMenu = menuItem;
        menuItem.setChecked(true);
        return true;
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.app_bar);
        this.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);

        /*this.navigationIconClickListener = new NavigationIconClickListener(
                this,
                this.cultureGrid,
                new AccelerateDecelerateInterpolator(),
                this.getDrawable(R.drawable.shr_menu),
                this.getDrawable(R.drawable.shr_close_menu));
        toolbar.setNavigationOnClickListener(navigationIconClickListener);*/
    }

    public void showDescription(View view) {
        LinearLayout description = (LinearLayout) view.getTag(R.string.itemDescriptionView);
        ImageView dropDownIcon = (ImageView) view.getTag(R.string.itemTitleDropDownIconView);
        if (description.isShown()) {
            description.setVisibility(View.GONE);
            dropDownIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_down_clicked));
        } else {
            description.setVisibility(View.VISIBLE);
            dropDownIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_up));
        }
    }

    private void openProductActivity(String cultureName, String productName, boolean contact) {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("cultureName", cultureName);
        intent.putExtra("productName", productName);
        intent.putExtra("contact", contact);
        startActivity(intent);
    }

    public void onCardClick(View view) {
        Object obj = view.getTag();
        if (obj instanceof ProductEntry) {
            ProductEntry product = (ProductEntry) obj;
            openProductActivity(null, product.getTitle(), false);
        } else if (obj instanceof CultureEntry) {
            CultureEntry culture = (CultureEntry) obj;
            openProductActivity(culture.getName(), null, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        productFavoriteList = db.findAllFavoriteProducts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("---> onDestroy <---");
        DatabaseManager.getInstance().close();
    }
}
