package com.example.th5_02;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private TextView callStatusTextView; // TextView để hiển thị trạng thái cuộc gọi
    private TelephonyManager telephonyManager;
    private String incomingNumber = null; // Lưu số điện thoại của cuộc gọi đến
    private boolean wasRinging = false; // Biến để theo dõi trạng thái đổ chuông
    private CallStateListener callStateListener; // Listener cho TelephonyCallback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ TextView từ layout
        callStatusTextView = findViewById(R.id.call_status_textview);

        // Yêu cầu quyền runtime
        requestPermissions();
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SEND_SMS
        };

        boolean needRequest = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                needRequest = true;
                break;
            }
        }

        if (needRequest) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
        } else {
            // Quyền đã được cấp, bắt đầu lắng nghe trạng thái cuộc gọi
            setupPhoneStateListener();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Toast.makeText(this, "Tất cả quyền đã được cấp!", Toast.LENGTH_SHORT).show();
                // Quyền đã được cấp, bắt đầu lắng nghe trạng thái cuộc gọi
                setupPhoneStateListener();
            } else {
                Toast.makeText(this, "Một số quyền bị từ chối. Ứng dụng có thể không hoạt động đúng.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupPhoneStateListener() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 (API 31) trở lên
            callStateListener = new CallStateListener();
            Executor executor = Executors.newSingleThreadExecutor();
            telephonyManager.registerTelephonyCallback(executor, callStateListener);
        } else {
            // Hỗ trợ cho các phiên bản Android cũ hơn (dưới API 31)
            // Sử dụng PhoneStateListener (deprecated)
            setupPhoneStateListenerLegacy();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private class CallStateListener extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        @Override
        public void onCallStateChanged(int state) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    // Không có cuộc gọi
                    callStatusTextView.setText("Không có cuộc gọi");
                    // Kiểm tra nếu trước đó đang đổ chuông nhưng không trả lời (cuộc gọi nhỡ)
                    if (wasRinging && incomingNumber != null && !incomingNumber.isEmpty()) {
                        sendMissedCallMessage(incomingNumber);
                        wasRinging = false; // Đặt lại trạng thái
                        incomingNumber = null; // Xóa số điện thoại
                    } else {
                        // Nếu không có số điện thoại, thông báo lỗi
                        if (wasRinging) {
                            Toast.makeText(MainActivity.this, "Không thể gửi SMS: Không lấy được số điện thoại.", Toast.LENGTH_LONG).show();
                            wasRinging = false;
                            incomingNumber = null;
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    // Có cuộc gọi đến
                    // Lưu ý: TelephonyCallback không cung cấp số điện thoại trực tiếp
                    // Bạn cần sử dụng cách khác để lấy số (ví dụ: BroadcastReceiver với Intent.ACTION_PHONE_STATE_CHANGED)
                    // Để đơn giản, tôi sẽ giả định số điện thoại được lấy từ cách khác
                    // Ở đây, tôi để trống incomingNumber và sẽ giải thích cách lấy số ở phần sau
                    wasRinging = true; // Đánh dấu rằng đang có cuộc gọi đến
                    callStatusTextView.setText("Có cuộc gọi đến");
                    Toast.makeText(MainActivity.this, "Có cuộc gọi đến", Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // Đang trong cuộc gọi (gọi đi hoặc nhận cuộc gọi)
                    callStatusTextView.setText("Đang trong cuộc gọi");
                    wasRinging = false; // Cuộc gọi đã được trả lời, không phải cuộc gọi nhỡ
                    incomingNumber = null; // Xóa số điện thoại
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void setupPhoneStateListenerLegacy() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Không có cuộc gọi
                        callStatusTextView.setText("Không có cuộc gọi");
                        // Kiểm tra nếu trước đó đang đổ chuông nhưng không trả lời (cuộc gọi nhỡ)
                        if (wasRinging && incomingNumber != null && !incomingNumber.isEmpty()) {
                            sendMissedCallMessage(incomingNumber);
                            wasRinging = false; // Đặt lại trạng thái
                            incomingNumber = null; // Xóa số điện thoại
                        } else {
                            // Nếu không có số điện thoại, thông báo lỗi
                            if (wasRinging) {
                                Toast.makeText(MainActivity.this, "Không thể gửi SMS: Không lấy được số điện thoại.", Toast.LENGTH_LONG).show();
                                wasRinging = false;
                                incomingNumber = null;
                            }
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        // Có cuộc gọi đến
                        incomingNumber = phoneNumber; // Lưu số điện thoại
                        wasRinging = true; // Đánh dấu rằng đang có cuộc gọi đến
                        callStatusTextView.setText("Có cuộc gọi đến từ: " + (phoneNumber != null ? phoneNumber : "Số không xác định"));
                        Toast.makeText(MainActivity.this, "Có cuộc gọi đến từ: " + (phoneNumber != null ? phoneNumber : "Số không xác định"), Toast.LENGTH_LONG).show();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        // Đang trong cuộc gọi (gọi đi hoặc nhận cuộc gọi)
                        callStatusTextView.setText("Đang trong cuộc gọi");
                        wasRinging = false; // Cuộc gọi đã được trả lời, không phải cuộc gọi nhỡ
                        incomingNumber = null; // Xóa số điện thoại
                        break;
                }
            }
        };

        // Bắt đầu lắng nghe trạng thái cuộc gọi
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void sendMissedCallMessage(String phoneNumber) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Tôi đang bận, hãy gọi lại sau.";
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Đã gửi SMS đến " + phoneNumber, Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "Lỗi: Quyền gửi SMS bị từ chối.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Lỗi: Số điện thoại không hợp lệ.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi gửi SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dừng lắng nghe trạng thái cuộc gọi khi Activity bị hủy
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && telephonyManager != null && callStateListener != null) {
            telephonyManager.unregisterTelephonyCallback(callStateListener);
        }
    }
}