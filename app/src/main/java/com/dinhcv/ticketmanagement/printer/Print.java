package com.dinhcv.ticketmanagement.printer;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.IPrinterPort;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.Table;
import com.android.print.sdk.util.Utils;

import java.io.UnsupportedEncodingException;

public class Print extends PrinterInstance {
    private static final long serialVersionUID = 1L;
    public static boolean DEBUG = true;
    private static String TAG = "PrinterInstance";
    private IPrinterPort myPrinter;
    private String charsetName = "gbk";
    private final String SDK_VERSION = "3.0";


    public Print(Context context, BluetoothDevice bluetoothDevice, Handler handler) {
        super(context, bluetoothDevice, handler);

    }



    public String getEncoding() {
        return this.charsetName;
    }

    public void setEncoding(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getSDK_Vesion() {
        return "3.0";
    }

    public boolean isConnected() {
        return this.myPrinter.getState() == 101;
    }

    public void openConnection() {
        this.myPrinter.open();
    }

    public void closeConnection() {
        this.myPrinter.close();
    }

    public int printText(String content) {
        byte[] data = null;

        try {
            if(this.charsetName != "") {
                data = content.getBytes(this.charsetName);
            } else {
                data = content.getBytes();
            }
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return this.sendByteData(data);
    }

    public int sendByteData(byte[] data) {
        if(data != null) {
            Utils.Log(TAG, "sendByteData length is: " + data.length);
            return this.myPrinter.write(data);
        } else {
            return -1;
        }
    }

    public int printImage(Bitmap bitmap) {
        return this.sendByteData(Utils.bitmap2PrinterBytes(bitmap, 0));
    }

    public int printImage(Bitmap bitmap, int left) {
        return this.sendByteData(Utils.bitmap2PrinterBytes(bitmap, left));
    }

    public int printImageStylus(Bitmap bitmap, int multiple) {
        return this.sendByteData(Utils.bitmap2PrinterBytes_stylus(bitmap, multiple, 0));
    }

    public int printImageStylus(Bitmap bitmap, int multiple, int left) {
        return this.sendByteData(Utils.bitmap2PrinterBytes_stylus(bitmap, multiple, left));
    }

    public int printTable(Table table) {
        return this.printText(table.getTableText());
    }

    public int printBarCode(Barcode barcode) {
        return this.sendByteData(barcode.getBarcodeData());
    }

    public void init() {
        this.setPrinter(0);
    }

    public byte[] read() {
        return this.myPrinter.read();
    }

    public boolean setPrinter(int command) {
        byte[] arrayOfByte = null;
        switch(command) {
            case 0:
                arrayOfByte = new byte[]{27, 64};
                break;
            case 1:
                arrayOfByte = new byte[]{0};
                break;
            case 2:
                arrayOfByte = new byte[]{12};
                break;
            case 3:
                arrayOfByte = new byte[]{10};
                break;
            case 4:
                arrayOfByte = new byte[]{13};
                break;
            case 5:
                arrayOfByte = new byte[]{9};
                break;
            case 6:
                arrayOfByte = new byte[]{27, 50};
        }

        this.sendByteData(arrayOfByte);
        return true;
    }

    public boolean setPrinter(int command, int value) {
        byte[] arrayOfByte = new byte[3];
        switch(command) {
            case 0:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 74;
                break;
            case 1:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 100;
                break;
            case 2:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 33;
                break;
            case 3:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 85;
                break;
            case 4:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 86;
                break;
            case 5:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 87;
                break;
            case 6:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 45;
                break;
            case 7:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 43;
                break;
            case 8:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 105;
                break;
            case 9:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 99;
                break;
            case 10:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 51;
                break;
            case 11:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 32;
            case 12:
                arrayOfByte[0] = 28;
                arrayOfByte[1] = 80;
            case 13:
                arrayOfByte[0] = 27;
                arrayOfByte[1] = 97;
                if(value > 2 || value < 0) {
                    return false;
                }
        }

        arrayOfByte[2] = (byte)value;
        this.sendByteData(arrayOfByte);
        return true;
    }

    public void setCharacterMultiple(int x, int y) {
        byte[] arrayOfByte = new byte[]{29, 33, 0};
        if(x >= 0 && x <= 7 && y >= 0 && y <= 7) {
            arrayOfByte[2] = (byte)(x * 16 + y);
            this.sendByteData(arrayOfByte);
        }

    }

    public void setLeftMargin(int nL, int nH) {
        byte[] arrayOfByte = new byte[]{29, 76, (byte)nL, (byte)nH};
        this.sendByteData(arrayOfByte);
    }

    public void setPrintModel(boolean isBold, boolean isDoubleHeight, boolean isDoubleWidth, boolean isUnderLine) {
        byte[] arrayOfByte = new byte[]{27, 33, 0};
        int a = 0;
        if(isBold) {
            a += 8;
        }

        if(isDoubleHeight) {
            a += 16;
        }

        if(isDoubleHeight) {
            a += 32;
        }

        if(isDoubleHeight) {
            a += 128;
        }

        arrayOfByte[2] = (byte)a;
        this.sendByteData(arrayOfByte);
    }

    public void cutPaper() {
        byte[] cutCommand = new byte[]{29, 86, 66, 0};
        this.sendByteData(cutCommand);
    }

    public void ringBuzzer(byte time) {
        byte[] buzzerCommand = new byte[]{29, 105, time};
        this.sendByteData(buzzerCommand);
    }

    public void openCashbox(boolean cashbox1, boolean cashbox2) {
        byte[] drawCommand;
        if(cashbox1) {
            drawCommand = new byte[]{27, 112, 0, 50, 50};
            this.sendByteData(drawCommand);
        }

        if(cashbox2) {
            drawCommand = new byte[]{27, 112, 1, 50, 50};
            this.sendByteData(drawCommand);
        }

    }
}