package com.example.doan;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.ImageView;
public class MainActivity extends AppCompatActivity {

    ImageView imgDat;
    int mn;
    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private RadioGroup mModeRadioGroup;
    private RadioButton mTuDongRadioButton;
    private RadioButton mThuCongRadioButton;
    private Button mOnButton;
    private Button mOffButton;
    private TextView mTrangThaiTextView;
    private TextView mMucNuocTextView;
    private TextView mtemperature_textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kết nối tới Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("mayBom");
        // Ánh xạ views
        mModeRadioGroup = findViewById(R.id.modeRadioGroup);
        mTuDongRadioButton = findViewById(R.id.tuDongRadioButton);
        mThuCongRadioButton = findViewById(R.id.thuCongRadioButton);
        mOnButton = findViewById(R.id.onButton);
        mOffButton = findViewById(R.id.offButton);
        mTrangThaiTextView = findViewById(R.id.trangThaiTextView);
        mMucNuocTextView = findViewById(R.id.mucNuocTextView);
        mtemperature_textview= findViewById(R.id.temperature_textview);
        imgDat = findViewById(R.id.imgData);
        // Đặt sự kiện khi chọn chế độ
        mModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.tuDongRadioButton) {
                    setMode("tuDong");
                } else if (i == R.id.thuCongRadioButton) {
                    setMode("thuCong");
                }
            }
        });

        // Đặt sự kiện khi nhấn nút Bật
        mOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMayBom(true);
            }
        });

        // Đặt sự kiện khi nhấn nút Tắt
        mOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMayBom(false);

            }
        });

        // Đọc dữ liệu từ Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu máy bơm
                Boolean mayBom = dataSnapshot.child("mayBom").getValue(Boolean.class);
                if (mayBom != null) {
                    // Cập nhật trạng thái máy bơm
                    if (mayBom) {
                        mTrangThaiTextView.setText("Trạng thái: Bật");
                    } else {
                        mTrangThaiTextView.setText("Trạng thái: Tắt");
                    }
                }
                Float temperature = dataSnapshot.child("temp").getValue(Float.class);
                if (temperature != null) {
                    // Cập nhật nhiệt độ


                    mtemperature_textview.setText("Nhiệt độ: " + temperature+ " Độ C");
                }


                // Lấy dữ liệu mức nước
                Float mucNuoc = dataSnapshot.child("status").getValue(Float.class);
                int mucNuocInt = (int) mucNuoc.floatValue();
                int mn = (20 - (mucNuocInt+7))*10;
                if (mucNuoc != null) {
                    // Cập nhật mức nước


                    mMucNuocTextView.setText("Mức nước: " + Integer.toString(mn) + " %");
                }

                if (mn >= 100)
                {
                    imgDat.setImageResource(R.drawable.anh13);
                    mMucNuocTextView.setText("Mức nước: 100% "   );
                }
                else if (mn >= 90)
                {
                    imgDat.setImageResource(R.drawable.anh12);
                }
                else if (mn >= 80)
                {
                    imgDat.setImageResource(R.drawable.anh11);
                }

                else if (mn >= 70)
                {
                    imgDat.setImageResource(R.drawable.anh9);
                }

                else if (mn >= 60)
                {
                    imgDat.setImageResource(R.drawable.anh7);
                }

                else if (mn >= 50)
                {
                    imgDat.setImageResource(R.drawable.anh6);
                }

                else if (mn >= 40)
                {
                    imgDat.setImageResource(R.drawable.anh5);
                }

                else if (mn >= 30 )
                {
                    imgDat.setImageResource(R.drawable.anh4);
                }

                else if (mn >=20)
                {
                    imgDat.setImageResource(R.drawable.anh3);
                }

                else if (mn >= 10)
                {
                    imgDat.setImageResource(R.drawable.anh2);

                }
                else if (mn <= 0)
                {
                    imgDat.setImageResource(R.drawable.anh0);
                    mMucNuocTextView.setText("Mức nước: 0% "   );
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // Đặt chế độ cho máy bơm
    private void setMode(String mode) {
        mDatabase.child("mode").setValue(mode);
        mDatabase.child("mode_text").setValue("Chế độ: " + mode);
    }

    // Đặt trạng thái máy bơm (Bật/Tắt)
    private void setMayBom(Boolean mayBom) {
        // Gửi lệnh điều khiển máy bơm lên Firebase
        mDatabase.child("mayBom").setValue(mayBom);

        // Cập nhật trạng thái máy bơm trên TextView
        if (mayBom) {
            mTrangThaiTextView.setText("Trạng thái: Bật");
            mDatabase.child("mayBom_text").setValue("Trạng thái: Bật");
        } else {
            mTrangThaiTextView.setText("Trạng thái: Tắt");
            mDatabase.child("mayBom_text").setValue("Trạng thái: Tắt");
        }
    }

    // Đặt mức nước
    private void setMucNuoc(Float mucNuoc) {
        mDatabase.child("mucNuoc").setValue(mucNuoc);
    }
}
