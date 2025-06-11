package com.example.musicstreamproject2.player;


//TODO:  Using ViewBinding to access views without findViewById.
// It provides type safety, reduces boilerplate, and avoids null pointer errors.
// it doesnt replace Intent sharing of data, you'll still need to do that.
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.musicstreamproject2.R;
import com.example.musicstreamproject2.databinding.ActivityPlayerBinding; // 🔸 Import binding

public class PlayerActivity extends AppCompatActivity {

    private ActivityPlayerBinding binding; // 🔸 Declare binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater()); // 🔸 Inflate binding
        setContentView(binding.getRoot()); // 🔸 Set the root view

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
