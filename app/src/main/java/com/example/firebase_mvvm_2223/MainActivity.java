package com.example.firebase_mvvm_2223;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText mEtNom, mEtIngredients, mEtPreu;
    private Button mBtnAfegir, mBtnActualitzar, mBtnEsborrar;
    private FirebaseDatabase mDataBase;
    private DatabaseReference mReference;

    private ListView mLvCarta;
    private List<Pizza> mllistaPizzes = new ArrayList<>();
    private ArrayAdapter<Pizza> mAdaptarPizzes;
    private Pizza mPizzaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InicialitzarComponents();
        InicialitzarListeners();
        LlistarPizzes();

    }

    private void LlistarPizzes() {

        mReference.child("Pizzes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mllistaPizzes.clear();

                //Omplim la nostre llista de Pizzes a partir del snapshot de Firebase.
                for (DataSnapshot pizzaActual : snapshot.getChildren()) {

                    Pizza pizza = pizzaActual.getValue(Pizza.class);
                    mllistaPizzes.add(pizza);
                }

                //Passem la llista de pizzes al component ListView de la pantalla.
                mAdaptarPizzes = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, mllistaPizzes);
                mLvCarta.setAdapter(mAdaptarPizzes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InicialitzarListeners() {

        mBtnAfegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AfegirPizza();

            }
        });

        mBtnActualitzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActualitzarPizza();

            }
        });

        mBtnEsborrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EsborrarPizza();

            }
        });

        mLvCarta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPizzaSeleccionada = (Pizza) adapterView.getItemAtPosition(i);
                mEtNom.setText(mPizzaSeleccionada.getNom());
                mEtIngredients.setText(mPizzaSeleccionada.getIngredients());
                mEtPreu.setText(mPizzaSeleccionada.getPreu());
            }
        });
    }

    private void EsborrarPizza() {
        mReference.child("Pizzas").child(mPizzaSeleccionada.getUid()).removeValue();
    }

    private void ActualitzarPizza() {
    }

    private void AfegirPizza() {

        String nom = mEtNom.getText().toString();
        String ingredients = mEtIngredients.getText().toString();
        String preu = mEtPreu.getText().toString();
        String uid = UUID.randomUUID().toString();


        Pizza pizza = new Pizza(nom, ingredients, preu, uid);

        mReference.child("Pizzes").child(uid).setValue(pizza);

        mReference.child("Pizzes").child("Especials").child(uid).setValue(pizza);
    }

    private void InicialitzarComponents() {

        mEtNom = findViewById(R.id.ET_Nom);
        mEtIngredients = findViewById(R.id.ET_Ingredients);
        mEtPreu = findViewById(R.id.ET_Preu);
        mBtnAfegir = findViewById(R.id.BT_Afegir);
        mBtnActualitzar = findViewById(R.id.BT_Actualitzar);
        mBtnEsborrar = findViewById(R.id.BT_Esborrar);

        mLvCarta = findViewById(R.id.LV_Carta);

        mDataBase = FirebaseDatabase.getInstance("https://fir-npc-default-rtdb.europe-west1.firebasedatabase.app/");
        mReference = mDataBase.getReference();
    }
}