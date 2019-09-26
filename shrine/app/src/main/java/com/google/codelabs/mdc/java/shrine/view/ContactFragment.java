package com.google.codelabs.mdc.java.shrine.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.utils.ServerComunication;

import java.io.InputStream;
import java.util.Objects;

import static android.support.v7.app.AppCompatActivity.RESULT_OK;

public class ContactFragment extends Fragment {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText cityInput;
    private TextInputEditText descriptionInput;
    private ImageView pictureView;
    private boolean isImageTaken;
    private Bitmap imageBitmap;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shr_contact_fragment, container, false);

        MaterialButton nextButton = view.findViewById(R.id.next_button);
        MaterialButton cancelButton = view.findViewById(R.id.cancel_button);
        imageBitmap = null;
        isImageTaken = false;
        nameInput = view.findViewById(R.id.name_text_input);
        emailInput = view.findViewById(R.id.email_text_input);
        cityInput = view.findViewById(R.id.city_text_input);
        descriptionInput = view.findViewById(R.id.description_text_input);
        pictureView = view.findViewById(R.id.picture_input);
        pictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageDialog();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    sendRequest();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.CAMERA}, 0);
        }
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        return view;
    }

    private void startImageDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        myAlertDialog.setTitle("Envio de imagem");
        //myAlertDialog.setMessage("How do you want to set your picture?");
        myAlertDialog.setPositiveButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                });
        myAlertDialog.setNegativeButton("Galeria",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                });
        myAlertDialog.show();
    }

    private boolean validateForm() {
        String message = null;
        if (nameInput.getText() == null || nameInput.getText().toString().length() < 1) {
            message = "O campo nome é obrigatório.";
        } else if (cityInput.getText() == null || cityInput.getText().toString().length() < 1) {
            message = "O campo cidade/estado é obrigatório. Precisamos saber onde a sua anomalia foi encontrada para podermos te ajudar.";
        } else if (emailInput.getText() == null || emailInput.getText().toString().length() < 5 || !emailInput.getText().toString().contains("@")) {
            message = "Informe um email válido, é através dele que te daremos um retorno.";
        } else if (!isImageTaken && (descriptionInput.getText() == null || descriptionInput.getText().toString().length() < 1)) {
            message = "Você deve incluir pelo menos uma foto ou uma descrição da anomalia para que possamos te ajudar. Quanto mais informações melhor.";
        }
        if (message == null) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this.getContext()));
            builder.setTitle(message).setNeutralButton("OK", null).create().show();
            return false;
        }
    }

    private void sendRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                recieveResult(ServerComunication.getInstance().sendNewSolicitation(
                        Objects.requireNonNull(nameInput.getText()).toString(),
                        Objects.requireNonNull(emailInput.getText()).toString(),
                        Objects.requireNonNull(cityInput.getText()).toString(),
                        Objects.requireNonNull(descriptionInput.getText()).toString(),
                        imageBitmap));
            }
        }).start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data.getExtras() != null) {
            this.imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                double scale = 150 * 1.0 / imageBitmap.getWidth();
                int height = (int)(imageBitmap.getHeight() * scale);
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(imageBitmap, 150, height, true);
                this.pictureView.setImageBitmap(scaledPhoto);
                this.isImageTaken = true;
                return;
            }
        }
        if (requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = Objects.requireNonNull(getContext()).getContentResolver().openInputStream(imageUri);
                this.imageBitmap = BitmapFactory.decodeStream(imageStream);
                this.pictureView.setImageURI(imageUri);
                System.out.println("aquii");
                return;
            } catch (Exception ignored) {
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this.getContext()));
        builder.setTitle("Erro ao obter a imagem, autorize o acesso do app à camera e/ou à galeria de fotos e tente novamente.").setNeutralButton("OK", null).create().show();
    }

    public void recieveResult(final boolean result) {
        Objects.requireNonNull(this.getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result) {
                    String message = "Entraremos em contato com você pelo seu email assim que possível";
                    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    builder.setTitle(message).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Objects.requireNonNull(getActivity()).onBackPressed();
                        }
                    });
                    builder.create().show();
                } else {
                    String message = "Erro ao enviar as informações. Verifique sua conexão com a internet";
                    //String message = "Nossos servidores estão offline, desculpe o inconveniente.";
                    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    builder.setTitle(message).setNeutralButton("OK", null);
                    builder.create().show();
                }
            }
        });
    }
}