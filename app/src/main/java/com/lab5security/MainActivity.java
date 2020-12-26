package com.lab5security;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    static final int GALLERY_REQUEST = 1;

    Uri path_to_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button button = findViewById(R.id.button1);
        Button button_encrypt = findViewById(R.id.button2);
        Button button_decrypt = findViewById(R.id.button3);

        EditText text_to_crypt = findViewById(R.id.editTextTextPersonName);

        TextView decrypted_text = findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        button_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_forencrypt = text_to_crypt.getText().toString();
                File image = new File(getPath(path_to_image));
                try {
                    Imageencrypt(text_forencrypt,image,1488);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        button_decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File image = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.jpg");
                try {
                    String decrypt_text = Imagedecrypt(image,1488);
                    decrypted_text.setText(decrypt_text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    path_to_image = imageReturnedIntent.getData();
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                }
        }
    }

    public void Imageencrypt(String message,File file,int key) throws java.io.IOException {
        byte b[] = new byte[2];

        BigInteger Abi;
        int k, k1;
        DataInputStream ins = new DataInputStream(new FileInputStream(file));
        DataOutputStream outs = new DataOutputStream(new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.jpg"  )));
        for (int c = 0; c < key; c++) {
            int ch = ins.read();
            outs.write(ch);
        }
        int len = message.length();
        //byte mess[] = new byte[2];
        char chmess[] = new char[len + 1];
        k = k1 = 0;
        for (int i = 0; i <= len; i++) {
            message.getChars(0 , len , chmess , 0);
            if (i == 0) {
                BigDecimal bd = new BigDecimal(len);
                BigInteger Blen = bd.toBigInteger();
                String Slen = Blen.toString(2);
                char Clen[] = new char[Blen.bitLength()];
                Slen.getChars(0 , Blen.bitLength() , Clen , 0);
                for (int j = 0; j <= 7; j++) {
                    if (j == 0) {
                        for (k = 0; k < 8 - Blen.bitLength(); k++) {
                            int n = ins.read(b);
                            Abi = new BigInteger(b);
                            String Aby = Abi.toString(2);
                            int Alen = Abi.bitLength();
                            if (b[0] < 0)
                                Alen++;
                            char Ach[] = new char[Alen + 1];
                            Aby.getChars(0 , Alen , Ach , 0);

                            if (b[0] == 0) {
                            } else {
                                if (Ach[Alen - 1] == '1') {
                                    if (Alen == Abi.bitLength()) {
                                        BigInteger bi = new BigInteger("1111111111111110" , 2);
                                        BigInteger big = Abi.and(bi);
                                        b = big.toByteArray();
                                    } else {
                                        BigInteger bi = new BigInteger("-1" , 2);
                                        BigInteger big = Abi.subtract(bi);
                                        b = big.toByteArray();
                                    }
                                }
                                outs.write(b);
                            }
                        }  //for loop k
                        j = j + k - 1;
                    } // if of j
                    else {
                        int n = ins.read(b);
                        Abi = new BigInteger(b);
                        String Aby = Abi.toString(2);
                        int Alen = Abi.bitLength();
                        if (b[0] < 0) Alen++;
                        char Ach[] = new char[Alen + 1];
                        Aby.getChars(0 , Alen , Ach , 0);
                        if (b[0] == 0) {
                            Alen = 1;
                        }
                        if (Clen[j - k] == '0' && Ach[Alen - 1] == '1') {
                            if (Alen == Abi.bitLength()) {
                                BigInteger bi = new BigInteger("1111111111111110" , 2);
                                BigInteger big = Abi.and(bi);
                                b = big.toByteArray();
                            } else {
                                BigInteger bi = new BigInteger("-1" , 2);
                                BigInteger big = Abi.subtract(bi);
                                b = big.toByteArray();
                            }
                        } else if (Clen[j - k] == '1' && Ach[Alen - 1] == '0') {
                            if (Alen == Abi.bitLength()) {
                                BigInteger bi = new BigInteger("1" , 2);
                                BigInteger big = Abi.add(bi);
                                b = big.toByteArray();
                            } else {
                                BigInteger bi = new BigInteger("-1" , 2);
                                BigInteger big = Abi.add(bi);
                                b = big.toByteArray();
                            }

                        }
                        outs.write(b);
                    } // end else
                } // for loop j
            } // end of if
            else {
                String slen = String.valueOf(chmess[i - 1]);
                byte blen[] = slen.getBytes();
                BigInteger Blen = new BigInteger(blen);
                String Slen = Blen.toString(2);
                char Clen[] = new char[Blen.bitLength()];
                Slen.getChars(0 , Blen.bitLength() , Clen , 0);
                for (int j = 0; j <= 7; j++) {
                    if (j == 0) {
                        for (k1 = 0; k1 < 8 - Blen.bitLength(); k1++) {
                            int n = ins.read(b);
                            Abi = new BigInteger(b);
                            String Aby = Abi.toString(2);
                            int Alen = Abi.bitLength();
                            if (b[0] < 0) Alen++;
                            char Ach[] = new char[Alen + 1];
                            Aby.getChars(0 , Alen , Ach , 0);
                            if (b[0] == 0) {
                            } else {
                                if (Ach[Alen - 1] == '1') {
                                    if (Alen == Abi.bitLength()) {
                                        BigInteger bi = new BigInteger("1111111111111110" , 2);
                                        BigInteger big = Abi.and(bi);
                                        b = big.toByteArray();
                                    } else {
                                        BigInteger bi = new BigInteger("-1" , 2);
                                        BigInteger big = Abi.subtract(bi);
                                        b = big.toByteArray();
                                    }
                                }
                            }
                            outs.write(b);
                        }  //for loop k
                        j = j + k1 - 1;
                    } // if of j
                    else {
                        int n = ins.read(b);
                        Abi = new BigInteger(b);
                        String Aby = Abi.toString(2);
                        int Alen = Abi.bitLength();
                        if (b[0] < 0) Alen++;
                        char Ach[] = new char[Alen + 1];
                        Aby.getChars(0 , Alen , Ach , 0);
                        if (b[0] == 0) {
                            Alen = 1;
                        }
                        if (Clen[j - k1] == '0' && Ach[Alen - 1] == '1') {
                            if (Alen == Abi.bitLength()) {
                                BigInteger bi = new BigInteger("1111111111111110" , 2);
                                BigInteger big = Abi.and(bi);
                                b = big.toByteArray();
                            } else {
                                BigInteger bi = new BigInteger("-1" , 2);
                                BigInteger big = Abi.subtract(bi);
                                b = big.toByteArray();
                            }
                        } else if (Clen[j - k1] == '1' && Ach[Alen - 1] == '0') {
                            if (Alen == Abi.bitLength()) {
                                BigInteger bi = new BigInteger("1" , 2);
                                BigInteger big = Abi.add(bi);
                                b = big.toByteArray();
                            } else {
                                BigInteger bi = new BigInteger("-1" , 2);
                                BigInteger big = Abi.add(bi);
                                b = big.toByteArray();
                            }
                        }
                        outs.write(b);
                    } // end else
                } // for loop j
            } // end of else
        }
    }

    public String Imagedecrypt(File filename,int key)throws java.io.IOException {
        FileInputStream ins=new FileInputStream(filename);
        byte b[]=new byte[2];
        BigInteger bb1;
        char mess[]=new char[8];
        int c=0;
        for(int i=0;i<key;i++) {
            int n=ins.read();
        }
        for(int i=0;i<8;i++) {
            ins.read(b);
            bb1=new BigInteger(b);
            String str=bb1.toString(2);
            int len=bb1.bitLength();
            if(b[0]<0)
                len++;
            char ch[]=new char[len+1];
            str.getChars(0,len,ch,0);
            if(b[0]==0)
                mess[i]='0';
            else
                mess[i]=ch[len-1];
        }
        String dd=new String(mess);
        BigInteger bb=new BigInteger(dd,2);
        String s=bb.toString(2);
        int l=bb.intValue();

        char me[]=new char[l];
        int count=0;

        for(int m=0;m<l;m++) {
            for(int i=0;i<8;i++) {
                ins.read(b);
                bb1=new BigInteger(b);
                String str=bb1.toString(2);
                int len=bb1.bitLength();
                if(b[0]<0)
                    len++;
                char ch[]=new char[len+1];
                str.getChars(0,len,ch,0);
                if(b[0]==0)
                    mess[i]='0';
                else
                    mess[i]=ch[len-1];
            }
            String dd1=new String(mess);
            BigInteger bb2=new BigInteger(dd1,2);
            String s1=bb2.toString(2);
            int l1=bb2.intValue();
            me[count]=(char)l1;
            count++;
        }
        String message=new String(me);
        ins.close();
        return message;
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
}