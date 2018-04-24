package com.example.mtaha.testbluetoothrasppi3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;
    private LineChart mChart;
    private RandomDataGenerator rmg;


    final byte delimiter = 33;
    int readBufferPosition = 0;


    public void sendBtMsg(String msg2send) {
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
        try {

            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()) {
                mmSocket.connect();
            }

            String msg = msg2send;
            //msg += "\n";
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Empeche le clavier d'apparaitre sans le toucher de l'utlisateur
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final Handler handler = new Handler();

        final TextView myLabel = (TextView) findViewById(R.id.btResult);
        final TextView measure = (TextView) findViewById(R.id.measure);
        final Button tempButton = (Button) findViewById(R.id.transferButton);
        final Button lightOnButton = (Button) findViewById(R.id.lightOn);
        final Button lightOffButton = (Button) findViewById(R.id.lightOff);
        mChart = (LineChart) findViewById(R.id.chart);

        setupChart();
        setupAxes();
        setupData();
//       //mChart.setVisibleXRangeMaximum(10000);
//      mChart.setVisibleXRangeMinimum(100);
        setLegend();

        rmg = new RandomDataGenerator(mChart,measure);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final class workerThread implements Runnable {

            private String btMsg;

            public workerThread(String msg) {
                btMsg = msg;
            }

            public void run() {
                sendBtMsg(btMsg);
                while (!Thread.currentThread().isInterrupted()) {
                    int bytesAvailable;
                    boolean workDone = false;

                    try {

                        final InputStream mmInputStream;
                        mmInputStream = mmSocket.getInputStream();
                        bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {

                            byte[] packetBytes = new byte[bytesAvailable];
                            Log.e("Aquarium recv bt", "bytes available");
                            byte[] readBuffer = new byte[1024];
                            mmInputStream.read(packetBytes);

                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    //The variable data now contains our full command
                                    handler.post(new Runnable() {
                                        public void run() {
                                            myLabel.setText(data);
                                        }
                                    });

                                    workDone = true;
                                    break;


                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }

                            if (workDone == true) {
                                mmSocket.close();
                                break;
                            }

                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }
        ;


        // start temp button handler

        tempButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                (new Thread(new workerThread("temp"))).start();

            }
        });


        //end temp button handler

        //start light on button handler
        lightOnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click
                rmg =new RandomDataGenerator(mChart,measure);
                rmg.execute();

            }
        });
        //end light on button handler

        //start light off button handler

        lightOffButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                rmg.cancel(true);

            }
        });

        // end light off button handler

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                {
                    Log.e("Aquarium", device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }


    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
    private void setupChart() {
        // disable description text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        // enable scaling
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        // set an alternative background color
        mChart.setBackgroundColor(Color.DKGRAY);

        //mChart.zoom(1,1,0,0);
    }

    private void setupAxes() {
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        //Permet d'agrandir la portee du zoom out pour plus de donnee par frame, pour une certaine
        //raison il faut setVisibleXrange JUSTE apres setAxisMinimum
        //Aucune idee pourquoi TODO: INVESTIGUER

//        xl.setAxisMinimum(-200f);
        //xl.setLabelCount(2000,true);
//        mChart.setVisibleXRangeMaximum(100);
//        mChart.setVisibleXRangeMinimum(100);
        //set interval between points in chart
       // xl.setGranularityEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(200);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Add a limit line
        LimitLine ll = new LimitLine(10, "Upper Limit");
        ll.setLineWidth(2f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll.setTextSize(10f);
        ll.setTextColor(Color.WHITE);
        // reset all limit lines to avoid overlapping lines
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
    }

    private void setupData() {
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

    }

    private void setLegend() {
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(Color.WHITE);
    }

//    private LineDataSet createSet() {
//        LineDataSet set = new LineDataSet(null, "Memory Data");
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setColors(ColorTemplate.VORDIPLOM_COLORS[0]);
//        set.setCircleColor(Color.WHITE);
//        set.setLineWidth(2f);
//        set.setCircleRadius(4f);
//        set.setValueTextColor(Color.WHITE);
//        set.setValueTextSize(10f);
//        // To show values of each point
//        set.setDrawValues(true);
//
//        return set;
//    }

//    private void addEntry(Float stat) {
//        LineData data = mChart.getData();
//
//        if (data != null) {
//            ILineDataSet set = data.getDataSetByIndex(0);
//
//            if (set == null) {
//                set = createSet();
//                data.addDataSet(set);
//            }
//
//            data.addEntry(new Entry(set.getEntryCount(), stat), 0);
//
//            // let the chart know it's data has changed
//            data.notifyDataChanged();
//            mChart.notifyDataSetChanged();
//
//            // limit the number of visible entries
//            mChart.setVisibleXRangeMaximum(15);
//
//            // move to the latest entry
//            mChart.moveViewToX(data.getEntryCount());
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //pusher.disconnect();
    }

}
