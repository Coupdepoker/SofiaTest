package a.b.softiatest;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public String nom="";
    public String prenom="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteDatabase dbr = dbHelper.getReadableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS Etudiant(idEtudiant integer PRIMARY KEY,nom VARCHAR,prenom VARCHAR,mail VARCHAR,convention integer REFERENCES Convention (idConvention));");
        db.execSQL("CREATE TABLE IF NOT EXISTS Convention(idConvention integer PRIMARY KEY,nom VARCHAR,nbHeur integer);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Attestation(idAttestation integer,etudiant integer REFERENCES Etudiant (idEtudiant),convention integer REFERENCES Convention (idConvention),message VARCHAR);");
        db.execSQL("INSERT INTO Convention VALUES(0,'classeA',3) ON CONFLICT DO NOTHING;");
        db.execSQL("INSERT INTO Convention VALUES(1,'classeB',5) ON CONFLICT DO NOTHING;");
        String tmp = " INSERT INTO Convention VALUES(2,'classeC',7) ON CONFLICT DO NOTHING;";
        db.execSQL(tmp);
        String[] etudiants={"Rami Aggoun","Nhu-huyen DOAN","Alain RAKOTONANAHARY","toto tot","Alain Delaet",
        "Mamadou Nang","Loubna Aggoun","Nicolas Chahine","Bertrand Baertsoen","Karim Benzema"};
        for(int i=0; i <etudiants.length; i++){
            String[] infoEtudiants = etudiants[i].split(" ");
            int idConvention = i%3;
            Cursor resultSet = dbr.rawQuery("Select * from Etudiant where idEtudiant=" + String.valueOf(i),null);
            if(resultSet==null || !resultSet.moveToFirst()){
                System.out.print("ok5");
                db.execSQL("INSERT INTO Etudiant VALUES("+String.valueOf(i)+",'"+infoEtudiants[1]+"','"+infoEtudiants[0]+"','ok@gmail.com',"+String.valueOf(idConvention)+"); ON CONFLICT DO NOTHING");
            }
        }
        TableLayout tl = (TableLayout) findViewById(R.id.tl);
        for(int i =0; i < etudiants.length; i++){
            TextView tx = new TextView(this);
            String[] infoEtudiants = etudiants[i].split(" ");
            tx.setText(etudiants[i]);
            tx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String[] infoEtudiants = tx.getText().toString().split(" ");
                    nom = infoEtudiants[1];
                    prenom = infoEtudiants[0];
                    Cursor resultSet = dbr.rawQuery("Select convention,nbHeur from Etudiant,Convention where Etudiant.nom='"+infoEtudiants[1]+"' and Etudiant.prenom='"+infoEtudiants[0]+"'and convention = idConvention ",null);
                    resultSet.moveToFirst();
                    String convention = resultSet.getString(0);
                    String nbHeur = resultSet.getString(1);
                    EditText edt = (EditText) findViewById(R.id.editText1);
                    edt.setText("Bonjour "+tx.getText()+ "\n"+
                            ",\nVous avez suivi "+nbHeur + " de formation chez FormationPlus."+
                            "\nPouvez-vous nous retourner ce mail avec la piece jointe signée."+"\n"+
                            "Cordialement,\n"+"FormationPlus"
                            );
                    EditText edt2 = (EditText) findViewById(R.id.editTextTextPersonName2);
                    edt2.setText("Convention :"+convention);


                }
            });
            tl.addView(tx);
        }

        Button addAttestation = (Button) findViewById(R.id.button1);
        addAttestation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nom != "" || prenom != "") {
                    System.out.println("ok1");
                    Cursor resultSet = dbr.rawQuery("Select idEtudiant,convention from Etudiant where nom='" + nom + "' and prenom='" + prenom + "'", null);
                    resultSet.moveToFirst();
                    String idEtud = resultSet.getString(0);
                    String convention = resultSet.getString(1);
                    EditText edt = (EditText) findViewById(R.id.editText1);
                    Cursor resultSet2 = dbr.rawQuery("Select * from Attestation,Etudiant where idEtudiant=etudiant and nom='" + nom + "' and prenom='" + prenom + "'" ,null);
                    if(resultSet2==null || !resultSet2.moveToFirst()){
                        db.execSQL("INSERT INTO Attestation VALUES(" + Integer.parseInt(idEtud) + "," + Integer.parseInt(idEtud) + "," + Integer.parseInt(convention)+ ",'" + edt.getText().toString() + "'); ON CONFLICT DO NOTHING");
                        System.out.println("ok2");
                    }
                }
            }
        });

        Button b2 = (Button) findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ok3");
                Cursor resultSet = dbr.rawQuery("Select idAttestation,etudiant,Etudiant.convention,message from Attestation,Etudiant where idEtudiant=etudiant and nom='" + nom + "' and prenom='" + prenom + "'" ,null);
                if(resultSet != null && resultSet.moveToFirst()){
                    System.out.println("ok4");
                    String idAtt = resultSet.getString(0);
                    String etudiant = resultSet.getString(1);
                    String convention= resultSet.getString(2);
                    String message = resultSet.getString(3);
                    EditText editText = (EditText) findViewById(R.id.edit2);
                    editText.setText("idAttestation: "+idAtt+
                            "\netudiant: "+ etudiant+
                            "\nconvention: "+ convention+
                            "\nmessage: "+message);
                }
            }
        });
    }

}