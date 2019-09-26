package com.google.codelabs.mdc.java.shrine.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.database.DatabaseManager;
import com.google.codelabs.mdc.java.shrine.model.ImageEntry;
import com.google.codelabs.mdc.java.shrine.model.InfoEntry;
import com.google.codelabs.mdc.java.shrine.model.ProductEntry;
import com.google.codelabs.mdc.java.shrine.utils.ServerComunication;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    ProductGridFragment productGridFragment;
    String cultureName;
    String productName;
    Bundle savedInstanceState;

    public ProductActivity() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_product_activity);

        this.savedInstanceState = savedInstanceState;
        Intent intent = getIntent();
        this.cultureName = intent.getStringExtra("cultureName");
        this.productName = intent.getStringExtra("productName");
        boolean contact = intent.getBooleanExtra("contact", false);

        if (contact) {
            this.navigateTo(new ContactFragment(), false);
        }
        if (this.cultureName != null) {
            this.loadProductsInDatabase();
        } if (productName != null) {
            this.goToProduct(productName);
        }
    }

    private void loadProductsInDatabase() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Carregando...");
        progress.setCancelable(false);
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseManager db = DatabaseManager.getInstance();

                ArrayList<ProductEntry> oldProducts = db.findProducts(cultureName);
                ArrayList<ProductEntry> newProducts = ServerComunication.getInstance().requestProductList(cultureName);
                System.out.println("newProducts --> " + newProducts);
                if (newProducts != null && !newProducts.isEmpty()) {
                    db.deleteProducts(oldProducts);
                    for (ProductEntry newProduct : newProducts) {
                        if (oldProducts != null && oldProducts.contains(newProduct) && oldProducts.get(oldProducts.indexOf(newProduct)).isFavorite()) {
                            newProduct.setFavorite(true);
                        }
                        db.addProduct(newProduct);
                        if (newProduct.getInformations() != null) {
                            for (InfoEntry info : newProduct.getInformations()) {
                                info.setProduct(newProduct);
                                db.addInfo(info);
                            }
                        }
                        if (newProduct.getImages() != null) {
                            for (ImageEntry image : newProduct.getImages()) {
                                image.setProduct(newProduct);
                                db.addImage(image);
                            }
                        }
                        //System.out.println(newProduct.getImages());
                    }
                }
                if (savedInstanceState == null) {
                    productGridFragment = new ProductGridFragment().setCultureName(cultureName);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, productGridFragment).commit();
                }
                if (productName != null) {
                    goToProduct(productName);
                }
                progress.dismiss();
            }
        }).start();
    }

    public void goToProduct(String productName) {
        ProductInfoFragment productInfoFragment = new ProductInfoFragment();
        productInfoFragment.setInitialData(null, DatabaseManager.getInstance().findProductByName(productName), null);
        navigateTo(productInfoFragment, false);
    }

    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void onFavoriteClick(View view) {
        CheckBox checkBox = (CheckBox) view;
        ProductEntry product = (ProductEntry) view.getTag();
        if (checkBox.isChecked()) {
            productGridFragment.onFavoriteCheck(product);
        } else {
            productGridFragment.onFavoriteUncheck(product);
        }
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

    public void onCardClick(View view) {
        ProductEntry product = (ProductEntry) view.getTag();
        productGridFragment.onCardClick(product, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
