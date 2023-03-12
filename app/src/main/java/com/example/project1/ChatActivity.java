package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private final String CHAT_URL = "https://express-messages-api.onrender.com" ;
    private String content;
    private List<ChatMessage> chatMessages;
    private ChatMessage userMessage;

    private LinearLayout chatContainer;
    private EditText etUserName;
    private EditText etUserMessage;




    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );

        new Thread( this::loadUrl ).start();

        this.chatContainer = findViewById( R.id.chat_container );
        this.etUserName = findViewById( R.id.et_chat_user_name );
        this.etUserMessage = findViewById( R.id.et_chat_message );

        findViewById(R.id.btn_chat_send).setOnClickListener( this::sendButtonClick );
    }

    private void sendButtonClick( View view ){

        String author = this.etUserName.getText().toString();
        if( author.isEmpty() ) {
            Toast.makeText( this, "Enter author name", Toast.LENGTH_SHORT ).show();
            this.etUserName.requestFocus();
            return;
        }

        String messageText = this.etUserMessage.getText().toString();
        if( messageText.isEmpty() ){
            Toast.makeText( this, "Enter message text", Toast.LENGTH_SHORT ).show();
            this.etUserMessage.requestFocus();
            return;
        }

        this.userMessage = new ChatMessage();
        this.userMessage.setAuthor( author );
        this.userMessage.setText( messageText );

        new Thread( this::postUserMessage ).start();
    }

    private void postUserMessage() {
        try {

            URL chatUrl = new URL( CHAT_URL );
            HttpURLConnection connection = ( HttpURLConnection ) chatUrl.openConnection();
            connection.setDoOutput( true );
            connection.setDoInput( true ) ;
            connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Content-Type", "application/json" );
            connection.setRequestProperty( "Accept", "*/*" );
            connection.setChunkedStreamingMode( 0 );

            OutputStream body = connection.getOutputStream();
            body.write( userMessage.toJsonString().getBytes() );
            body.flush();
            body.close();

            int responseCode = connection.getResponseCode();
            if( responseCode >= 400 ) {
                Log.d( "postUserMessage", "Request fails with code " + responseCode );
                return;
            }

            InputStream response = connection.getInputStream();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int len;
            while( ( len = response.read( chunk ) ) != -1 ){
                bytes.write( chunk, 0, len );
            }

            String responseBody = new String( bytes.toByteArray(), StandardCharsets.UTF_8 );
            Log.i( "postUserMessage", responseBody );


            bytes.close();
            response.close();
            connection.disconnect();

            new Thread( this::loadUrl ).start();

        }catch ( Exception ex ) {

            Log.d( "postUserMessage", ex.getMessage() );

        }
    }


    private void loadUrl() {
        try(InputStream urlStream = new URL(this.CHAT_URL).openStream()){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int len;
            while( ( len = urlStream.read( chunk ) ) != -1 ){
                bytes.write( chunk, 0, len );
            }
            this.content = new String( bytes.toByteArray(), StandardCharsets.UTF_8 );
            bytes.close();

            new Thread(this::parseContent).start();
        }
        catch (MalformedURLException ex){
            Log.d("loadUrl", "MalformedURLException: " + ex.getMessage());
        }
        catch (IOException ex){
            Log.d("loadUrl", "IOException: " + ex.getMessage());
        }
        catch (Exception ex){
            Log.d("loadUrl", "Exception: " + ex.getMessage());
        }
    }

    private void showContent1(){
        LinearLayout ratesContainer = findViewById(R.id.chat_container);

        Drawable otherBg = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_l);
        Drawable myBg = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_r);


        LinearLayout.LayoutParams rateLParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rateLParams.setMargins(10,7,10,7);

        LinearLayout.LayoutParams rateRParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rateRParams.setMargins(10,7,10,7);
        rateRParams.gravity = Gravity.END;

        for(ChatMessage message: this.chatMessages){

            TextView tvRate = new TextView(this);


            if(message.getAuthor() == this.etUserName.getText().toString()){
                tvRate.setTextSize(18);
                tvRate.setText( message.getAuthor() + ": " + message.getText() );
                tvRate.setBackground(otherBg);
                tvRate.setPadding(15,5,15,5);
                tvRate.setLayoutParams(rateLParams);
                ratesContainer.addView(tvRate);
            }
            else{
                tvRate.setTextSize(18);
                tvRate.setText( message.getAuthor() + ": " + message.getText() );
                tvRate.setBackground(myBg);
                tvRate.setPadding(15,5,15,5);
                tvRate.setLayoutParams(rateRParams);
                ratesContainer.addView(tvRate);
            }


        }
    }
    private void showContent(){
        String author = this.etUserName.getText().toString();

        Drawable otherBg = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_l);
        Drawable myBg = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_r);

        LinearLayout.LayoutParams otherParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        otherParams.setMargins(10,7,10,7);

        LinearLayout.LayoutParams myParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        myParams.setMargins(10,7,10,7);
        myParams.gravity = Gravity.END;

        for( ChatMessage chatMessage : this.chatMessages ) {
            TextView tvMessage = new TextView( this );
            tvMessage.setText(String.format( "[%s]\n%s",
                    chatMessage.getAuthor(),
                    chatMessage.getText()));
            tvMessage.setTextSize(18);
            tvMessage.setPadding(15,5,15,5);
            tvMessage.setLayoutParams(
                    author.equals(chatMessage.getAuthor())
                    ? myParams : otherParams
            );
            tvMessage.setBackground(
                    author.equals(chatMessage.getAuthor())
                    ? myBg : otherBg
            );
            chatContainer.addView(tvMessage);
        }
    }
    /*private void showChatMessages() {
        String author = etUserName.getText().toString();

        Drawable otherBg = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.rates_shape_l);
        Drawable myBg = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.rates_shape_r);

        LinearLayout.LayoutParams otherLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        otherLayoutParams.setMargins(10, 7, 10, 7);

        LinearLayout.LayoutParams myLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        myLayoutParams.setMargins(10, 7, 10, 7);
        myLayoutParams.gravity = Gravity.END;

        boolean needScroll = false;
        for (ChatMessage chatMessage : this.chatMessages) {
            if (chatMessage.getView() != null)
                continue;

            TextView tvMessage = new TextView(this);
            tvMessage.setText(
                    String.format("%s: %s -%n %s",
                            chatMessage.getMoment(),
                            chatMessage.getAuthor(),
                            chatMessage.getText()));
            tvMessage.setTextSize(16);
            tvMessage.setPadding(10, 5, 10, 5);
            tvMessage.setBackground(
                    author.equals(chatMessage.getAuthor())
                            ? myBg
                            : otherBg);
            tvMessage.setLayoutParams(
                    author.equals(chatMessage.getAuthor())
                            ? myLayoutParams
                            : otherLayoutParams);
            chatContainer.addView(tvMessage);
            chatMessage.setView(tvMessage);
            tvMessage.setTag(chatMessage);
            needScroll = true;
        }
        if (needScroll) {  // ознака наявності нового повідомлення
            svContainer.post(() -> svContainer.fullScroll(View.FOCUS_DOWN));
            incomingMessagePlayer.start() ;
        }
    }
     */

    private void parseContent(){
        try {
            JSONObject object = new JSONObject (this.content);

            JSONArray array = object.getJSONArray("data");

            this.chatMessages = new ArrayList<ChatMessage>();

            int len = array.length();

            for (int i = 0; i < len; ++i) {
                this.chatMessages.add( new ChatMessage( array.getJSONObject( i ) ) );
            }

        } catch (JSONException e) {
            Log.d("parseContent", "JSONException: " + e.getMessage());
            return;
        }

        runOnUiThread( this::showContent );
    }

    private static class ChatMessage{
        private UUID id;
        private String author;
        private String text;
        private Date moment;
        private UUID idReply;
        private String replyPreview;
        private static final SimpleDateFormat scanFormat =
                new SimpleDateFormat( "MMM d, yyyy KK:mm:ss a", Locale.US );

        public ChatMessage(){

        }
        public ChatMessage( JSONObject object ) throws JSONException {
            this.setId(UUID.fromString( object.getString( "id" ) ) );
            this.setAuthor( object.getString( "author" ) );
            this.setText( object.getString( "txt") );

            /*
            if( object.has("moment") ) {
                try {
                    this.setMoment(scanFormat.parse(object.getString("moment")));
                } catch (ParseException e) {
                    throw new JSONException("Date format parse error: " + object.getString("moment"));
                }
            }
             */

            if( object.has("idReply") )
                this.setIdReply( UUID.fromString( object.getString( "idReply" ) ) );
            if( object.has( "replyPreview") )
                this.setReplyPreview( object.getString( "replyPreview" ) );
        }

        public String toJsonString(){
            StringBuilder sb = new StringBuilder();
            sb.append( String.format( "{\"author\":\"%s\", \"txt\":\"%s\"", getAuthor(), getText() ) );
            if( idReply != null )
                sb.append( String.format( ", \"idReply\":\"%s\"", getIdReply() ) );

            sb.append( "}" );

            return sb.toString();
        }

        public UUID getId() {
            return id;
        }
        public void setId(UUID id) {
            this.id = id;
        }
        public String getAuthor() {
            return author;
        }
        public void setAuthor(String author) {
            this.author = author;
        }
        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
        public Date getMoment() {
            return moment;
        }
        public void setMoment(Date moment) {
            this.moment = moment;
        }
        public UUID getIdReply() {
            return idReply;
        }
        public void setIdReply(UUID idReply) {
            this.idReply = idReply;
        }
        public String getReplyPreview() {
            return replyPreview;
        }
        public void setReplyPreview(String replyPreview) {
            this.replyPreview = replyPreview;
        }
    }




}