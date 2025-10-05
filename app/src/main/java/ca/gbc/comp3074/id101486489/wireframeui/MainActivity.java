package ca.gbc.comp3074.id101486489.wireframeui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.itemCasaLomaHistory).setOnClickListener(v -> openInfo(
                "Casa Loma: History",
                "An in-depth look at Casa Loma’s architectural history, its construction in 1914, and its role as one of Toronto’s most iconic landmarks."
        ));

        findViewById(R.id.itemCasaLomaFolklore).setOnClickListener(v -> openInfo(
                "Casa Loma: Folklore",
                "Legends surrounding the mysterious hallways of Casa Loma — ghost stories and hidden tunnels told through Toronto’s folklore."
        ));

        findViewById(R.id.itemGeorgeBrown).setOnClickListener(v -> openInfo(
                "George Brown College: History",
                "Founded in 1967, George Brown College has been a pillar of applied education in Toronto, connecting students with industry leaders."
        ));

        findViewById(R.id.itemBigBen).setOnClickListener(v -> openInfo(
                "Big Ben: History",
                "Discover Big Ben — the Great Bell of the Elizabeth Tower, built in 1859."
        ));
    }

    private void openInfo(String title, String info) {
        Intent intent = new Intent(MainActivity.this, InfoActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("info", info);
        startActivity(intent);
    }
}