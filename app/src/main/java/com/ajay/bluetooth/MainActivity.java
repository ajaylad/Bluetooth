package com.ajay.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final int SPEECH_REQUEST_CODE = 1;
    private GestureDetector gestureDetector;
    public ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    TextView text;
    boolean bt = false;
    private TextToSpeech TTS;
//    private GestureDetector gestureDetector;
    TextToSpeech speaker;
    private static final int SWIPE_THRESHOLD =100 ;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    int pos = 0;

    //BluetoothHeadset bluetoothHeadset;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mdevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (mdevice.getBondState() == BluetoothDevice.BOND_BONDED){

                }
                if (mdevice.getBondState() == BluetoothDevice.BOND_BONDING){

                }if (mdevice.getBondState() == BluetoothDevice.BOND_NONE){

                }



            }
        }
    };

    BluetoothHeadset bluetoothHeadset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.textView);

        IntentFilter filter =  new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver,filter);

        this.gestureDetector = new GestureDetector(this,this);
        gestureDetector.setOnDoubleTapListener(this);



        speaker =new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speaker.setLanguage(Locale.ENGLISH);

                    speaker.speak("Swipe left and right to check for paired devices",TextToSpeech.QUEUE_ADD,null);
                    speaker.speak("Double tap to connect",TextToSpeech.QUEUE_ADD,null);
                    speaker.speak("To turn off bluetooth long press",TextToSpeech.QUEUE_ADD,null);
                    speaker.speak(btDevices.get(pos).getName(),TextToSpeech.QUEUE_ADD,null);

                }
            }
        });


// Get the default adapter


        if (bluetoothAdapter == null){
            Log.d("bt", "not supported");
        }

        if (!bluetoothAdapter.isEnabled()){
            Log.d("in enabled","in enabled");
            BluetoothAdapter.getDefaultAdapter().enable();
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice p : pairedDevices){
            Log.d("loop", p.getName());
            btDevices.add(p);
        }

        text.setText(btDevices.get(pos).getName());
        speaker.speak(btDevices.get(pos).getName(),TextToSpeech.QUEUE_FLUSH,null);

//        Log.d("123456789987654321","");


//        Log.d("paired",""+pairedDevices);
//        List<String> s = new ArrayList<String>();
//        for(BluetoothDevice bt : pairedDevices)
//            s.add(bt.getName());
//
//        for (int i=0; i<s.size();i++){
//
//            String str = s.get(i);
//            Log.d("paired", str);
//        }

//        text.setText(s.get(pos));



    }



    private void speak(String double_tap_to_pair) {
    }

    public void conn(View view){
        btDevices.get(0).createBond();
    }


//    private void displaySpeechRecognizer(int code) {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        // Start the activity, the intent will be populated with the speech text
//        startActivityForResult(intent, code);
//    }

    //     This callback is invoked when the Speech Recognizer returns.
//     This is where you process the intent and extract the speech text from the intent.



    @Override
    public void onInit(int status) {

    }

    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        try {
            btDevices.get(pos).createBond();
            speaker.speak("Connected to " + btDevices.get(pos).getName(),TextToSpeech.QUEUE_FLUSH,null);
        }catch (Exception q){
            speaker.speak("Bluetooth device not connected",TextToSpeech.QUEUE_FLUSH,null);
        }

        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

        BluetoothAdapter.getDefaultAdapter().disable();
        finish();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;


    }

    private void onSwipeRight() {

        pos--;
        if (pos < 0){
            pos = btDevices.size()-1;
        }
        text.setText(btDevices.get(pos).getName());
        speaker.speak(btDevices.get(pos).getName(),TextToSpeech.QUEUE_FLUSH,null);

    }

    private void onSwipeLeft() {

        pos++;
        if (pos >= btDevices.size()){
            pos = 0;
        }
        text.setText(btDevices.get(pos).getName());
        speaker.speak(btDevices.get(pos).getName(),TextToSpeech.QUEUE_FLUSH,null);

    }

    private void onSwipeBottom() {
    }

    private void onSwipeTop() {
    }



}
