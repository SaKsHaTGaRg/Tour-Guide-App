package ca.gbc.comp3074.id101486489.wireframeui;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.infoTitle);
        TextView info = findViewById(R.id.infoDescription);

        title.setText(getIntent().getStringExtra("title"));
        info.setText(getIntent().getStringExtra("info"));
    }
}