package com.google.codelabs.mdc.java.shrine.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.codelabs.mdc.java.shrine.utils.adapters.ImageAdapter;
import com.google.codelabs.mdc.java.shrine.utils.adapters.InfoRecyclerViewAdapter;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.model.InfoEntry;
import com.google.codelabs.mdc.java.shrine.model.ProductEntry;
import com.google.codelabs.mdc.java.shrine.utils.decoration.GridItemDecoration;

import java.util.ArrayList;
import java.util.Objects;

public class ProductInfoFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private InfoRecyclerViewAdapter infoAdapter;
    private ProductEntry product;
    private ProductGridFragment productGridFragment;
    private int actualItemMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shr_product_info_fragment, container, false);
        FloatingActionButton cameraButton = view.findViewById(R.id.camera_buttom);
        CheckBox favoriteButton = view.findViewById(R.id.favorite_button);
        ViewPager productImages = view.findViewById(R.id.product_image_info);
        BottomNavigationView navBar = view.findViewById(R.id.bottom_navigation2);
        navBar.setOnNavigationItemSelectedListener(this);
        navBar.getMenu().getItem(this.actualItemMenu).setChecked(true);

        ImageAdapter imageAdapter = new ImageAdapter(this.getContext(), this.product.getImages(), (TextView) view.findViewById(R.id.image_legend), false);
        productImages.setAdapter(imageAdapter);
        if(this.product.isFavorite()) {
            favoriteButton.setChecked(true);
        }

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
        favoriteButton.setOnCheckedChangeListener(this);

        setUpToolbar(view);

        infoAdapter = new InfoRecyclerViewAdapter(this.getContext(), new ArrayList<InfoEntry>(product.getInformations()));
        GridItemDecoration infoDecoration = new GridItemDecoration(0, 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(infoDecoration, 0);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(infoAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_info).setBackground(getContext().getDrawable(R.drawable.shr_product_grid_background_shape));
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.shr_toolbar_menu, menu);
        MenuItem shareItemMenu = menu.findItem(R.id.share);
        menu.findItem(R.id.search).setVisible(false);
        shareItemMenu.setVisible(true);
        shareItemMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Olá! Olha a " + product.getType().toLowerCase() + " que eu encontrei no Manual do Abacaxi!\n\nhttps://ps54.app.link/product-info?productName=" + product.getTitle());
                startActivity(Intent.createChooser(intent, "Compartilhe usando:"));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        toolbar.setNavigationIcon(null);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setTitle(product.getTitle());
    }

    public void setInitialData(ProductGridFragment productGridFragment, ProductEntry product, MenuItem actualItemMenu) {
        this.product = product;
        this.productGridFragment = productGridFragment;

        this.actualItemMenu = 1;
        if (actualItemMenu != null && actualItemMenu.getItemId() == R.id.favorite) {
            this.actualItemMenu = 2;
        }
        if (actualItemMenu != null) {
            actualItemMenu.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            this.productGridFragment.onFavoriteCheck(this.product);
        } else {
            this.productGridFragment.onFavoriteUncheck(this.product);
        }
    }

    public void showDescription(int position) {
        LinearLayout description = this.infoAdapter.getInfoViewHolder(position).description;
        if (description.isShown()) {
            description.setVisibility(View.GONE);
        } else {
            description.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (this.productGridFragment != null) {
            this.productGridFragment.onNavigationItemSelected(menuItem);
            this.getActivity().onBackPressed();
            return true;
        }
        return false;
    }
}
