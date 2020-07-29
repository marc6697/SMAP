package space.badboyin.smap;

import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import space.badboyin.smap.Activity.ScanQRCodeActivity;

public class FetchBarcodeData extends AsyncTask<String,Void,Void> {
    public static final String  EXTRA_URL="extra_url" ;
    String databarcode;
    String dataParsed="";
    String singleParsed="";

    @Override
    protected Void doInBackground(String... strings) {
        final String intent_url=getIntent().getStringExtra(EXTRA_URL);
        try{
            URL url=new URL(intent_url);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            InputStream inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while (line !=null){
                line = bufferedReader.readLine();
                databarcode=databarcode+line;
            }
            JSONArray JA=new JSONArray(databarcode);
            for (int i=0;i<JA.length();i++){
                JSONObject JO=(JSONObject) JA.get(i);
                singleParsed="Name:"+JO.get("nama_keramik")+ "\n"+
                        "jenis:"+JO.get("jenis_keramik")+ "\n";
                dataParsed=dataParsed + singleParsed;
            }

        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Intent getIntent() {
        return getIntent();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
