package com.androidatc.contentprovidertest;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editName, editNickname;
    Button buttonAdd, buttonShow, buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = (EditText) findViewById(R.id.editName);
        editNickname = (EditText) findViewById(R.id.editNickname);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonShow = (Button) findViewById(R.id.buttonShow);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editName.getText().length() > 0 &&
                        editNickname.getText().length() > 0) {
                    ContentValues values = new ContentValues();

                    values.put(NicknameProvider.NAME, editName.getText().toString());
                    values.put(NicknameProvider.NICK_NAME, editNickname.getText().toString());

                    Uri uri = getContentResolver().insert(NicknameProvider.CONTENT_URI, values);

                    Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getBaseContext(), "Please enter a name", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve records
                String URL = "content://com.androidatc.ContentProviderTest.NicknameProvider";

                Uri nicknames = Uri.parse(URL);
                Cursor c = getContentResolver().query(nicknames, null, null, null, "name");

                if (c.moveToFirst()) {
                    do{
                        Toast.makeText(getBaseContext(),
                                c.getString(c.getColumnIndex(NicknameProvider.ID)) +
                                        ", " + c.getString(c.getColumnIndex( NicknameProvider.NAME)) +
                                        ", " + c.getString(c.getColumnIndex( NicknameProvider.NICK_NAME)),
                                Toast.LENGTH_SHORT).show();
                    } while (c.moveToNext());
                }
                c.close();

            }
        });

    }
}
