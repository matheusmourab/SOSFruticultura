package com.google.codelabs.mdc.java.shrine.view;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.database.DatabaseManager;
import com.google.codelabs.mdc.java.shrine.model.InfoEntry;
import com.google.codelabs.mdc.java.shrine.model.ProductEntry;
import com.google.codelabs.mdc.java.shrine.utils.listeners.NavigationIconClickListener;
import com.google.codelabs.mdc.java.shrine.utils.adapters.InfoRecyclerViewAdapter;
import com.google.codelabs.mdc.java.shrine.utils.adapters.ProductCardRecyclerViewAdapter;
import com.google.codelabs.mdc.java.shrine.utils.decoration.GridItemDecoration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ProductGridFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String DEFICIENCIA_NUTRICIONAL = "deficiência nutricional";
    public static final String PRAGA = "praga";
    public static final String DOENCA = "doênça";

    private String cultureName = null;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private GridItemDecoration productDecoration;
    private GridItemDecoration infoDecoration;
    private ProductCardRecyclerViewAdapter productAdapter;
    private InfoRecyclerViewAdapter infoAdapter;
    private Toolbar toolbar;
    private ArrayList<InfoEntry> infoList;
    private ArrayList<ProductEntry> productList;
    private ArrayList<ProductEntry> productFavoriteList;
    private boolean wasNeverOpenedBefore = true;
    private View view;
    private MenuItem actualItemMenu;
    private MenuItem searchItemMenu;
    private SearchView searchView;
    private NestedScrollView productGrid;
    private NavigationIconClickListener navigationIconClickListener;
    private BottomNavigationView navBar;
    private DatabaseManager db;
    private Drawable openMenuIcon;

    public ProductGridFragment () {
        db = DatabaseManager.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (wasNeverOpenedBefore) {
            view = inflater.inflate(R.layout.shr_product_grid_fragment, container, false);
            navBar = view.findViewById(R.id.bottom_navigation);
            FloatingActionButton cameraButton = view.findViewById(R.id.camera_buttom);
            MaterialButton pragasButton = view.findViewById(R.id.pragas_button);
            MaterialButton doencasButton = view.findViewById(R.id.doencas_button);
            MaterialButton deficienciasNutricionaisButton = view.findViewById(R.id.deficiencias_nutricionais_button);
            MaterialButton allButton = view.findViewById(R.id.all_button);
            productGrid = view.findViewById(R.id.product_grid);
            infoList = db.findGeneralInfos(this.cultureName);
            productList = db.findProducts(cultureName);
            productFavoriteList = db.findFavoriteProducts(cultureName);

            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
            productDecoration = new GridItemDecoration(16, 4);
            infoDecoration = new GridItemDecoration(0, 0);
            productAdapter = new ProductCardRecyclerViewAdapter(productList);
            infoAdapter = new InfoRecyclerViewAdapter(this.getContext(), infoList);

            pragasButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigationIconClickListener.closeBackdrop();
                    recyclerView.setAdapter(productAdapter.setList(filter(PRAGA)));
                }
            });
            doencasButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigationIconClickListener.closeBackdrop();
                    recyclerView.setAdapter(productAdapter.setList(filter(DOENCA)));
                }
            });
            deficienciasNutricionaisButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigationIconClickListener.closeBackdrop();
                    recyclerView.setAdapter(productAdapter.setList(filter(DEFICIENCIA_NUTRICIONAL)));
                }
            });
            allButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigationIconClickListener.closeBackdrop();
                    recyclerView.setAdapter(productAdapter.setList(filter(null)));
                }
            });
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.DialogStyle);
                    builder.setTitle("Fale conosco").setMessage(getString(R.string.shr_contact_label)).setNegativeButton("Cancelar", null).setPositiveButton("Próximo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((ProductActivity) Objects.requireNonNull(getActivity())).navigateTo(new ContactFragment(), true);
                        }
                    });
                    builder.create().show();
                }
            });

            navBar.setOnNavigationItemSelectedListener(this);
            setUpToolbar(view);

            this.recyclerView = view.findViewById(R.id.recycler_view);
            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.addItemDecoration(productDecoration, 0);
            MenuItem menuItem = navBar.getMenu().getItem(1);
            this.onNavigationItemSelected(menuItem);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.findViewById(R.id.product_grid).setBackground(Objects.requireNonNull(getContext()).getDrawable(R.drawable.shr_product_grid_background_shape));
            }

            this.wasNeverOpenedBefore = false;
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

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
                    recyclerView.setAdapter(new InfoRecyclerViewAdapter(getContext(), filteredList));
                } else {
                    ArrayList<ProductEntry> filteredList = new ArrayList<>();
                    ArrayList<ProductEntry> generalList = new ArrayList<>();
                    if (actualItemMenu.getItemId() == R.id.favorite) {
                        generalList = productFavoriteList;
                    } else if (actualItemMenu.getItemId() == R.id.home) {
                        generalList = productList;
                    }
                    for (ProductEntry product : generalList) {
                        if (product.getTitle().toLowerCase().contains(query)) {
                            filteredList.add(product);
                        }
                    }
                    for (ProductEntry product : productFavoriteList) {
                        Iterator<InfoEntry> it = product.getInformations().iterator();
                        while(it.hasNext()) {
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (this.searchView != null)
            this.searchView.setIconified(true);
        this.navigationIconClickListener.closeBackdrop();

        if (menuItem.getItemId() == R.id.info) {
            menuItem = navBar.getMenu().getItem(0);
            //this.toolbar.setNavigationOnClickListener(null);
            this.toolbar.setNavigationIcon(null);
            this.recyclerView.setLayoutManager(linearLayoutManager);
            this.recyclerView.setAdapter(infoAdapter);
            this.recyclerView.removeItemDecorationAt(0);
            this.recyclerView.addItemDecoration(infoDecoration, 0);
            System.out.println(infoAdapter.getList());
        } else if (menuItem.getItemId() == R.id.home) {
            menuItem = navBar.getMenu().getItem(1);
            //this.toolbar.setNavigationOnClickListener(navigationIconClickListener);
            this.toolbar.setNavigationIcon(this.openMenuIcon);
            this.recyclerView.setLayoutManager(gridLayoutManager);
            this.recyclerView.setAdapter(productAdapter.setList(this.productList));
            this.recyclerView.removeItemDecorationAt(0);
            this.recyclerView.addItemDecoration(productDecoration, 0);
        } else if (menuItem.getItemId() == R.id.favorite) {
            menuItem = navBar.getMenu().getItem(2);
            //this.toolbar.setNavigationOnClickListener(navigationIconClickListener);
            this.toolbar.setNavigationIcon(this.openMenuIcon);
            this.recyclerView.setLayoutManager(gridLayoutManager);
            this.recyclerView.setAdapter(productAdapter.setList(this.productFavoriteList));
            this.recyclerView.removeItemDecorationAt(0);
            this.recyclerView.addItemDecoration(productDecoration, 0);
        }
        if (searchItemMenu != null) {
            MenuItemCompat.collapseActionView(searchItemMenu);
        }
        this.actualItemMenu = menuItem;
        menuItem.setChecked(true);
        return true;
    }

    public ProductGridFragment setCultureName(String cultureName) {
        this.cultureName = cultureName;
        return this;
    }

    private void setUpToolbar(View view) {
        this.toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        this.openMenuIcon = Objects.requireNonNull(getContext()).getDrawable(R.drawable.shr_menu);
        this.navigationIconClickListener = new NavigationIconClickListener(
                getContext(),
                this.productGrid,
                new AccelerateDecelerateInterpolator(),
                this.openMenuIcon,
                getContext().getDrawable(R.drawable.shr_close_menu));
        toolbar.setNavigationOnClickListener(navigationIconClickListener);
    }

    public void onFavoriteCheck(ProductEntry product) {
        product.setFavorite(true);
        if (!productFavoriteList.contains(product)) {
            this.productFavoriteList.add(product);
        }
        this.onNavigationItemSelected(this.actualItemMenu);
        db.updateProduct(product);
    }

    public void onFavoriteUncheck(ProductEntry product) {
        product.setFavorite(false);
        if (productFavoriteList.contains(product)) {
            this.productFavoriteList.remove(product);
        }
        this.onNavigationItemSelected(this.actualItemMenu);
        db.updateProduct(product);
    }

    public void onCardClick(ProductEntry product, boolean addToBackStack) {
        ProductInfoFragment productInfoFragment = new ProductInfoFragment();
        productInfoFragment.setInitialData(this, product, actualItemMenu);
        ((ProductActivity) Objects.requireNonNull(getActivity())).navigateTo(productInfoFragment, addToBackStack);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.onNavigationItemSelected(this.actualItemMenu);
    }

    public ArrayList<ProductEntry> filter(String type) {
        ArrayList<ProductEntry> filteredList = new ArrayList<>();
        ArrayList<ProductEntry> generalList;
        if (navBar.getMenu().findItem(R.id.favorite).isChecked()) {
            generalList = productFavoriteList;
        } else if (navBar.getMenu().findItem(R.id.home).isChecked()) {
            generalList = productList;
        } else {
            return null;
        }

        if (type == null) {
            return generalList;
        }
        for (ProductEntry product : generalList) {
            if (product.getType().toLowerCase().equals(type)) {
                filteredList.add(product);
            }
        }
        return filteredList;
    }
}
